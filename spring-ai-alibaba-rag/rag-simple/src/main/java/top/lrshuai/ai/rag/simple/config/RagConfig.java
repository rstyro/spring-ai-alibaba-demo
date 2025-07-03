package top.lrshuai.ai.rag.simple.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.document.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class RagConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder clientBuilder) {
        return clientBuilder.defaultSystem("回答问题不要瞎说").build();
    }

    /**
     * SimpleVectorStore 基于内存的向量数据库，用于存储文档的向量表示（支持相似性搜索）
     * @param embeddingModel 将文本转换为向量的模型
     */
    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(embeddingModel).build();
        // metadata(元数据)用途 ：可用于后续过滤或排序（例如按年份筛选文档）
        List<Document> documents = List.of(
                new Document("单身富婆电话号码：18818868688"),
                new Document("广东富婆姐妹群号码：88888888", Map.of("year", 2025)),
                new Document("1", "群主私密号码：1688", new HashMap<>()));
        // 加载文档知识库
        simpleVectorStore.add(documents);
        return simpleVectorStore;
    }

}
