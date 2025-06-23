package kosa.server.board.repository;

import kosa.server.board.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    //PlatForm 사용
    @Query("SELECT DISTINCT p FROM Post p JOIN p.partyMember pm WHERE pm.member.id = :memberId")
    Page<Post> findPostsByPartyMemberId(@Param("memberId") Long memberId, Pageable pageable);


    //PlatForm, Member 사용
    Page<Post> findPostByPlatformName(String platformName, Pageable pageable);

}
