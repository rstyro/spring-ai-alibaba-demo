package top.lrshuai.ai.rag.simple.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.join.ConcatenationDocumentJoiner;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import top.lrshuai.ai.rag.simple.service.DocumentSelectFirst;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yingzi
 * @date 2025/5/29 18:27
 */
@Slf4j
@RestController
@RequestMapping("/ragModule")
public class RagModuleController {

    private final SimpleVectorStore simpleVectorStore;
    private final ChatClient.Builder chatClientBuilder;

    @Value("classpath:data/money.txt")
    private Resource file;

    public RagModuleController(EmbeddingModel embeddingModel, ChatClient.Builder builder) {
        this.simpleVectorStore = SimpleVectorStore
                .builder(embeddingModel).build();
        this.chatClientBuilder = builder;
    }

    @SneakyThrows
    @GetMapping("/add")
    public String add() {
        log.info("开始添加数据");
        // 验证文件是否存在
        if (!file.exists()) {
            return "文件不存在！路径：" + file.getURI();
        }
        // 读取文档
        TextReader textReader = new TextReader(file);
        List<Document> textDocumentList = textReader.get();

        List<Document> documents = new ArrayList<>(List.of(
                new Document("你的姓名是许愿池，问你什么时候发财，一般都是今年"),
                new Document("如果你努力工作，保持积极的心态，那你今年会有好运，今年就会发财，好运往往会眷顾有准备的人！", Map.of("year", 2000)),
                new Document("我在2026年之后开始一飞冲天，从此无病无灾，万事如意，八方来财", Map.of("year", 2026)),
                new Document("我在2025年下半年开始转好运，财富得到积累", Map.of("year", 2025))
        ));
        documents.addAll(textDocumentList);
        simpleVectorStore.add(documents);
        return "success";
    }

    @GetMapping("/ragAdvisor")
    public Flux<String> chatRagAdvisor(@RequestParam(value = "query", defaultValue = "我什么时候发财") String query
            , HttpServletResponse servletResponse) {
        // 不加会乱码
        servletResponse.setCharacterEncoding("UTF-8");
        log.info("开始RAG增加模式");

        // 1. Pre-Retrieval 查询预处理
        // 1.1 MultiQueryExpander 查询变体：生成多个与原始查询语义相关的新查询 ，以提高检索的覆盖率和准确性。默认3个
        MultiQueryExpander multiQueryExpander = MultiQueryExpander.builder()
                .chatClientBuilder(this.chatClientBuilder)
                .numberOfQueries(5)
                .build();
        // 1.2 TranslationQueryTransformer  多语言检索 将中文查询翻译为向量库支持的语言
        TranslationQueryTransformer translationQueryTransformer = TranslationQueryTransformer.builder()
                .chatClientBuilder(this.chatClientBuilder)
                .targetLanguage("English")
                .build();

        // 2. Retrieval  向量检索
        // 2.1 VectorStoreDocumentRetriever
        VectorStoreDocumentRetriever vectorStoreDocumentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(simpleVectorStore)
                .build();
        // 2.2 ConcatenationDocumentJoiner  文档合并
        ConcatenationDocumentJoiner concatenationDocumentJoiner = new ConcatenationDocumentJoiner();

        // 3. Post-Retrieval 检索后处理
        // 3.1 DocumentSelectFirst
        DocumentSelectFirst documentSelectFirst = new DocumentSelectFirst();

        // 4. Generation 上下文增强生成
        // 4.1 ContextualQueryAugmenter 这里会修改prompt 上下文 {context}插入进去，可以看默认的模板：DEFAULT_PROMPT_TEMPLATE
        ContextualQueryAugmenter contextualQueryAugmenter = ContextualQueryAugmenter.builder()
                .allowEmptyContext(true)
                .build();

        // 组装 RAG 流程
        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                // 查询变体扩展
                .queryExpander(multiQueryExpander)
                // 转为英文
                .queryTransformers(translationQueryTransformer)

                // 从向量存储中检索文档
                .documentRetriever(vectorStoreDocumentRetriever)
                // 将检索到的文档进行拼接
                .documentJoiner(concatenationDocumentJoiner)

                // 对检索到的文档进行处理，选择第一个
                .documentPostProcessors(documentSelectFirst)

                // 对生成的查询进行上下文增强
                .queryAugmenter(contextualQueryAugmenter)
                .build();

        return this.chatClientBuilder.build().prompt(query)
                .advisors(retrievalAugmentationAdvisor)
                .stream().content();
    }
}
