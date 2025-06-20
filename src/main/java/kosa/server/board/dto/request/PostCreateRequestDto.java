package kosa.server.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/*
*   게시판 구인글 요청
*
*/
@Data
public class PostCreateRequestDto {

    @NotBlank(message = "서비스명")
    private String platformName;

    @NotBlank(message = "최대 인원 수")
    private int capacity;

    private String loginId;

    @NotBlank(message = "이용할 개월 수")
    private int durationMonth;

    @NotBlank(message = "공유 할 아이디")
    private String hostId;

    @NotBlank(message = "공유 할 비밀번호")
    private String hostPwd;

}
