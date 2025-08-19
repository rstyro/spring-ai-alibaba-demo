package top.lrshuai.ai.chat.controller;


import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import top.lrshuai.ai.common.resp.R;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/model")
@RestController
public class ChatModelController {

    @Resource
    private ChatModel dashScopeChatModel;

    public static final String DEFAULT_QUESTION = "你好，很高兴认识你，能简单介绍一下自己吗？";

    /**
     * 最简单的使用方式，没有任何 LLMs 参数注入。
     * @return String types.
     */
    @GetMapping("/simple/chat")
    public String simpleChat(@RequestParam(value = "query", defaultValue = DEFAULT_QUESTION) String query) {

        return dashScopeChatModel.call(new Prompt(query, DashScopeChatOptions
                .builder()
                .withModel(DashScopeApi.ChatModel.QWEN_PLUS.getValue())
                .build())).getResult().getOutput().getText();
    }

    /**
     * Stream 流式调用。可以使大模型的输出信息实现打字机效果。
     * @return Flux<String> types.
     */
    @GetMapping("/stream/chat")
    public Flux<String> streamChat(HttpServletResponse response
            ,@RequestParam(value = "query", defaultValue = DEFAULT_QUESTION) String query) {

        // 避免返回乱码
        response.setCharacterEncoding("UTF-8");

        Flux<ChatResponse> stream = dashScopeChatModel.stream(new Prompt(query, DashScopeChatOptions
                .builder()
                .withModel(DashScopeApi.ChatModel.QWEN_PLUS.getValue())
                .build()));
        return stream.map(resp -> resp.getResult().getOutput().getText());
    }

    /**
     * 演示如何获取 LLM 的 token 信息
     */
    @GetMapping("/tokens")
    public Object tokens(@RequestParam(value = "query", defaultValue = DEFAULT_QUESTION) String query) {

        ChatResponse chatResponse = dashScopeChatModel.call(new Prompt(query, DashScopeChatOptions
                .builder()
                .withModel(DashScopeApi.ChatModel.QWEN_PLUS.getValue())
                .build()));

        Map<String, Object> res = new HashMap<>();
        res.put("output", chatResponse.getResult().getOutput().getText());
        res.put("output_token", chatResponse.getMetadata().getUsage().getCompletionTokens());
        res.put("input_token", chatResponse.getMetadata().getUsage().getPromptTokens());
        res.put("total_token", chatResponse.getMetadata().getUsage().getTotalTokens());

        return R.ok(chatResponse);
    }

    /**
     * 使用编程方式自定义 LLMs ChatOptions 参数， {@link com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions}
     * 优先级高于在 application.yml 中配置的 LLMs 参数！
     */
    @GetMapping("/custom/chat")
    public String customChat(@RequestParam(value = "query", defaultValue = DEFAULT_QUESTION) String query) {

        DashScopeChatOptions customOptions = DashScopeChatOptions.builder()
                .withTopP(0.7)
                .withTopK(50)
                .withTemperature(0.8)
                .build();

        return dashScopeChatModel.call(new Prompt(query, customOptions)).getResult().getOutput().getText();
    }


}
