package kosa.server.board.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
public class PostUpdateRequestDto {

    private Long postId;
    private int capacity;
    private int durationMonth;
    private String hostId;
    private String hostPwd;

    @Builder
    public PostUpdateRequestDto(int capacity, int durationMonth, String hostId, String hostPwd) {
        this.capacity = capacity;
        this.durationMonth = durationMonth;
        this.hostId = hostId;
        this.hostPwd = hostPwd;
    }
}
