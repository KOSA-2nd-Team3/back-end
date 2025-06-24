package kosa.server.board.dto.request;

import lombok.Data;

@Data
public class SubscriptionCreateDto {

    private Long platformId;
    private Long loginId;
}
