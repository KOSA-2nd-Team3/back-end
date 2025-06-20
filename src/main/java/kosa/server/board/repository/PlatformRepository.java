package kosa.server.board.repository;

import kosa.server.board.entity.Platform;
import kosa.server.board.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {

    @Query("SELECT p.name, COUNT(post), p.price, p.capacity " +
            "FROM Platform p LEFT JOIN Post post ON p.id = post.platform.id " +
            "GROUP BY p.id, p.name, p.price, p.capacity")
    List<Object[]> countPostStatsByPlatform();

    @Query("SELECT post.postId, p.name, post.member.id, p.price, post.partySize, post.currentCount " +
            "FROM Post post JOIN post.platform p "+
            "WHERE p.name = :platformName")
    List<Object[]> findPostStatsWithPlatform(@Param("platformName") String platformName);

    Page<Post> findByCategory(int category, Pageable pageable);
    Platform findByName(String name);

    List<Platform> findAllByOrderByCategoryAscNameAsc();
    List<Platform> findByCategoryOrderByNameAsc(int category);

}
