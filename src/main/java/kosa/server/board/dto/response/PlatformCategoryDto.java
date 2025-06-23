package kosa.server.board.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PlatformCategoryDto {
    private Long platformId;
    private String platformName;
    private BigDecimal price;
    private int category;
    private int capacity;
    private String imageUrl;
}
