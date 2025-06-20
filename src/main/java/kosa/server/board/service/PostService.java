package kosa.server.board.service;

import kosa.server.board.dto.response.MyPostOneResponseDto;
import kosa.server.board.dto.response.MyPostResponseDto;
import kosa.server.board.dto.request.PostCreateRequestDto;
import kosa.server.board.dto.request.PostUpdateRequestDto;
import kosa.server.board.dto.response.PlatformPostResponseDto;
import kosa.server.board.entity.PartyMember;
import kosa.server.board.entity.Platform;
import kosa.server.board.entity.Post;
import kosa.server.board.repository.PartyMemberRepository;
import kosa.server.board.repository.PlatformRepository;
import kosa.server.board.repository.PostRepository;
import kosa.server.member.entity.Member;
import kosa.server.member.repository.jpa.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Platform platform = platformRepository.findByName(request.getPlatformName());
        Member member = memberJpaRepository.findByLoginId(request.getLoginId())
                .orElseThrow(()-> new IllegalArgumentException("회원을 불러 올 수 없습니다."));
        Post postToCreate = Post.builder()
                .platform(platform)
                .member(member)
                .partySize(request.getCapacity())
                .durationMonth(request.getDurationMonth())
                .hostId(request.getHostId())
                .hostPwd(request.getHostPwd())
                .isExpired("N")
                .build();
        postRepository.save(postToCreate);
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

        post.getPartyMember()
                        .forEach(partyMember -> postRepository.delete(postRepository.findByPostId(postId).orElseGet(null)));
        post.setCurrentCount(post.getCurrentCount() - 1);
    }

    public void joinParty(String loginId, Long postId) {
        Member member = memberJpaRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("멤버가 존재하지 않습니다."));
        Post post = postRepository.findByPostId(postId)
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

        partyMemberRepository.save(partyMember);
        post.setCurrentCount(post.getCurrentCount() + 1);
    }

    public Page<MyPostResponseDto> readMyPost(String loginId) {
        Member member = memberJpaRepository.findByLoginId(loginId)
                .orElseThrow(()->new IllegalArgumentException("회원을 찾을 수 없습니다."));


        //몇 페이지, 몇 개, 정렬(기본은 최신순)
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findByMember_Id(member.getId(), pageRequest);


        return posts.map(post -> MyPostResponseDto.builder()
                .postId(post.getPostId())
                .platformName(post.getPlatform().getName())
                .currentCount(post.getCurrentCount())
                .partySize(post.getPartySize())
                .price(post.getPlatform().getPrice().divide(BigDecimal.valueOf(post.getPartySize()), 0, BigDecimal.ROUND_HALF_UP))
                .imageUrl(post.getPlatform().getImageUrl())
                .isOwner(post.getMember().getId().equals(member.getId()) ? "Y" : "N")
                .isExpired(post.getIsExpired())
                .build());
    }


    public Slice<PlatformPostResponseDto> findByPlatformName(String platformName) {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Slice<Post> postByPlatformName = postRepository.findPostByPlatformName(platformName, pageRequest);

        return postByPlatformName.map(post -> PlatformPostResponseDto.builder()
                .platformName(post.getPlatform().getName())
                .partySize(post.getPartySize())
                .currentCount(post.getCurrentCount())
                .memberName(post.getMember().getName())
                .postId(post.getPostId())
                .isExpired(post.getIsExpired())
                .build());
    }


    public MyPostOneResponseDto selectParty(String loginId, Long postId) {
        Post posts = postRepository.findByPostId(postId)
                .orElseThrow(()->new IllegalArgumentException("게시글이 없습니다."));
        Member member = memberJpaRepository.findByLoginId(loginId)
                .orElseThrow(()->new IllegalArgumentException("로그인 아이디 정보가 없습니다."));

        return MyPostOneResponseDto.builder()
                .platformName(posts.getPlatform().getName())
                .price(posts.getPlatform().getPrice())
                .currentCount(posts.getCurrentCount())
                .partySize(posts.getPartySize())
                .durationMonth(posts.getDurationMonth())
                .hostId(posts.getHostId())
                .hostPwd(posts.getHostPwd())
                .memberId(posts.getMember().getId())
                .isOwner(posts.getMember().getId().equals(member.getId()) ? "Y" : "N")
                .isExpired(posts.getIsExpired())
                .build();
    }


}
