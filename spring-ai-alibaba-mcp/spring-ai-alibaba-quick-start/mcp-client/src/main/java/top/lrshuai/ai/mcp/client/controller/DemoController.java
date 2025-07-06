package top.lrshuai.ai.mcp.client.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.*;
import top.lrshuai.ai.common.resp.R;


@RequestMapping("/client")
@RestController
public class DemoController {

    private final ChatClient chatClient;

    @Resource
    private ToolCallbackProvider toolCallbackProvider;

    public DemoController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .build();
    }

    /**
     * 默认询问天气
     */
    @GetMapping("/ask")
    public R ask(@RequestParam(value = "question", defaultValue = "深圳今天的天气怎样") String question) {
        return R.ok(chatClient
                .prompt(question)
                .toolCallbacks(toolCallbackProvider)
                .call()
                .content());
    }

}
