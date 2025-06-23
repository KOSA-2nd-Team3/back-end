package kosa.server.board.service;

import kosa.server.board.dto.response.*;
import kosa.server.board.entity.Platform;
import kosa.server.board.entity.Post;
import kosa.server.board.repository.PlatformRepository;
import kosa.server.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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
                        .build())
                .collect(Collectors.toList());
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
                .orElseThrow(() -> new RuntimeException("Platform not found"));

        return PlatformResponseDto.builder()
                .platformId(findPlatform.getId())
                .name(findPlatform.getName())
                .capacity(findPlatform.getCapacity())
                .price(findPlatform.getPrice().longValue())
                .monthUnit(findPlatform.getMonthUnit())
                .build();
    }

    // 구하는 글 개수
    public List<PlatformSortResponseDto> getStatsOrderByCount() {
        List<PlatformSortResponseDto> list = getStatsCommon();
        list.sort(Comparator.comparing(PlatformSortResponseDto::getPostCount).reversed());
        return list;
    }

    // 가격
    public List<PlatformSortResponseDto> getStatusOrderByPrice() {
        List<PlatformSortResponseDto> list = getStatsCommon();
        list.sort(Comparator.comparing(PlatformSortResponseDto::getOnePersonPrice));
        return list;
    }


    private List<PlatformSortResponseDto> getStatsCommon() {
        List<Object[]> results = platformRepository.countPostStatsByPlatform();
        List<PlatformSortResponseDto> dtos = new ArrayList<>();
        for(Object[] row : results) {
            String name = (String) row[0];
            Long postCount = (Long) row[1];
            BigDecimal price = (BigDecimal) row[2];
            int capacity = (Integer) row[3];
            BigDecimal onePersonPrice = price.divide(BigDecimal.valueOf(capacity), 0);
            dtos.add(new PlatformSortResponseDto(name, postCount, onePersonPrice, capacity));
        }
        return dtos;
    }

    // 플랫폼 선택 후 현재 인원 정렬
    public List<PlatformPeopleSortDto> getStatsOrderByRate(String platformName) {
        List<Object[]> results = platformRepository.findPostStatsWithPlatform(platformName);
        List<PlatformPeopleSortDto> list = new ArrayList<>();
        for (Object[] row : results) {
            Long postId = ((Number) row[0]).longValue();
            String name = (String) row[1];
            String memberId = String.valueOf(row[2]);  // 가입자 ID (숫자일 수도 있으니 String 변환)
            BigDecimal price = (BigDecimal) row[3];
            int partySize = ((Number) row[4]).intValue();
            int currentCount = ((Number) row[5]).intValue();
            BigDecimal onePersonPrice = price.divide(BigDecimal.valueOf(partySize), 0, BigDecimal.ROUND_HALF_UP);
            int leftSeat = partySize - currentCount;
            double rate = (double) currentCount / partySize;
            list.add(new PlatformPeopleSortDto(postId, platformName, memberId, onePersonPrice, partySize, currentCount, leftSeat, rate));
        }
        list.sort(Comparator.comparing(PlatformPeopleSortDto::getRate).reversed());
        return list;
    }

    /*private List<PlatformSortResponseDto> getStatsCommon() {
        List<Object[]> results = platformRepository.countPostStatsByPlatform();
        List<PlatformSortResponseDto> dtos = new ArrayList<>();
        for(Object[] row : results) {
            String name = (String) row[0];
            Long postId = (Long) row[1];
            BigDecimal price = (BigDecimal) row[2];
            int currentCount = row[3] != null ? ((Long)row[3]).intValue() : 0;
            int maxCount = row[4] != null ? ((Long)row[4]).intValue() : 0;
            BigDecimal onePersonPrice = price.divide(BigDecimal.valueOf(maxCount), 0);
            double rate = (double) currentCount / maxCount;
            dtos.add(new PlatformSortResponseDto(name, postId, onePersonPrice, maxCount, currentCount , rate));
        }
        return dtos;
    }*/





    /*public Slice<PlatformPostResponseDto> findByCategory(int category, int page) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by("platform.price").descending());
        Slice<Post> postByPlatformName = platformRepository.findByCategory(category, pageRequest);

        return postByPlatformName.map(post -> PlatformPostResponseDto.builder()
                .platformName(post.getPlatform().getName())
                .partySize(post.getPartySize())
                .currentCount(post.getCurrentCount())
                .memberName(post.getMember().getName())
                .postId(post.getPostId())
                .isExpired(post.getIsExpired())
                .build());
    }*/
}
