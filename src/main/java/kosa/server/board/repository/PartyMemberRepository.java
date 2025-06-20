package kosa.server.board.repository;

import kosa.server.board.entity.PartyMember;
import kosa.server.board.entity.Post;
import kosa.server.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartyMemberRepository extends JpaRepository<PartyMember, Long> {
    Optional<PartyMember> findByPostAndMember(Post post, Member member);
}
