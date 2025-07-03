package top.lrshuai.ai.rag.simple.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
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

    @GetMapping("/search")
    public List<Document> search(@RequestParam(defaultValue = "号码") String query) {
        log.info("start search data");
        return simpleVectorStore.similaritySearch(SearchRequest
                .builder()
                .query(query)
                .topK(3)
                .build());
    }

}
