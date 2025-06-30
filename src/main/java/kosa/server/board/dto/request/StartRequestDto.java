package kosa.server.board.dto.request;

import lombok.Data;
import lombok.Getter;

@Data
public class StartRequestDto {
    private Long PostId;
    private int durationMonth;
}
