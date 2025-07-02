package kosa.server.board.service;

import kosa.server.board.entity.Platform;
import kosa.server.board.entity.Post;
import kosa.server.board.repository.PartyMemberRepository;
import kosa.server.board.repository.PlatformRepository;
import kosa.server.board.repository.PostRepository;
import kosa.server.member.entity.Member;
import kosa.server.member.entity.Role;
import kosa.server.member.enums.RoleType;
import kosa.server.member.exception.PartyFullException;
import kosa.server.member.repository.jpa.MemberJpaRepository;
import kosa.server.member.repository.jpa.RoleJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PostServiceConcurrencyTest {

    @Autowired
    private PostService postService;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private MemberJpaRepository memberJpaRepository;
    
    @Autowired
    private PlatformRepository platformRepository;
    
    @Autowired
    private PartyMemberRepository partyMemberRepository;
    
    @Autowired
    private RoleJpaRepository roleJpaRepository;

    private Member testOwner;
    private Platform testPlatform;
    private Role userRole;

    @BeforeEach
    void setUp() {
        // 실제 DB에 데이터 저장
        userRole = Role.builder()
                .roleName(RoleType.USER.getKey())
                .build();
        roleJpaRepository.save(userRole);

        testOwner = Member.builder()
                .loginId("owner123")
                .password("password")
                .nickname("파티장")
                .name("테스트유저")
                .email("owner@test.com")
                .role(userRole)
                .enabled(true)
                .build();
        memberJpaRepository.save(testOwner);

        testPlatform = Platform.builder()
                .name("Netflix")
                .category(1)
                .capacity(4)
                .price(BigDecimal.valueOf(15000))
                .build();
        platformRepository.save(testPlatform);
    }

    @Test
    @DisplayName("동시에 여러 사용자가 파티 가입을 시도할 때 비관적 락으로 정원을 초과하지 않는다")
    void joinParty_concurrencyTestWithPessimisticLock() throws InterruptedException {
        // given - 정원 3명인 파티 생성 (파티장 포함하여 2명 더 가입 가능)
        Post post = Post.builder()
                .platform(testPlatform)
                .member(testOwner)
                .current_count(1) // 파티장만 있는 상태
                .partySize(3)     // 최대 정원 3명
                .durationMonth(1)
                .hostId("host123")
                .hostPwd("hostpwd")
                .isExpired("N")
                .build();
        Post savedPost = postRepository.save(post);

        // 5명의 테스트 회원 생성
        for (int i = 1; i <= 5; i++) {
            Member member = Member.builder()
                    .loginId("user" + i)
                    .password("password")
                    .nickname("회원" + i)
                    .name("테스트" + i)
                    .email("user" + i + "@test.com")
                    .role(userRole)
                    .enabled(true)
                    .build();
            memberJpaRepository.save(member);
        }

        // when - 5명이 동시에 가입 시도
        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 1; i <= threadCount; i++) {
            final String loginId = "user" + i;
            executorService.submit(() -> {
                try {
                    postService.joinParty(loginId, savedPost.getId());
                    successCount.incrementAndGet();
                } catch (PartyFullException e) {
                    failCount.incrementAndGet();
                } catch (Exception e) {
                    // 다른 예외는 실패로 처리
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then - 비관적 락으로 정원 2명만 성공하고 나머지는 실패해야 함
        assertThat(successCount.get()).isEqualTo(2); // 파티장 제외하고 2명만 가입 성공
        assertThat(failCount.get()).isEqualTo(3);   // 나머지 3명은 실패

        // DB에서 실제 currentCount 확인
        Post updatedPost = postRepository.findById(savedPost.getId()).orElse(null);
        assertThat(updatedPost).isNotNull();
        assertThat(updatedPost.getCurrentCount()).isEqualTo(3); // 파티장 + 2명
        
        // PartyMember 테이블에서도 확인
        long memberCount = partyMemberRepository.findByPostId(savedPost.getId()).size();
        assertThat(memberCount).isEqualTo(2); // 파티장 제외하고 2명의 PartyMember
    }

    @Test
    @DisplayName("정원이 1명 남았을 때 여러 명이 동시 가입 시도하면 1명만 성공한다")
    void joinParty_onlyOneSlotLeft_shouldAllowOnlyOneUser() throws InterruptedException {
        // given - 정원 4명 중 3명이 이미 가입한 상태 (1자리만 남음)
        Post post = Post.builder()
                .platform(testPlatform)
                .member(testOwner)
                .current_count(3) // 3명 이미 가입
                .partySize(4)     // 최대 정원 4명
                .durationMonth(1)
                .hostId("host123")
                .hostPwd("hostpwd")
                .isExpired("N")
                .build();
        Post savedPost = postRepository.save(post);

        // 3명의 테스트 회원 생성
        for (int i = 1; i <= 3; i++) {
            Member member = Member.builder()
                    .loginId("competitor" + i)
                    .password("password")
                    .nickname("경쟁자" + i)
                    .name("테스트경쟁자" + i)
                    .email("competitor" + i + "@test.com")
                    .role(userRole)
                    .enabled(true)
                    .build();
            memberJpaRepository.save(member);
        }

        // when - 3명이 동시에 마지막 자리를 두고 경쟁
        int threadCount = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 1; i <= threadCount; i++) {
            final String loginId = "competitor" + i;
            executorService.submit(() -> {
                try {
                    postService.joinParty(loginId, savedPost.getId());
                    successCount.incrementAndGet();
                } catch (PartyFullException e) {
                    failCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then - 1명만 성공하고 나머지는 실패
        assertThat(successCount.get()).isEqualTo(1); // 1명만 성공
        assertThat(failCount.get()).isEqualTo(2);   // 2명은 실패

        // DB 확인
        Post updatedPost = postRepository.findById(savedPost.getId()).orElse(null);
        assertThat(updatedPost).isNotNull();
        assertThat(updatedPost.getCurrentCount()).isEqualTo(4); // 정원 가득참
        
        // PartyMember 확인
        long memberCount = partyMemberRepository.findByPostId(savedPost.getId()).size();
        assertThat(memberCount).isEqualTo(1); // 새로 가입한 1명만
    }
}