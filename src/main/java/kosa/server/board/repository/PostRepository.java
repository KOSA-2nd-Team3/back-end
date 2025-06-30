package kosa.server.board.repository;

import kosa.server.board.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 기존 파티장 조회 메서드 (상태 필터링 없음)
    @Query(value = "SELECT p FROM Post p JOIN p.platform platform WHERE p.member.id = :memberId",
            countQuery = "SELECT count(p) FROM Post p WHERE p.member.id = :memberId")
    Page<Post> findPostsByMemberId(Long memberId, Pageable pageable);

    // 파티장 조회 - 상태 필터링 포함
    @Query(value = "SELECT p FROM Post p " +
            "JOIN p.platform platform " +
            "WHERE p.member.id = :memberId " +
            "AND p.isExpired = :isExpired",
            countQuery = "SELECT COUNT(p) FROM Post p " +
                    "WHERE p.member.id = :memberId " +
                    "AND p.isExpired = :isExpired")
    Page<Post> findPostsByMemberIdAndExpiredStatus(Long memberId, String isExpired, Pageable pageable);

    // 기존 파티원 조회 메서드 (상태 필터링 없음)
    @Query(value = "SELECT DISTINCT p FROM Post p " +
            "JOIN p.partyMember pm " +
            "JOIN pm.member m " +
            "JOIN p.platform platform " +
            "WHERE m.loginId = :loginId " +
            "AND pm.isOwner = 'N'",
            countQuery = "SELECT COUNT(DISTINCT p) FROM Post p " +
                    "JOIN p.partyMember pm " +
                    "JOIN pm.member m " +
                    "WHERE m.loginId = :loginId " +
                    "AND pm.isOwner = 'N'")
    Page<Post> findAllByPartyMemberLoginId(String loginId, Pageable pageable);

    // 파티원 조회 - 상태 필터링 포함
    @Query(value = "SELECT DISTINCT p FROM Post p " +
            "JOIN p.partyMember pm " +
            "JOIN pm.member m " +
            "JOIN p.platform platform " +
            "WHERE m.loginId = :loginId " +
            "AND pm.isOwner = 'N' " +
            "AND p.isExpired = :isExpired",
            countQuery = "SELECT COUNT(DISTINCT p) FROM Post p " +
                    "JOIN p.partyMember pm " +
                    "JOIN pm.member m " +
                    "WHERE m.loginId = :loginId " +
                    "AND pm.isOwner = 'N' " +
                    "AND p.isExpired = :isExpired")
    Page<Post> findAllByPartyMemberLoginIdAndExpiredStatus(String loginId, String isExpired, Pageable pageable);

    List<Post> findAllByIsExpired(String isExpired);
    //PlatForm, Member 사용
    Page<Post> findPostByPlatformName(String platformName, Pageable pageable);

    List<Post> findByPlatformId(Long platformId);
}
