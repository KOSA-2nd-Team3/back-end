package kosa.server.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AuthStatusDto {

    private boolean authenticated;
    private UserInfoDto user;

}
