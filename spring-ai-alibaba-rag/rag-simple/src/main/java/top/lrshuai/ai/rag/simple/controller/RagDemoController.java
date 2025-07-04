package top.lrshuai.ai.rag.simple.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
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
        // QuestionAnswerAdvisor是一个轻量级、开箱即用的 RAG 实现
        QuestionAnswerAdvisor questionAnswerAdvisor = new QuestionAnswerAdvisor(simpleVectorStore);
        return chatClient.prompt(prompt).advisors(questionAnswerAdvisor).call().content();
    }

    @GetMapping("/ragAdvisor")
    public String chatRagAdvisor(@RequestParam(value = "prompt", defaultValue = DEFAULT_PROMPT) String prompt) {
        log.info("start chat with rag-advisor");
        // RetrievalAugmentationAdvisor 提供完整的 RAG 流程框架，支持高度定制化和模块化组合
        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(simpleVectorStore)
                        .build())
                .build();
        return chatClient.prompt(prompt)
                .advisors(retrievalAugmentationAdvisor)
                .call().content();
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
