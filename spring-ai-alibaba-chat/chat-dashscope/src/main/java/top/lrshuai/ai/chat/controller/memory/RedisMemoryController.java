package top.lrshuai.ai.chat.controller.memory;

import com.alibaba.cloud.ai.memory.redis.RedisChatMemoryRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

/**
 * @author yingzi
 * @date 2025/5/27 19:10
 */
@RestController
@RequestMapping("/advisor/memory/redis")
public class RedisMemoryController {

    private final ChatClient chatClient;
    private final int MAX_MESSAGES = 100;
    private final MessageWindowChatMemory messageWindowChatMemory;

    public RedisMemoryController(ChatClient.Builder builder, RedisChatMemoryRepository redisChatMemoryRepository) {
        this.messageWindowChatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(redisChatMemoryRepository)
                .maxMessages(MAX_MESSAGES)
                .build();

        this.chatClient = builder
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(messageWindowChatMemory)
                                .build()
                )
                .build();
    }

    @GetMapping("/call")
    public String call(@RequestParam(value = "query", defaultValue = "你好，我的外号是影子，请记住呀") String query,
                       @RequestParam(value = "conversation_id", defaultValue = "yingzi") String conversationId
    ) {
        return chatClient.prompt(query)
                .advisors(
                        a -> a.param(CONVERSATION_ID, conversationId)
                )
                .call().content();
    }

    @GetMapping("/messages")
    public List<Message> messages(@RequestParam(value = "conversation_id", defaultValue = "yingzi") String conversationId) {
        return messageWindowChatMemory.get(conversationId);
    }
}
