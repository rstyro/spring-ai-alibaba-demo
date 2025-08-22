package top.lrshuai.ai.chat.multi.controller;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author rstyro
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    private static final String DEFAULT_PROMPT = "你好，介绍下你自己！";

    @Qualifier("dashScopeChatClient")
    @Resource
    private ChatClient dashScopeChatClient;

    @Qualifier("deepSeekChatClient")
    @Resource
    private ChatClient deepSeekChatClient;


    @Resource
    private DashScopeChatModel dashScopeChatModel;

    /**
     * 执行默认提示语的 AI 生成请求
     */
    @GetMapping("/dashScope/ask")
    public String chat(@RequestParam(defaultValue = DEFAULT_PROMPT) String question) {
        return dashScopeChatClient.prompt(question)
                .call()
                .content();
    }


    /**
     * 流式生成接口 - 支持实时获取生成过程的分块响应
     */
    @GetMapping("/dashScope/stream")
    public Flux<String> stream(HttpServletResponse response,@RequestParam(defaultValue = DEFAULT_PROMPT) String question) {
        // 避免返回乱码
        response.setCharacterEncoding("UTF-8");
        return dashScopeChatClient.prompt(question)
                .stream()
                .content();
    }


    /**
     * Stream 流式调用。可以使大模型的输出信息实现打字机效果。
     * @return Flux<String> types.
     */
    @GetMapping("/dashScopeChatModel/stream")
    public Flux<String> streamChat(HttpServletResponse response ,@RequestParam(defaultValue = DEFAULT_PROMPT) String query) {

        // 避免返回乱码
        response.setCharacterEncoding("UTF-8");

        Flux<ChatResponse> stream = dashScopeChatModel.stream(new Prompt(query, DashScopeChatOptions
                .builder()
                .withModel(DashScopeApi.ChatModel.QWEN_PLUS.getValue())
                .build()));
        return stream.map(resp -> resp.getResult().getOutput().getText());
    }




    /**
     * 执行默认提示语的 AI 生成请求
     */
    @GetMapping("/deepSeek/ask")
    public String deepSeekAsk(@RequestParam(defaultValue = DEFAULT_PROMPT) String question) {
        return deepSeekChatClient.prompt(question)
                .call()
                .content();
    }


}
