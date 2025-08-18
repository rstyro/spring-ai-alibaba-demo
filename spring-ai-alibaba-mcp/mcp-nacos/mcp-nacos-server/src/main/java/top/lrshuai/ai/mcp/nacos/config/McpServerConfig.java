package top.lrshuai.ai.mcp.nacos.config;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpServerConfig {

    @Bean
    public ToolCallbackProvider toolCallbackProvider(RichWomanService richWomanService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(richWomanService).build();
    }

}