package kosa.server.board.service;

import kosa.server.board.dto.response.MyPostOneResponseDto;
import kosa.server.board.dto.response.MyPostResponseDto;
import kosa.server.board.dto.response.PartyMemberDto;
import kosa.server.board.entity.PartyMember;
import kosa.server.board.entity.Post;
import kosa.server.board.repository.PostRepository;
import kosa.server.common.code.ErrorCode;
import kosa.server.member.entity.Member;
import kosa.server.member.exception.InvalidArgumentException;
import kosa.server.member.repository.jpa.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DashboardService {
    private final PostRepository postRepository;
    private final MemberJpaRepository memberJpaRepository;


    // 파티장 조회 - 상태 필터 포함
    public Page<MyPostResponseDto> readMyPost(String loginId, int page, String sortField, String sortDirection, String statusFilter) {
        Member member = memberJpaRepository.findByLoginId(loginId)
                .orElseThrow(()->new InvalidArgumentException(ErrorCode.MEMBER_NOT_FOUND));

        Sort sort = getSort(sortField, sortDirection);
        Pageable pageable = PageRequest.of(page, 9, sort);

        // 상태 필터에 따른 isExpired 값 결정
        String isExpired = "active".equals(statusFilter) ? "N" : "Y";

        Page<Post> posts = postRepository.findPostsByMemberIdAndExpiredStatus(member.getId(), isExpired, pageable);

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

    public MyPostOneResponseDto selectParty(String loginId, Long postId) {
        // postId 검증
        Post posts = postRepository.findById(postId)
                .orElseThrow(()->new InvalidArgumentException(ErrorCode.POST_NOT_FOUND));

        // loginId 검증
        Member member = memberJpaRepository.findByLoginId(loginId)
                .orElseThrow(()->new InvalidArgumentException(ErrorCode.MEMBER_NOT_FOUND));

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
                .price(posts.getPlatform().getPrice().divide(BigDecimal.valueOf(posts.getPartySize()), 0, BigDecimal.ROUND_HALF_UP))
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
                .expirationDate(posts.getExpirationDate())
                .startDate(posts.getStartDate())
                .build();
    }



    // 파티원 조회 - 상태 필터 포함
    public Page<MyPostResponseDto> findPostsByPartyMemberLoginId(String loginId, int page, String sortField, String sortDirection, String statusFilter) {
        Sort sort = getSort(sortField, sortDirection);
        Pageable pageable = PageRequest.of(page, 9, sort);

        // 상태 필터에 따른 isExpired 값 결정
        String isExpired = "active".equals(statusFilter) ? "N" : "Y";

        Page<Post> posts = postRepository.findAllByPartyMemberLoginIdAndExpiredStatus(loginId, isExpired, pageable);

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
                    .createdAt(post.getCreatedAt())
                    .build();
        });
    }

    // 정렬 조건 생성 헬퍼 메서드 - 더 많은 정렬 옵션 지원
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

    @Transactional
    public void startService(Long postId, int durationMonth) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()->new InvalidArgumentException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

        post.startService(durationMonth);
        postRepository.save(post);
    }
}
