package kosa.server.board.service;

import jakarta.mail.MessagingException;
import kosa.server.board.dto.request.PostCreateRequestDto;
import kosa.server.board.dto.request.PostUpdateRequestDto;
import kosa.server.board.dto.response.*;
import kosa.server.board.entity.PartyMember;
import kosa.server.board.entity.Platform;
import kosa.server.board.entity.Post;
import kosa.server.board.repository.PartyMemberRepository;
import kosa.server.board.repository.PlatformRepository;
import kosa.server.board.repository.PostRepository;
import kosa.server.common.code.ErrorCode;
import kosa.server.mail.service.MailService;
import kosa.server.member.entity.Member;
import kosa.server.member.exception.MemberNotFoundException;
import kosa.server.member.repository.jpa.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PlatformRepository platformRepository;
    private final PartyMemberRepository partyMemberRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final MailService mailService;

    public Long create(PostCreateRequestDto request) {
        //dto를 post로 변환
        Platform createPlatform = platformRepository.findById(request.getPlatformId())
                .orElseThrow(() -> new RuntimeException("플랫폼을 찾을 수 없습니다."));

        Member findMember = memberJpaRepository.findByLoginId(request.getLoginId())
                .orElseThrow(()-> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        Post createPost = Post.builder()
                .platform(createPlatform)
                .member(findMember)
                .current_count(request.getCurrentCount())
                .partySize(request.getCapacity())
                .durationMonth(createPlatform.getMonthUnit())
                .isExpired("N")
                .build();

        PartyMember partyMember = PartyMember.builder()
                .member(findMember)
                .post(createPost)
                .isOwner("Y")
                .build();

        createPost.addPartyMember(partyMember);

        platformRepository.save(createPlatform);
        postRepository.save(createPost);
        partyMemberRepository.save(partyMember);

        return createPost.getId();
    }

    public void update(PostUpdateRequestDto request) {
        //dto로부터 수정할 Post객체 가져오기
        //Post객체 수정
        //수정한 Post객체 DB에 저장

        // todo 예외 만들기
        Post postToUpdate = postRepository.findById(request.getPostId())
                .orElseThrow(()->new IllegalArgumentException("글을 찾을 수 없습니다."));

        PostUpdateRequestDto.PostUpdateRequestDtoBuilder editor = postToUpdate.toEditor();
        if (request.getHostPwd() != null) {
            editor.hostPwd(request.getHostPwd());
        }
        if (request.getHostId() != null) {
            editor.hostId(request.getHostId());
        }
        // todo 프론트에서 인원수와 개월 수를 바꾸지 않는다면 -1을 보내주기로
        if (request.getDurationMonth() != 0 && request.getDurationMonth() != postToUpdate.getDurationMonth()) {
            editor.durationMonth(request.getDurationMonth());
            editor.limitCount(postToUpdate.getLimitCount() - 1);
        }
        postToUpdate.edit(editor.build());
    }

    // 파티원일때
    public void leaveMyPost(Long postId, String loginId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("파티가 존재하지 않습니다."));
//        Member member = memberJpaRepository.findByLoginId(loginId)
//                .orElseThrow(()->new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        Long memberId = post.getPartyMember().stream()
                .map(PartyMember::getMember)
                .filter(pm -> pm.getLoginId().equals(loginId))
                .map(Member::getId)
                .findFirst()
                .orElse(null);

        boolean isOwner = post.getPartyMember().stream()
                .anyMatch(pm -> pm.getMember().getLoginId().equals(loginId) &&
                        pm.getIsOwner().equals("Y"));

        if (isOwner) {
            // 파티장 탈퇴 -> 전체 파티 삭제
            partyMemberRepository.deleteAllByPost_Id(postId);
            postRepository.deleteById(postId);
        } else {
            // 파티원 탈퇴 -> 본인만 제거
            partyMemberRepository.deleteByPost_IdAndMember_Id(postId, memberId);

            // current_count 업데이트
            post.setCurrentCount(post.getCurrentCount() - 1);
            postRepository.save(post);
        }
    }

    public void joinParty(String loginId, Long postId) {
        Member member = memberJpaRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("멤버가 존재하지 않습니다."));
        Post post = postRepository.findById(postId)
                .orElseThrow(()->new IllegalArgumentException("방이 존재하지 않습니다."));

        PartyMember partyMember = PartyMember.builder()
                .post(post)
                .member(member)
                .isOwner("N")
                .build();

        boolean alreadyJoined = post.getPartyMember().stream()
                .anyMatch(pm -> pm.getMember().equals(member));
        if (alreadyJoined) {
            throw new IllegalArgumentException("이미 파티에 가입되어 있습니다.");
        }

        post.setCurrentCount(post.getCurrentCount() + 1);
        partyMemberRepository.save(partyMember);
    }

    // 동기 메서드: 데이터 준비
    public void prepareAndSendMail(Long postId) throws UnsupportedEncodingException, MessagingException {
        List<PartyMember> members = partyMemberRepository.findByPostId(postId);
        List<MailTargetResponseDto> targets = members.stream()
                .map(m -> new MailTargetResponseDto(
                        m.getMember().getEmail(),
                        m.getPost().getPlatform().getName(),
                        "http://localhost:8080/post/" + postId,
                        m.getPost().getPartySize()
                ))
                .toList();
        mailService.sendMail(targets); // 비동기 메서드 호출
    }

    public Page<MyPostResponseDto> readMyPost(String loginId, int page, String sortField, String sortDirection) {
        Member member = memberJpaRepository.findByLoginId(loginId)
                .orElseThrow(()->new IllegalArgumentException("회원을 찾을 수 없습니다."));

        Sort sort = getSort(sortField, sortDirection);
        Pageable pageable = PageRequest.of(page, 9, sort);
        Page<Post> posts = postRepository.findPostsByMemberId(member.getId(), pageable);

        return posts.map(post -> {
                String isOwner = post.getPartyMember()
                .stream()
                .filter(pm -> pm.getMember().getId().equals(member.getId()))
                .findFirst()
                .map(PartyMember::getIsOwner)
                .orElse("N");

                return MyPostResponseDto.builder()
                .postId(post.getId())
                .platformName(post.getPlatform().getName())
                .currentCount(post.getCurrentCount())
                .platformImageUrl(post.getPlatform().getImageUrl())
                .partySize(post.getPartySize())
                .price(post.getPlatform().getPrice().divide(BigDecimal.valueOf(post.getPartySize()), 0, BigDecimal.ROUND_HALF_UP))
                .isOwner(isOwner)
                .isExpired(post.getIsExpired())
                .createdAt(post.getCreatedAt())
                .build();
        });
    }


    public Slice<PlatformPostResponseDto> findByPlatformName(String platformName) {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Slice<Post> postByPlatformName = postRepository.findPostByPlatformName(platformName, pageRequest);

        return postByPlatformName.map(post -> PlatformPostResponseDto.builder()
                .platformName(post.getPlatform().getName())
                .partySize(post.getPartySize())
                .currentCount(post.getCurrentCount())
                .postId(post.getId())
                .isExpired(post.getIsExpired())
                .build());
    }


    public MyPostOneResponseDto selectParty(String loginId, Long postId) {
        // postId 검증
        Post posts = postRepository.findById(postId)
                .orElseThrow(()->new IllegalArgumentException("게시글이 없습니다."));

        // loginId 검증
        Member member = memberJpaRepository.findByLoginId(loginId)
                .orElseThrow(()->new IllegalArgumentException("로그인 아이디 정보가 없습니다."));

        List<PartyMemberDto> members = posts.getPartyMember()
                .stream()
                .map(partyMember -> PartyMemberDto.builder()
                        .memberId(partyMember.getMember().getId())
                        .nickName(partyMember.getMember().getNickname())
                        .isOwner(partyMember.getIsOwner())
                        .createdAt(partyMember.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        String isOwner = posts.getPartyMember()
                .stream()
                .filter(pm -> pm.getMember().getId().equals(member.getId()))
                .findFirst()
                .map(PartyMember::getIsOwner)
                .orElse("N");

        return MyPostOneResponseDto.builder()
                .postId(posts.getId())
                .platformName(posts.getPlatform().getName())
                .price(posts.getPlatform().getPrice())
                .currentCount(posts.getCurrentCount())
                .partySize(posts.getPartySize())
                .durationMonth(posts.getDurationMonth())
                .hostId(posts.getHostId())
                .hostPwd(posts.getHostPwd())
                .memberId(posts.getMember().getId())
                .isOwner(isOwner)
                .isExpired(posts.getIsExpired())
                .members(members)
                .platformImageUrl(posts.getPlatform().getImageUrl())
                .limitCount(posts.getLimitCount())
                .expirationDate(posts.getExpirationDate())
                .startDate(posts.getStartDate())
                .build();
    }

    public List<PlatformPostResponseDto> platformPostList(Long platformId) {
        List<Post> findPostList = postRepository.findByPlatformId(platformId);

        return findPostList.stream().map(post -> PlatformPostResponseDto.builder()
                .postId(post.getId())
                .leaderName(post.getMember().getName())
                .platformName(post.getPlatform().getName())
                .platformPrice(post.getPlatform().getPrice().longValue())
                .monthUnit(post.getPlatform().getMonthUnit())
                .currentCount(post.getCurrentCount())
                .partySize(post.getPartySize())
                .isExpired(post.getIsExpired())
                .createdAt(post.getCreatedAt())
                .build()).toList();
    }

    public PlatformPostNullResponseDto platformPostNull(Long platformId) {
        Platform platform = platformRepository.findById(platformId)
                .orElseThrow(()->new IllegalArgumentException("로그인 아이디 정보가 없습니다."));

        return PlatformPostNullResponseDto.builder()
                .platformName(platform.getName())
                .platformPrice(platform.getPrice())
                .build();
    }

    public Page<MyPostResponseDto> findPostsByPartyMemberLoginId(String loginId, int page, String sortField, String sortDirection) {
        Sort sort = getSort(sortField, sortDirection);
        Pageable pageable = PageRequest.of(page, 9, sort);
        Page<Post> posts = postRepository.findAllByPartyMemberLoginId(loginId, pageable);

        return posts.map(post -> {
            // isOwner 여부 확인
            String isOwner = post.getPartyMember().stream()
                    .filter(pm -> pm.getMember().getLoginId().equals(loginId))
                    .findFirst()
                    .map(PartyMember::getIsOwner)
                    .orElse("N");

            return MyPostResponseDto.builder()
                    .postId(post.getId())
                    .platformName(post.getPlatform().getName())
                    .currentCount(post.getCurrentCount())
                    .partySize(post.getPartySize())
                    .price(post.getPlatform().getPrice().divide(BigDecimal.valueOf(post.getPartySize()), 0, BigDecimal.ROUND_HALF_UP))
                    .isOwner(isOwner)
                    .isExpired(post.getIsExpired())
                    .platformImageUrl(post.getPlatform().getImageUrl())
                    .build();
        });
    }

    // 정렬 조건 생성 헬퍼 메서드
    private Sort getSort(String sortField, String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDirection.toUpperCase()).orElse(Sort.Direction.DESC);

        // 안전하게 필드 매핑 (Platform.name은 join이 필요하므로 alias 'platform.name' 으로 정렬 가능하게 쿼리 수정 필요)
        if ("platformName".equalsIgnoreCase(sortField)) {
            // platform.name 으로 정렬 (Post 엔티티에 platform.name 조인 후 정렬 가능)
            return Sort.by(direction, "platform.name");
        } else if ("createdAt".equalsIgnoreCase(sortField)) {
            return Sort.by(direction, "createdAt");
        } else {
            // 기본값
            return Sort.by(direction, "createdAt");
        }
    }

    //만료 상태 업데이트
    @Transactional
    public void updateExpiredPosts() {
        LocalDateTime today = LocalDateTime.now();
        List<Post> posts = postRepository.findAllByIsExpired("N");
        for (Post post : posts) {
            if (post.getExpirationDate() != null && !post.getExpirationDate().isAfter(today)) {
                post.expired();
            }
        }
        postRepository.saveAll(posts);
    }

    @Transactional
    public void startService(Long postId, int durationMonth) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()->new IllegalArgumentException("구독 정보가 없습니다."));

        post.startService(durationMonth);
        postRepository.save(post);
    }
}
