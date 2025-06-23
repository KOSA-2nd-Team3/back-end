package kosa.server.board.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
public class PartyJoinRequestDto {
    private Long postId;
    private String loginId;
    private String isOwner;

    @Builder
    public PartyJoinRequestDto(Long postId, String loginId, String isOwner) {
        this.postId = postId;
        this.loginId = loginId;
        this.isOwner = isOwner;
    }
}
