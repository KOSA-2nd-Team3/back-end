package kosa.server.auth.oauth2.handler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TempAuthStorage {

    private static final ConcurrentHashMap<String, TempAuthData> storage = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    static {
        // 주기적으로 만료된 토큰 정리 (1분마다)
        scheduler.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            storage.entrySet().removeIf(entry -> entry.getValue().expiredAt < now);
        }, 1, 1, TimeUnit.MINUTES);
    }

    public static void store(String code, String accessToken, long ttlMillis) {
        long expiredAt = System.currentTimeMillis() + ttlMillis;
        storage.put(code, new TempAuthData(accessToken, expiredAt));
    }

    public static String retrieveAndRemove(String code) {
        TempAuthData data = storage.remove(code);
        if (data == null || data.expiredAt < System.currentTimeMillis()) {
            return null;
        }
        return data.accessToken;
    }

    private static class TempAuthData {
        final String accessToken;
        final long expiredAt;

        TempAuthData(String accessToken, long expiredAt) {
            this.accessToken = accessToken;
            this.expiredAt = expiredAt;
        }
    }
}
