package kosa.server.member.repository.jpa;

import kosa.server.member.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleJpaRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(String roleName);

}
