package kosa.server.board.service;

import kosa.server.board.dto.response.MyPostOneResponseDto;
import kosa.server.board.dto.response.MyPostResponseDto;
import kosa.server.board.dto.request.PostCreateRequestDto;
import kosa.server.board.dto.request.PostUpdateRequestDto;
import kosa.server.board.dto.response. PartyMemberDto;
import kosa.server.board.dto.response.PlatformPostResponseDto;
import kosa.server.board.entity.PartyMember;
import kosa.server.board.entity.Platform;
import kosa.server.board.entity.Post;
import kosa.server.board.repository.PartyMemberRepository;
import kosa.server.board.repository.PlatformRepository;
import kosa.server.board.repository.PostRepository;
import kosa.server.common.code.ErrorCode;
import kosa.server.member.entity.Member;
import kosa.server.member.exception.MemberNotFoundException;
import kosa.server.member.repository.jpa.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PlatformRepository platformRepository;
    private final PartyMemberRepository partyMemberRepository;
    private final MemberJpaRepository memberJpaRepository;

    public void create(PostCreateRequestDto request) {
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
                .isExpired("Y")
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
        if (request.getCapacity() != -1) {
            editor.capacity(request.getCapacity());
        }
        if (request.getDurationMonth() != -1) {
            editor.durationMonth(request.getDurationMonth());
        }
        postToUpdate.edit(editor.build());
    }

    // 파티장일때
    public void delete(Long id) {
        Post postToDelete = postRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("글을 찾을 수 없습니다."));
        postRepository.delete(postToDelete);
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

    public Page<MyPostResponseDto> readMyPost(String loginId) {
        Member member = memberJpaRepository.findByLoginId(loginId)
                .orElseThrow(()->new IllegalArgumentException("회원을 찾을 수 없습니다."));


        //몇 페이지, 몇 개, 정렬(기본은 최신순)
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findPostsByPartyMemberId(member.getId(), pageRequest);

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
                .partySize(post.getPartySize())
                .price(post.getPlatform().getPrice().divide(BigDecimal.valueOf(post.getPartySize()), 0, BigDecimal.ROUND_HALF_UP))
                .isOwner(isOwner)
                .isExpired(post.getIsExpired())
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
        Post posts = postRepository.findById(postId)
                .orElseThrow(()->new IllegalArgumentException("게시글이 없습니다."));
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
                .build()).toList();
    }
}
