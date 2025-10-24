package top.lrshuai.ai.mcp.client;

import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class McpClientAnnotationsApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpClientAnnotationsApplication.class, args);
    }

    @Value("${spring.ai.ollama.base-url}")
    private String baseUrl;

    @Bean
    @ConditionalOnMissingBean
    public OllamaApi ollamaApi(ObjectProvider<RestClient.Builder> restClientBuilderProvider,
                               ObjectProvider<WebClient.Builder> webClientBuilderProvider, ResponseErrorHandler responseErrorHandler) {
        return OllamaApi.builder()
                .baseUrl(baseUrl)
                .restClientBuilder(restClientBuilderProvider.getIfAvailable(RestClient::builder))
                .webClientBuilder(webClientBuilderProvider.getIfAvailable(WebClient::builder))
                .responseErrorHandler(responseErrorHandler)
                .build();
    }
}
