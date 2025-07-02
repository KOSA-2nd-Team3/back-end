package kosa.server.board.repository;

import kosa.server.board.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {

    List<Platform> findAllByOrderByCategoryAscNameAsc();
    List<Platform> findByCategoryOrderByNameAsc(int category);
    Optional<Platform> findByName(String name);
}
