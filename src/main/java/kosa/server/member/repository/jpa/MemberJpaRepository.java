package kosa.server.member.repository.jpa;

import kosa.server.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberJpaRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLoginId(String loginId);
//  Optional<Member> findByName(String name);
    @Query("SELECT m FROM Member m JOIN FETCH m.role WHERE m.loginId = :loginId")
    Optional<Member> findByLoginIdWithRole(@Param("loginId") String loginId);

    boolean existsByLoginId(String loginId);
    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);
}
