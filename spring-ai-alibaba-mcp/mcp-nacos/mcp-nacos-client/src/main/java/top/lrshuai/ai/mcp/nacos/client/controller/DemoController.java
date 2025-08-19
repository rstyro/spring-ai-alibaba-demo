package top.lrshuai.ai.mcp.nacos.client.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@RequestMapping("/client")
@RestController
public class DemoController {

    private final ChatClient chatClient;

    @Resource
    @Qualifier("loadbalancedMcpAsyncToolCallbacks")
    private ToolCallbackProvider toolCallbackProvider;

    public DemoController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .build();
    }

    @GetMapping("/ask")
    public Flux<String> ask(@RequestParam(value = "question", defaultValue = "给我推荐几个资产超过30亿元的富婆") String question) {
        return chatClient
                .prompt(question)
                .toolCallbacks(toolCallbackProvider)
                .stream()
                .content();
    }

}
