package kosa.server.board.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class PlatformSortResponseDto {
    private String platformName;
    private Long postCount;
    private BigDecimal onePersonPrice;
    private int capacity;
}
