package kosa.server.board.service;

import kosa.server.board.entity.PartyMember;
import kosa.server.board.entity.Platform;
import kosa.server.board.entity.Post;
import kosa.server.board.repository.PartyMemberRepository;
import kosa.server.board.repository.PlatformRepository;
import kosa.server.board.repository.PostRepository;
import kosa.server.member.entity.Member;
import kosa.server.member.entity.Role;
import kosa.server.member.enums.RoleType;
import kosa.server.board.exception.PartyFullException;
import kosa.server.member.repository.jpa.MemberJpaRepository;
import kosa.server.member.repository.jpa.RoleJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    
    @Mock
    private MemberJpaRepository memberJpaRepository;
    
    @Mock
    private PlatformRepository platformRepository;
    
    @Mock
    private PartyMemberRepository partyMemberRepository;
    
    @Mock
    private RoleJpaRepository roleJpaRepository;

    @InjectMocks
    private PostService postService;

    private Member testOwner;
    private Platform testPlatform;
    private Role userRole;

    @BeforeEach
    void setUp() {
        // Mock 데이터 준비
        userRole = Role.builder()
                .roleName(RoleType.USER.getKey())
                .build();

        testOwner = Member.builder()
                .loginId("owner123")
                .password("password")
                .nickname("파티장")
                .name("테스트유저")
                .email("owner@test.com")
                .role(userRole)
                .enabled(true)
                .build();

        testPlatform = Platform.builder()
                .name("Netflix")
                .category(1)
                .capacity(4)
                .price(BigDecimal.valueOf(15000))
                .build();
    }

    @Test
    @DisplayName("파티 정원 초과 시 PartyFullException이 발생한다")
    void joinParty_shouldThrowExceptionWhenPartyIsFull() {
        // given - 정원이 가득 찬 파티
        Post fullPost = Post.builder()
                .platform(testPlatform)
                .member(testOwner)
                .current_count(3) // 정원 가득참
                .partySize(3)     // 최대 정원 3명
                .durationMonth(1)
                .hostId("host123")
                .hostPwd("hostpwd")
                .isExpired("N")
                .build();
        ReflectionTestUtils.setField(fullPost, "id", 1L);

        Member newMember = Member.builder()
                .loginId("newuser")
                .password("password")
                .nickname("신규회원")
                .name("테스트신규")
                .email("new@test.com")
                .role(userRole)
                .enabled(true)
                .build();

        // Mock 설정
        when(memberJpaRepository.findByLoginId("newuser")).thenReturn(Optional.of(newMember));
        when(postRepository.findByIdWithLock(1L)).thenReturn(Optional.of(fullPost));

        // when & then
        assertThatThrownBy(() -> postService.joinParty("newuser", 1L))
                .isInstanceOf(PartyFullException.class);
                
        // 파티 가득참으로 인해 save 호출되지 않음
        verify(partyMemberRepository, never()).save(any(PartyMember.class));
        // currentCount 변경되지 않음
        assertThat(fullPost.getCurrentCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("정상적인 파티 가입 시 currentCount가 증가하고 PartyMember가 저장된다")
    void joinParty_shouldSucceedWithAvailableSpace() {
        // given - 여유 있는 파티
        Post availablePost = Post.builder()
                .platform(testPlatform)
                .member(testOwner)
                .current_count(1) // 파티장만 있음
                .partySize(4)     // 최대 정원 4명
                .durationMonth(1)
                .hostId("host123")
                .hostPwd("hostpwd")
                .isExpired("N")
                .build();
        ReflectionTestUtils.setField(availablePost, "id", 1L);

        Member newMember = Member.builder()
                .loginId("normaluser")
                .password("password")
                .nickname("일반회원")
                .name("테스트일반")
                .email("normal@test.com")
                .role(userRole)
                .enabled(true)
                .build();

        // Mock 설정
        when(memberJpaRepository.findByLoginId("normaluser")).thenReturn(Optional.of(newMember));
        when(postRepository.findByIdWithLock(1L)).thenReturn(Optional.of(availablePost));

        // when
        assertThatNoException().isThrownBy(() -> postService.joinParty("normaluser", 1L));

        // then
        assertThat(availablePost.getCurrentCount()).isEqualTo(2); // 파티장 + 1명
        verify(partyMemberRepository, times(1)).save(any(PartyMember.class));
    }

    @Test
    @DisplayName("비관적 락을 사용하여 Post를 조회한다")
    void joinParty_shouldUsePessimisticLock() {
        // given
        Post post = Post.builder()
                .platform(testPlatform)
                .member(testOwner)
                .current_count(1)
                .partySize(4)
                .durationMonth(1)
                .hostId("host123")
                .hostPwd("hostpwd")
                .isExpired("N")
                .build();
        ReflectionTestUtils.setField(post, "id", 1L);

        Member member = Member.builder()
                .loginId("testuser")
                .password("password")
                .nickname("테스트유저")
                .name("테스트")
                .email("test@test.com")
                .role(userRole)
                .enabled(true)
                .build();

        // Mock 설정
        when(memberJpaRepository.findByLoginId("testuser")).thenReturn(Optional.of(member));
        when(postRepository.findByIdWithLock(1L)).thenReturn(Optional.of(post));

        // when
        postService.joinParty("testuser", 1L);

        // then - 비관적 락 메서드가 호출되었는지 확인
        verify(postRepository, times(1)).findByIdWithLock(1L);
        verify(postRepository, never()).findById(1L); // 일반 findById는 호출되지 않음
    }
}