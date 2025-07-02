package kosa.server.board.repository;

import kosa.server.board.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 파티장 조회 - 상태 필터링 포함
    @Query(value = "SELECT p FROM Post p " +
            "JOIN FETCH p.platform platform " +
            "WHERE p.member.id = :memberId " +
            "AND p.isExpired = :isExpired",
            countQuery = "SELECT COUNT(p) FROM Post p " +
                    "WHERE p.member.id = :memberId " +
                    "AND p.isExpired = :isExpired")
    Page<Post> findPostsByMemberIdAndExpiredStatus(Long memberId, String isExpired, Pageable pageable);

    // 파티원 조회 - 상태 필터링 포함
    @Query(value = "SELECT p FROM Post p " +
            "JOIN p.partyMember pm " +
            "JOIN pm.member m " +
            "JOIN p.platform platform " +
            "WHERE m.loginId = :loginId " +
            "AND pm.isOwner = 'N' " +
            "AND p.isExpired = :isExpired",
            countQuery = "SELECT COUNT(p) FROM Post p " +
                    "JOIN p.partyMember pm " +
                    "JOIN pm.member m " +
                    "WHERE m.loginId = :loginId " +
                    "AND pm.isOwner = 'N' " +
                    "AND p.isExpired = :isExpired")
    Page<Post> findAllByPartyMemberLoginIdAndExpiredStatus(String loginId, String isExpired, Pageable pageable);

    List<Post> findAllByIsExpired(String isExpired);

    List<Post> findByPlatformId(Long platformId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Post p WHERE p.id = :id")
    Optional<Post> findByIdWithLock(@Param("id") Long id);
}
