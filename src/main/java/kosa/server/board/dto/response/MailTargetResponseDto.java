package kosa.server.board.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MailTargetResponseDto {
        private final String email;
        private final String platformName;
        private final String postUrl;
        private final int partySize;

}
