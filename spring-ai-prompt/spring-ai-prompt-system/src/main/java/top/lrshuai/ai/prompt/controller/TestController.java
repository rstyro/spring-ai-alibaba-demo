package top.lrshuai.ai.prompt.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/prompt")
public class TestController {

    private final ChatClient chatClient;

    private final String DEFAULT_PROMPT_TEXT = "他的经典语录";

    /**
     * 加载 System prompt tmpl.
     */
    @Value("classpath:/prompts/prompt-role.st")
    private Resource systemResource;

    @Autowired
    public TestController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/ask")
    public Flux<String> ask(HttpServletResponse response,
                            @RequestParam(defaultValue = DEFAULT_PROMPT_TEXT) String question,
                            @RequestParam(defaultValue = "鲁迅") String name,
                            @RequestParam(defaultValue = "严肃") String voice) {
        // 避免返回乱码
        response.setCharacterEncoding("UTF-8");
        // 用户输入
        UserMessage userMessage = new UserMessage(question);
        // 使用 系统人设
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemResource);
        // 填充占位符变量
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", name, "voice", voice));
        // 调用大模型
        return chatClient.prompt(new Prompt(List.of(userMessage,systemMessage))).stream().content();
    }

}
