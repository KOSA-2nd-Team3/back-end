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

    List<Platform> findAllByOrderByCategoryAscNameAsc();
    List<Platform> findByCategoryOrderByNameAsc(int category);

}
