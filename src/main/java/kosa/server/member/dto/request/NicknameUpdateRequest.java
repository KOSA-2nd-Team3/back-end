package kosa.server.member.dto.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NicknameUpdateRequest {
    private String nickname;
}
