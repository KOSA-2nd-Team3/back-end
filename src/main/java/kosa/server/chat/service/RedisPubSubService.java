package kosa.server.chat.service;

import kosa.server.chat.dto.ChatMessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
public class RedisPubSubService implements MessageListener {

    private final StringRedisTemplate stringRedisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;

    public RedisPubSubService(@Qualifier("chatPubSub") StringRedisTemplate stringRedisTemplate, SimpMessageSendingOperations messagingTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.messagingTemplate = messagingTemplate;
    }

    public void publish(String channel, String message) {
        stringRedisTemplate.convertAndSend(channel, message);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String payload = new String(message.getBody());
        ObjectMapper objectMapper = new ObjectMapper();
        ChatMessageDto chatMessageDto = null;
        try {
            chatMessageDto = objectMapper.readValue(payload, ChatMessageDto.class);
            messagingTemplate.convertAndSend("/topic/" + chatMessageDto.getRoomId(), chatMessageDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
