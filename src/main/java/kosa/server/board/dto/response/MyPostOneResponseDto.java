package kosa.server.board.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Schema(name = "MyPostOneResponse", description = "내 게시글 상세 조회 응답 DTO")
public class MyPostOneResponseDto {

    // platform
    @Schema(description = "플랫폼 이름", example = "디즈니플러스", required = true)
    private String platformName;
    @Schema(description = "플랫폼 가격", example = "12900", required = true)
    private BigDecimal price;
    @Schema(description = "참여 인원 제한", example = "10", required = true)
    private int limitCount;

    // Post
    @Schema(description = "게시글 ID", example = "11", required = true)
    private Long postId;
    @Schema(description = "현재 참여 인원 수", example = "5", required = true)
    private int currentCount;
    @Schema(description = "최대 파티 인원 수", example = "10", required = true)
    private int partySize;
    @Schema(description = "구독 기간", example = "6", required = true)
    private int durationMonth;
    @Schema(description = "호스트 ID", example = "host123", required = true)
    private String hostId;
    @Schema(description = "호스트 비밀번호", example = "test1234!", required = true)
    private String hostPwd;
    @Schema(description = "만료 여부 (Y 또는 N)", example = "N", required = true)
    private String isExpired;
    @Schema(description = "파티 멤버 목록", required = true)
    private List<PartyMemberDto> members;
    @Schema(description = "게시글 만료일", example = "2025-12-31T23:59:59", type = "string", format = "date-time", required = false)
    private LocalDateTime expirationDate;
    @Schema(description = "게시글 시작일", example = "2025-06-01T00:00:00", type = "string", format = "date-time", required = false)
    private LocalDateTime startDate;

    // partyMember
    @Schema(description = "내 멤버 ID", example = "2", required = true)
    private Long memberId;
    @Schema(description = "내가 작성자인지 여부", example = "Y", required = true)
    private String isOwner;
    @Schema(description = "플랫폼 이미지 URL", example = "http://localhost:8080/disneyplus.png", required = true)
    private String platformImageUrl;
}
