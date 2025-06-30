package kosa.server.board.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Schema(name = "MyPostResponse", description = "내 게시글 응답 DTO")
public class MyPostResponseDto {

    @Schema(description = "게시글 ID", example = "11", required = true)
    private Long postId;
    @Schema(description = "플랫폼 이름", example = "넷플릭스", required = true)
    private String platformName;
    @Schema(description = "현재 참여 인원 수", example = "3", required = true)
    private int currentCount;
    @Schema(description = "파티 최대 인원 수", example = "5", required = true)
    private int partySize;
    @Schema(description = "플랫폼 가격", example = "12900", required = true)
    private BigDecimal price;
    @Schema(description = "작성자 여부 (Y 또는 N)", example = "Y", required = true)
    private String isOwner;
    @Schema(description = "만료 여부 (Y 또는 N)", example = "N", required = true)
    private String isExpired;
    @Schema(description = "플랫폼 이미지 URL", example = "http://localhost:8080//netflix.png", required = true)
    private String platformImageUrl;
    @Schema(description = "게시글 생성일", example = "2025-06-30T14:00:00", type = "string", format = "date-time")
    private LocalDateTime createdAt;
}
