package top.lrshuai.ai.chat.multi.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 多模型配置
 * @author rstyro
 */
@Configuration
public class ModelConfig {

    /**
     * 阿里百炼平台大模型
     * @param dashscopeChatModel 阿里百炼平台大模型
     * @return 客户端
     */
    @Bean("dashScopeChatClient")
    public ChatClient dashScopeChatClient(DashScopeChatModel dashscopeChatModel) {
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                // 实现 Logger 的 Advisor
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                // 设置 ChatClient 中 ChatModel 的 Options 参数
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .withTopP(0.7)
                                .build()
                )
                .build();

        return chatClient;
    }

    /**
     * deepseek 大模型
     * @param chatModel deepseek 大模型
     * @return deepseek客户端
     */
    @Bean("deepSeekChatClient")
    public ChatClient deepSeekChatClient(DeepSeekChatModel chatModel) {
        return  ChatClient.builder(chatModel).defaultAdvisors(MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build())
                // 实现 Logger 的 Advisor
                .defaultAdvisors(new SimpleLoggerAdvisor())
                // 设置 ChatClient 中 ChatModel 的 Options 参数
                .defaultOptions(DeepSeekChatOptions.builder().temperature(0.7d).build()).build();
    }
}
