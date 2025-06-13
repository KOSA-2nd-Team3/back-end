package kosa.server.member.entity;

import jakarta.persistence.*;
import kosa.server.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Role extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String roleName; // 예: "ROLE_USER", "ROLE_ADMIN"

    @Builder
    private Role(String roleName) {
        this.roleName = roleName;
    }
}
