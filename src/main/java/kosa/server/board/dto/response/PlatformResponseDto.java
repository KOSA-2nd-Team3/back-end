package kosa.server.board.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class PlatformResponseDto {

    private Long platformId;
    private String name;
    private int capacity;
    private Long price;
    private int category;
    private int monthUnit;

    @Builder
    public PlatformResponseDto(Long platformId, String name, int capacity, Long price, int category, int monthUnit) {
        this.platformId = platformId;
        this.name =  name;
        this.capacity = capacity;
        this.price = price;
        this.category = category;
        this.monthUnit = monthUnit;
    }
}
