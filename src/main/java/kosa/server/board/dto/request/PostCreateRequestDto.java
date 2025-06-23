package kosa.server.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/*
게시판 구인글 요청
*
*/
@Data
public class PostCreateRequestDto {

    @NotEmpty(message = "플랫폼 아이디가 없습니다.")
    private Long platformId;

    @NotEmpty(message = "최대 인원수는 필수입니다.")
    private int capacity;

    @NotEmpty(message = "현재 인숸수는 필수입니다.")
    private int currentCount;

    @NotEmpty(message = "로그인 아이디는 필수입니다.")
    private String loginId;

    private int durationMonth;

    private String hostId;

    private String hostPwd;

}