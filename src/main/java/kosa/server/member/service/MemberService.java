package kosa.server.member.service;

import kosa.server.board.entity.Post;
import kosa.server.board.repository.PostRepository;
import kosa.server.board.dto.request.PartyJoinRequestDto;
import kosa.server.member.entity.Member;
import kosa.server.member.repository.jpa.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberJpaRepository memberJpaRepository;
    private final PostRepository postRepository;


}
