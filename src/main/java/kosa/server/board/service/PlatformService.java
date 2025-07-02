package kosa.server.board.service;

import kosa.server.board.dto.response.PlatformCategoryDto;
import kosa.server.board.dto.response.PlatformPostNullResponseDto;
import kosa.server.board.dto.response.PlatformPostResponseDto;
import kosa.server.board.dto.response.PlatformResponseDto;
import kosa.server.board.entity.Platform;
import kosa.server.board.entity.Post;
import kosa.server.board.exception.PlatformNotFoundException;
import kosa.server.board.repository.PlatformRepository;
import kosa.server.board.repository.PostRepository;
import kosa.server.common.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class PlatformService {

    private final PostRepository postRepository;
    private final PlatformRepository platformRepository;

    // 전체 플랫폼
    public List<PlatformCategoryDto> getAllPlatforms() {
        List<Platform> platforms = platformRepository.findAllByOrderByCategoryAscNameAsc();

        return platforms.stream()
                .map(platform -> PlatformCategoryDto.builder()
                        .platformId(platform.getId())
                        .platformName(platform.getName())
                        .price(platform.getPrice())
                        .category(platform.getCategory())
                        .capacity(platform.getCapacity())
                        .imageUrl(platform.getImageUrl())
                        .build())
                .collect(Collectors.toList());
    }

    // 카테고리별 플랫폼
    public List<PlatformCategoryDto> getPlatformsByCategory(int category) {
        List<Platform> platforms = platformRepository.findByCategoryOrderByNameAsc(category);

        return platforms.stream()
                .map(platform -> PlatformCategoryDto.builder()
                        .platformId(platform.getId())
                        .platformName(platform.getName())
                        .price(platform.getPrice())
                        .category(platform.getCategory())
                        .capacity(platform.getCapacity())
                        .imageUrl(platform.getImageUrl())
                        .build())
                .collect(Collectors.toList());
    }

    public List<PlatformPostResponseDto> platformPostList(Long platformId) {
        List<Post> posts = postRepository.findByPlatformId(platformId);

        return posts.stream().map(post -> PlatformPostResponseDto.builder()
                .postId(post.getId())
                .nickName(post.getMember().getNickname())
                .platformName(post.getPlatform().getName())
                .platformPrice(post.getPlatform().getPrice().longValue())
                .monthUnit(post.getPlatform().getMonthUnit())
                .currentCount(post.getCurrentCount())
                .partySize(post.getPartySize())
                .durationMonth(post.getDurationMonth())
                .isExpired(post.getIsExpired())
                .startDate(post.getStartDate())
                .createdAt(post.getCreatedAt())
                .build()).toList();
    }

    public PlatformPostNullResponseDto platformPostNull(Long platformId) {
        Platform platform = platformRepository.findById(platformId)
                .orElseThrow(() -> new PlatformNotFoundException(ErrorCode.PLATFORM_NOT_FOUND));

        return PlatformPostNullResponseDto.builder()
                .platformName(platform.getName())
                .platformPrice(platform.getPrice())
                .build();
    }

    public List<PlatformResponseDto> getPlatformList() {
        return platformRepository.findAll().stream().map(p -> PlatformResponseDto.builder()
                .platformId(p.getId())
                .name((p.getName()))
                .category(p.getCategory())
                .build()).toList();
    }

    public PlatformResponseDto getPlatform(Long platformId) {
        Platform findPlatform = platformRepository.findById(platformId)
                .orElseThrow(() -> new PlatformNotFoundException(ErrorCode.PLATFORM_NOT_FOUND));

        return PlatformResponseDto.builder()
                .platformId(findPlatform.getId())
                .name(findPlatform.getName())
                .capacity(findPlatform.getCapacity())
                .price(findPlatform.getPrice().longValue())
                .monthUnit(findPlatform.getMonthUnit())
                .build();
    }
}
