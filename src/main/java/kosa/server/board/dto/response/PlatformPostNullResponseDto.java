package kosa.server.board.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PlatformPostNullResponseDto {
    private String platformName;
    private BigDecimal platformPrice;
}
