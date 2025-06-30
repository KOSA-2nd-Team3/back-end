package kosa.server.board.scheduler;


import kosa.server.board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostExpirationScheduler {

    private final PostService postService;

    // 매일 12시에 실행
    @Scheduled(cron = "@daily")
    public void runExpirationCheck() {
        postService.updateExpiredPosts();
    }
}
