package kosa.server.board.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(name = "PlatformPostResponse", description = "플랫폼 게시글 응답 DTO")
public class PlatformPostResponseDto {

    @Schema(description = "게시글 ID", example = "1", required = true)
    private Long postId;
    @Schema(description = "방장 이름", example = "홍길동", required = true)
    private String leaderName;
    @Schema(description = "플랫폼 이름", example = "넷플릭스", required = true)
    private String platformName;
    @Schema(description = "플랫폼 가격", example = "12900", required = true)
    private Long platformPrice;
    @Schema(description = "구독 기간", example = "1", required = true)
    private int monthUnit;
    @Schema(description = "현재 참여 인원 수", example = "3", required = true)
    private int currentCount;
    @Schema(description = "최대 파티 인원 수", example = "5", required = true)
    private int partySize;
    @Schema(description = "만료 여부", example = "N", required = true)
    private String isExpired;
    @Schema(description = "게시글 시작일", example = "2025-06-01T00:00:00", type = "string", format = "date-time", required = true)
    private LocalDateTime startDate;
    @Schema(description = "게시글 생성일", example = "2025-06-01T00:00:00", type = "string", format = "date-time", required = true)
    private LocalDateTime createdAt;

}
