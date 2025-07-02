package kosa.server.board.service;

import jakarta.mail.MessagingException;
import kosa.server.board.dto.request.PostCreateRequestDto;
import kosa.server.board.dto.request.PostUpdateRequestDto;
import kosa.server.board.dto.response.*;
import kosa.server.board.entity.PartyMember;
import kosa.server.board.entity.Platform;
import kosa.server.board.entity.Post;
import kosa.server.board.exception.*;
import kosa.server.board.repository.PartyMemberRepository;
import kosa.server.board.repository.PlatformRepository;
import kosa.server.board.repository.PostRepository;
import kosa.server.common.code.ErrorCode;
import kosa.server.mail.service.MailService;
import kosa.server.member.entity.Member;
import kosa.server.common.exception.MemberNotFoundException;
import kosa.server.member.repository.jpa.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;

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
                .orElseThrow(() -> new PlatformNotFoundException(ErrorCode.PLATFORM_NOT_FOUND));

        Member findMember = memberJpaRepository.findByLoginId(request.getLoginId())
                .orElseThrow(()-> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        Post createPost = Post.builder()
                .platform(createPlatform)
                .member(findMember)
                .current_count(request.getCurrentCount())
                .partySize(request.getCapacity())
                .durationMonth(request.getDurationMonth())
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

        Post postToUpdate = postRepository.findById(request.getPostId())
                .orElseThrow(()->new PostNotFoundException(ErrorCode.POST_NOT_FOUND));

        PostUpdateRequestDto.PostUpdateRequestDtoBuilder editor = postToUpdate.toEditor();
        if (request.getHostPwd() != null) {
            editor.hostPwd(request.getHostPwd());
        }
        if (request.getHostId() != null) {
            editor.hostId(request.getHostId());
        }
        // todo 프론트에서 인원수와 개월 수를 바꾸지 않는다면 0을 보내주기로
        if (request.getDurationMonth() != 0 && request.getDurationMonth() != postToUpdate.getDurationMonth()) {
            editor.durationMonth(request.getDurationMonth());
        }
        postToUpdate.edit(editor.build());
    }

    // 파티 삭제, 나가기
    public void leaveMyPost(Long postId, String loginId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND));

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

    // 파티 가입
    public void joinParty(String loginId, Long postId) {
        Member member = memberJpaRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        
        // 비관적 락으로 Post 조회
        Post post = postRepository.findByIdWithLock(postId)
                .orElseThrow(() -> new PostNotFoundException(ErrorCode.POST_NOT_FOUND));

        // 파티 정원 체크
        if (post.getCurrentCount() >= post.getPartySize()) {
            throw new PartyFullException(ErrorCode.PARTY_FULL);
        }

        // 이미 가입했는지 체크
        boolean alreadyJoined = post.getPartyMember().stream()
                .anyMatch(pm -> pm.getMember().equals(member));
        if (alreadyJoined) {
            throw new AlreadyPartyJoinedException(ErrorCode.PARTY_ALREADY_JOINED);
        }

        PartyMember partyMember = PartyMember.builder()
                .post(post)
                .member(member)
                .isOwner("N")
                .build();

        post.setCurrentCount(post.getCurrentCount() + 1);
        partyMemberRepository.save(partyMember);
    }

    // 동기 메서드: 데이터 준비
    public void prepareAndSendMail(Long postId) throws UnsupportedEncodingException, MessagingException {
        List<PartyMember> members = partyMemberRepository.findByPostId(postId);

        if (members.isEmpty()) {
            throw new PartyMemberNotFoundException(ErrorCode.PARTY_MEMBER_NOT_FOUND);
        }

        List<MailTargetResponseDto> targets = members.stream()
                .map(m -> new MailTargetResponseDto(
                        m.getMember().getEmail(),
                        m.getPost().getPlatform().getName(),
                        "http://localhost:5173/dashboard/" + postId,
                        m.getPost().getPartySize()
                ))
                .toList();
        mailService.sendMail(targets); // 비동기 메서드 호출
    }

    //만료 상태 업데이트
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
}
