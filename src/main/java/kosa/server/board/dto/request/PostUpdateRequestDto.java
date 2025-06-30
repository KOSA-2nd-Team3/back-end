package kosa.server.board.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class PostUpdateRequestDto {

    private Long postId;
    private int durationMonth;

    @JsonProperty("email")
    private String hostId;
    @JsonProperty("password")
    private String hostPwd;

    @Builder
    public PostUpdateRequestDto(int durationMonth, String hostId, String hostPwd) {
        this.durationMonth = durationMonth;
        this.hostId = hostId;
        this.hostPwd = hostPwd;
    }
}
