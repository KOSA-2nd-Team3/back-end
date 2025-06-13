package kosa.server.member.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum RoleType {
    ADMIN("ROLE_ADMIN", "관리자"),
    USER("ROLE_USER", "유저");

    private final String key;
    private final String title;

    RoleType(String key, String title) {
        this.key = key;
        this.title = title;
    }

    public static RoleType fromKey(String key) {
        return Arrays.stream(RoleType.values()) // 모든 Enum 상수를 스트림으로 변환
                .filter(roleType -> roleType.getKey().equals(key)) // key 값이 일치하는 것을 찾아서
                .findFirst()// 첫 번째 일치하는 것을 Optional로 반환
                .orElseThrow(() -> new IllegalArgumentException("Invalid RoleType key: " + key)); // 없으면 예외 발생
    }
}
