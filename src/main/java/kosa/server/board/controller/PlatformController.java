package kosa.server.board.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kosa.server.board.dto.request.SubscriptionCreateDto;
import kosa.server.board.dto.response.*;
import kosa.server.board.service.PlatformService;
import kosa.server.board.service.PostService;
import kosa.server.common.security.user.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Platform API")
@Slf4j
@RestController
@RequestMapping("/api/list")
@RequiredArgsConstructor
public class PlatformController {

    private final PostService postService;
    private final PlatformService platformService;

    // 메인 페이지
    @Operation(summary = "전체 플랫폼 조회", description = "메인 페이지에서 모든 플랫폼 목록을 조회합니다.")
    @GetMapping("/main")
    public ResponseEntity<List<PlatformCategoryDto>> getAllPlatforms() {
        List<PlatformCategoryDto> allPlatform = platformService.getAllPlatforms();
        return new ResponseEntity<>(allPlatform, HttpStatus.OK);
    }

    // 메인 페이지에서 카테고리 클릭
    @Operation(summary = "카테고리별 플랫폼 조회", description = "카테고리 클릭 시 해당 카테고리의 플랫폼 목록을 조회합니다.")
    @GetMapping("/category/{category}")
    public ResponseEntity<List<PlatformCategoryDto>> getPlatformsByCategory(@PathVariable("category") int category) {
        List<PlatformCategoryDto> categoryPlatform = platformService.getPlatformsByCategory(category);
        return new ResponseEntity<>(categoryPlatform, HttpStatus.OK);
    }

    // 메인 페이지에서 플랫폼 하나 선택했을 때, 모든 방 리스트 출력
    @Operation(summary = "플랫폼별 파티 목록 조회", description = "특정 플랫폼을 클릭했을 때 해당 플랫폼의 파티 목록을 조회합니다.")
    @GetMapping("/{platformName}")
    public ResponseEntity<Slice<PlatformPostResponseDto>> list(@PathVariable("platformName") String platformName) {
        Slice<PlatformPostResponseDto> byPlatformName = postService.findByPlatformName(platformName);
        return new ResponseEntity<>(byPlatformName, HttpStatus.OK);
    }

    // 플랫폼 기준 (현재인원 / 최대인원) 정렬
    @Operation(summary = "현재 인원 비율로 정렬", description = "플랫폼 기준 비율로 정렬된 목록을 반환합니다.")
    @GetMapping("/{platformName}/sort/rate")
    public ResponseEntity<List<PlatformPeopleSortDto>> getStatsOrderByRate(@PathVariable("platformName") String platformName) {
        List<PlatformPeopleSortDto> StatsOrderByRate = platformService.getStatsOrderByRate(platformName);
        return new ResponseEntity<>(StatsOrderByRate, HttpStatus.OK);
    }

    // 메인 페이지에 있는 카테고리 버튼 클릭
//    @GetMapping("/posts")
//    public ResponseEntity<Slice<PlatformPostResponseDto>> listByCategory(@RequestParam int category, @RequestParam int page) {
//        Slice<PlatformPostResponseDto> byCategory = platformService.findByCategory(category, page);
//        return new ResponseEntity<>(byCategory, HttpStatus.OK);
//    }

    // 플랫폼 마다 구하는 글 개수
    @Operation(summary = "플랫폼별 글 개수 정렬", description = "모든 플랫폼을 글 개수 기준으로 정렬하여 조회합니다.")
    @GetMapping("/main/sort/count")
    public ResponseEntity<List<PlatformSortResponseDto>> getStatsOrderByCount() {
        List<PlatformSortResponseDto> StatsOrderByCount = platformService.getStatsOrderByCount();
        return new ResponseEntity<>(StatsOrderByCount, HttpStatus.OK);
    }

    // 가격 별로
    @Operation(summary = "가격별 플랫폼 정렬", description = "모든 플랫폼을 가격 기준으로 정렬하여 조회합니다.")
    @GetMapping("/main/sort/price")
    public ResponseEntity<List<PlatformSortResponseDto>> getPriceByPlatform() {
        List<PlatformSortResponseDto> PriceByPlatform = platformService.getStatusOrderByPrice();
        return new ResponseEntity<>(PriceByPlatform, HttpStatus.OK);
    }

    @Operation(summary = "내 구독 플랫폼 조회", description = "현재 로그인한 사용자의 구독 중인 플랫폼 목록을 조회합니다.")
    @GetMapping("/subscription")
    public ResponseEntity<?> getPlatform(@AuthenticationPrincipal CustomUserPrincipal customUserDetails) {
        String loginId = customUserDetails.getUsername();
        log.info("GET /subscription 요청: loginId={}", loginId);
        List<PlatformResponseDto> platformListDto = platformService.getPlatformList();

        return new ResponseEntity<>(platformListDto, HttpStatus.OK);
    }

    @Operation(summary = "플랫폼 ID로 상세 조회", description = "플랫폼 ID를 통해 특정 플랫폼의 상세 정보를 조회합니다.")
    @GetMapping("/platforms/{platformId}")
    public ResponseEntity<?> getPlatformById(@PathVariable Long platformId,
                                             @AuthenticationPrincipal CustomUserPrincipal customUserDetails) {
        String loginId = customUserDetails.getUsername();
        log.info("GET /subscription 요청: loginId={}", loginId);
        PlatformResponseDto platformDto = platformService.getPlatform(platformId);
        return new ResponseEntity<>(platformDto, HttpStatus.OK);
    }

    @Operation(summary = "구독 생성", description = "플랫폼 구독을 생성하고 해당 플랫폼의 정보를 반환합니다.")
    @PostMapping("/subscription")
    public ResponseEntity<?> createSubscription(@RequestBody SubscriptionCreateDto subscriptionCreateDto) {
        Long platformId = subscriptionCreateDto.getPlatformId();
        PlatformResponseDto platformDto = platformService.getPlatform(platformId);
        return new ResponseEntity<>(platformDto, HttpStatus.OK);
    }
}
