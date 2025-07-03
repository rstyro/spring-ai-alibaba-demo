package top.lrshuai.ai.rag.simple.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/rag")
public class RagDemoController {

    private static final String DEFAULT_PROMPT = "广东富婆的号码是多少";

    @Resource
    private VectorStore simpleVectorStore;

    @Resource
    private ChatClient chatClient;

    @GetMapping("/simple")
    public String simpleRag(@RequestParam(value = "prompt", defaultValue = DEFAULT_PROMPT) String prompt) {
           return chatClient.prompt(prompt)
                    .advisors(new QuestionAnswerAdvisor(simpleVectorStore)).call().content();
    }


}
