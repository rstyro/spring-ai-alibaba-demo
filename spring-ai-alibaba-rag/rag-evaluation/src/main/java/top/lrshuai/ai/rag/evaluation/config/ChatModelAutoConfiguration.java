package top.lrshuai.ai.rag.evaluation.config;

import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeChatProperties;
import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeConnectionProperties;
import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeConnectionUtils;
import com.alibaba.cloud.ai.autoconfigure.dashscope.ResolvedConnectionProperties;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.model.tool.DefaultToolExecutionEligibilityPredicate;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionEligibilityPredicate;
import org.springframework.ai.model.tool.autoconfigure.ToolCallingAutoConfiguration;
import org.springframework.ai.retry.autoconfigure.SpringAiRetryAutoConfiguration;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 配置2个模型，代码可参考自：
 * @see com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeChatAutoConfiguration
 */
@ConditionalOnClass({DashScopeApi.class})
@AutoConfiguration(
        after = {RestClientAutoConfiguration.class, SpringAiRetryAutoConfiguration.class, ToolCallingAutoConfiguration.class}
)
@ImportAutoConfiguration(
        classes = {SpringAiRetryAutoConfiguration.class, RestClientAutoConfiguration.class, ToolCallingAutoConfiguration.class, WebClientAutoConfiguration.class}
)
@EnableConfigurationProperties({DashScopeConnectionProperties.class, DashScopeChatProperties.class})
public class ChatModelAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ChatModelAutoConfiguration.class);

    // 1. 提取通用配置方法
    private DashScopeChatModel createChatModel(
            String modelName,
            String logMessage,
            RetryTemplate retryTemplate,
            ToolCallingManager toolCallingManager,
            DashScopeChatProperties chatProperties,
            ResponseErrorHandler responseErrorHandler,
            DashScopeConnectionProperties commonProperties,
            ObjectProvider<ObservationRegistry> observationRegistry,
            ObjectProvider<WebClient.Builder> webClientBuilderProvider,
            ObjectProvider<RestClient.Builder> restClientBuilderProvider,
            ObjectProvider<ChatModelObservationConvention> observationConvention,
            ObjectProvider<ToolExecutionEligibilityPredicate> toolEligibilityPredicate) {

        // 深拷贝配置避免相互影响
        DashScopeChatProperties clonedProperties = new DashScopeChatProperties();
        BeanUtils.copyProperties(chatProperties, clonedProperties);
        if (clonedProperties.getOptions() != null) {
            clonedProperties.getOptions().setModel(modelName);
        }

        DashScopeApi dashscopeApi = this.dashscopeChatApi(
                commonProperties,
                clonedProperties,
                restClientBuilderProvider.getIfAvailable(RestClient::builder),
                webClientBuilderProvider.getIfAvailable(WebClient::builder),
                responseErrorHandler,
                "chat"
        );

        DashScopeChatModel model = DashScopeChatModel.builder()
                .dashScopeApi(dashscopeApi)
                .retryTemplate(retryTemplate)
                .toolCallingManager(toolCallingManager)
                .defaultOptions(clonedProperties.getOptions())
                .observationRegistry(observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
                .toolExecutionEligibilityPredicate(toolEligibilityPredicate.getIfUnique(DefaultToolExecutionEligibilityPredicate::new))
                .build();

        observationConvention.ifAvailable(model::setObservationConvention);
        logger.info(logMessage);
        return model;
    }

    // 2. 复用API创建逻辑
    private DashScopeApi dashscopeChatApi(
            DashScopeConnectionProperties commonProperties,
            DashScopeChatProperties chatProperties,
            RestClient.Builder restClientBuilder,
            WebClient.Builder webClientBuilder,
            ResponseErrorHandler responseErrorHandler,
            String modelType) {

        ResolvedConnectionProperties resolved = DashScopeConnectionUtils.resolveConnectionProperties(
                commonProperties, chatProperties, modelType);

        return DashScopeApi.builder()
                .apiKey(resolved.apiKey())
                .headers(resolved.headers())
                .baseUrl(resolved.baseUrl())
                .webClientBuilder(webClientBuilder)
                .workSpaceId(resolved.workspaceId())
                .restClientBuilder(restClientBuilder)
                .responseErrorHandler(responseErrorHandler)
                .build();
    }

    // 3. 简化Bean声明
    @Bean(name = "qwen-max")
    public DashScopeChatModel qwenMaxChatModel(
            RetryTemplate retryTemplate,
            ToolCallingManager toolCallingManager,
            DashScopeChatProperties chatProperties,
            ResponseErrorHandler responseErrorHandler,
            DashScopeConnectionProperties commonProperties,
            ObjectProvider<ObservationRegistry> observationRegistry,
            ObjectProvider<WebClient.Builder> webClientBuilderProvider,
            ObjectProvider<RestClient.Builder> restClientBuilderProvider,
            ObjectProvider<ChatModelObservationConvention> observationConvention,
            ObjectProvider<ToolExecutionEligibilityPredicate> toolEligibilityPredicate) {

        return createChatModel(
                "qwen-max",
                "load qwenMaxChatModel success",
                retryTemplate,
                toolCallingManager,
                chatProperties,
                responseErrorHandler,
                commonProperties,
                observationRegistry,
                webClientBuilderProvider,
                restClientBuilderProvider,
                observationConvention,
                toolEligibilityPredicate
        );
    }

    @Bean(name = "qwen-plus")
    public DashScopeChatModel qwenPlusChatModel(
            RetryTemplate retryTemplate,
            ToolCallingManager toolCallingManager,
            DashScopeChatProperties chatProperties,
            ResponseErrorHandler responseErrorHandler,
            DashScopeConnectionProperties commonProperties,
            ObjectProvider<ObservationRegistry> observationRegistry,
            ObjectProvider<WebClient.Builder> webClientBuilderProvider,
            ObjectProvider<RestClient.Builder> restClientBuilderProvider,
            ObjectProvider<ChatModelObservationConvention> observationConvention,
            ObjectProvider<ToolExecutionEligibilityPredicate> toolEligibilityPredicate) {

        return createChatModel(
                "qwen-plus",
                "load qwenPlusChatModel success",
                retryTemplate,
                toolCallingManager,
                chatProperties,
                responseErrorHandler,
                commonProperties,
                observationRegistry,
                webClientBuilderProvider,
                restClientBuilderProvider,
                observationConvention,
                toolEligibilityPredicate
        );
    }
}