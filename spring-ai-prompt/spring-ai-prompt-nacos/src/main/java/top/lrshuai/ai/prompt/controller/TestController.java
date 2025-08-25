package top.lrshuai.ai.prompt.controller;

import com.alibaba.cloud.ai.prompt.ConfigurablePromptTemplate;
import com.alibaba.cloud.ai.prompt.ConfigurablePromptTemplateFactory;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/prompt")
public class TestController {

    private final ChatClient chatClient;
    /**
     * 参考源码 {@link ConfigurablePromptTemplateFactory 的onConfigChange()方法}
     * 默认监听 ：dataId = "spring.ai.alibaba.configurable.prompt", group = "DEFAULT_GROUP"
     */
    private final ConfigurablePromptTemplateFactory promptTemplateFactory;

    @Autowired
    public TestController(ChatClient.Builder builder, ConfigurablePromptTemplateFactory promptTemplateFactory) {
        this.chatClient = builder.build();
        this.promptTemplateFactory = promptTemplateFactory;
    }

    @GetMapping("/ask")
    public Flux<String> ask(HttpServletResponse response,@RequestParam(defaultValue = "鲁迅") String name) {
        // 避免返回乱码
        response.setCharacterEncoding("UTF-8");

        // 获取模板实例（优先使用Nacos配置）
        ConfigurablePromptTemplate template = promptTemplateFactory.getTemplate("idolTemplate");

        // 无配置时使用默认值
        if (template == null) {
            template = promptTemplateFactory.create(
                    "name",
                    "请列出这位{name}最著名的三本书。");
        }

        // 使用 系统人设
        Prompt prompt = template.create(Map.of("name", name));
        // 调用大模型
        return chatClient.prompt(prompt).stream().content();
    }

}
