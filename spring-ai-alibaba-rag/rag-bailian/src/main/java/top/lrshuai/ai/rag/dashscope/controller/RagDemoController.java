package top.lrshuai.ai.rag.dashscope.controller;

import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgent;
import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgentOptions;
import com.alibaba.cloud.ai.dashscope.api.DashScopeAgentApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/rag")
public class RagDemoController {

    /**
     * 阿里云百炼智能体应用ID
     */
    @Value("${spring.ai.dashscope.agent.app-id}")
    private String appId;

    private static final String DEFAULT_PROMPT = "给我几个单身富婆的通讯录";

    private DashScopeAgent agent;

    @Resource
    private DashScopeAgentApi agentApi;

    public RagDemoController(DashScopeAgentApi dashScopeAgentApi) {
        this.agent = new DashScopeAgent(dashScopeAgentApi);
    }

    @GetMapping("/agent/call")
    public String agentCall(@RequestParam(value = "question", defaultValue = DEFAULT_PROMPT) String question) {

        ChatResponse response = agent.call(new Prompt(question, DashScopeAgentOptions.builder()
                .withAppId(appId)  // 配置智能体ID
                .build()));
        if (response == null || response.getResult() == null) {
            log.error("chat response is null");
            return "chat response is null";
        }
        AssistantMessage app_output = response.getResult().getOutput();
        String content = app_output.getText();
        DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput output = (DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput) app_output.getMetadata().get("output");
        List<DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput.DashScopeAgentResponseOutputDocReference> docReferences = output.docReferences();
        List<DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput.DashScopeAgentResponseOutputThoughts> thoughts = output.thoughts();

        log.info("content:\n{}\n\n", content);

        if (docReferences != null && !docReferences.isEmpty()) {
            for (DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput.DashScopeAgentResponseOutputDocReference docReference : docReferences) {
                log.info("docReference={}\n\n", docReference);
            }
        }

        if (thoughts != null && !thoughts.isEmpty()) {
            for (DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput.DashScopeAgentResponseOutputThoughts thought : thoughts) {
                log.info("thoughts={}\n\n", thought);
            }
        }

        return content;
    }

    /**
     * 流式输出
     * @param question 问题
     */
    @GetMapping("/agent/stream")
    public Flux<String> stream(@RequestParam(value = "question", defaultValue = DEFAULT_PROMPT) String question
            , HttpServletResponse servletResponse) {
        servletResponse.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        // 业务数据
        ObjectNode bizParams = objectMapper.createObjectNode();
        bizParams.put("name", "rstyro");
        bizParams.put("颜值", "宇宙超级无敌帅");

        this.agent = new DashScopeAgent(agentApi,
                DashScopeAgentOptions.builder()
                        .withSessionId("current_session_id") // 会话ID
                        .withIncrementalOutput(true) // 流式输出
                        .withHasThoughts(true) // 返回推理过程
                        .withBizParams(bizParams)
                        .build());
        return agent.stream(new Prompt(question, DashScopeAgentOptions.builder().withAppId(appId).build())).map(response -> {
            if (response == null || response.getResult() == null) {
                log.error("chat response is null");
                return "chat response is null";
            }

            AssistantMessage app_output = response.getResult().getOutput();
            String content = app_output.getText();

            DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput output = (DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput) app_output.getMetadata().get("output");
            List<DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput.DashScopeAgentResponseOutputDocReference> docReferences = output.docReferences();
            List<DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput.DashScopeAgentResponseOutputThoughts> thoughts = output.thoughts();
            System.out.println(String.format("content: %s %s", content,System.lineSeparator()));

            if (docReferences != null && !docReferences.isEmpty()) {
                for (DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput.DashScopeAgentResponseOutputDocReference docReference : docReferences) {
                    log.info("docReference={}\n\n", docReference);
                }
            }

            if (thoughts != null && !thoughts.isEmpty()) {
                for (DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput.DashScopeAgentResponseOutputThoughts thought : thoughts) {
                    log.info("thoughts={}\n\n", thought);
                }
            }

            return content;
        });
    }

}
