package kosa.server.board.dto.request;

import lombok.Data;

@Data
public class LeaveMyPostRequestDto {

    private Long postId;
    private String loginId;

}
