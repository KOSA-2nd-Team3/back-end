package kosa.server.board.repository;

import kosa.server.board.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByPostId(Long postId);

    //PlatForm 사용
    Page<Post> findByMember_Id(Long memberId, Pageable pageable);

    //PlatForm, Member 사용
    Page<Post> findPostByPlatformName(String platformName, Pageable pageable);

}
