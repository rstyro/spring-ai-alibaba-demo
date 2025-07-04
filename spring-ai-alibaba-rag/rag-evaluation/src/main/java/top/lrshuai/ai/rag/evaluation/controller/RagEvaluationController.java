package top.lrshuai.ai.rag.evaluation.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author yingzi
 * @since 2025/6/11
 */
@RestController
@RequestMapping("/ragEvalutaion")
public class RagEvaluationController {

    private static final Logger logger = LoggerFactory.getLogger(RagEvaluationController.class);
    private final SimpleVectorStore simpleVectorStore;
    private final DashScopeChatModel qwenMaxChatModel;
    private final DashScopeChatModel qwenPlusChatModel;

    public RagEvaluationController(EmbeddingModel embeddingModel, @Qualifier("qwen-max") DashScopeChatModel qwenMaxChatModel,
                                   @Qualifier("qwen-plus") DashScopeChatModel qwenPlusChatModel) {
        this.simpleVectorStore = SimpleVectorStore
                .builder(embeddingModel).build();
        this.qwenMaxChatModel = qwenMaxChatModel;
        this.qwenPlusChatModel = qwenPlusChatModel;
    }


    @GetMapping("/add")
    public void add() {
        logger.info("开始添加数据到向量库");
        List<Document> documents = List.of(
                new Document("【富婆档案001】王阿姨，58岁，拥有三家连锁美容院。特长：给年轻人介绍对象。名言：'小帅哥不想努力了，但你可以努力当阿姨女婿呀！'",
                        Map.of("资产", "三个美容院", "爱好", "做媒")),

                new Document("【富婆档案002】李女士，45岁，拆迁获得8套房。日常：每天收租两小时，其余时间跳广场舞。招聘要求：'陪舞小助手，要求体力好，会喊666'",
                        Map.of("资产", "8套房产", "招聘", "广场舞助手")),

                new Document("【富婆档案003】张姐，52岁，海鲜批发女王。特殊技能：能一眼看出龙虾的年龄。恋爱宣言：'跟姐好，顿顿有海鲜，顿顿有医保！'",
                        Map.of("资产", "海鲜帝国", "福利", "海鲜+医保")),

                new Document("【富婆档案004】赵阿姨，60岁，退休数学教授。爱好：教年轻人理财。经典语录：'你的钱放余额宝？来，阿姨教你复利计算公式，保你三年超过阿姨！'",
                        Map.of("资产", "知识财富", "特长", "理财教学")),

                new Document("【富婆档案005】陈奶奶，68岁，神秘古玩收藏家。寻人启事：'找能分清元青花和现代仿品的年轻人，报酬：一件真品+陪奶奶斗地主'",
                        Map.of("资产", "古董收藏", "要求", "鉴宝能力"))
        );
        simpleVectorStore.add(documents);
        logger.info("添加完成！共{}条富婆档案", documents.size());
    }

    @GetMapping("/evalute")
    public String evalute(@RequestParam(value = "query", defaultValue = "阿姨，我不想努力了，请问有什么快速致富的方法吗？") String query) {
        logger.info("开始评估查询：{}", query);
        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(simpleVectorStore)
                        .topK(3)
                        .build())
                .build();

        // 使用 qwen-max 模型生成回答
        ChatResponse chatResponse = ChatClient.builder(qwenMaxChatModel)
                .build().prompt(query).advisors(retrievalAugmentationAdvisor).call().chatResponse();

        String aiResponse = chatResponse.getResult().getOutput().getText();
        logger.info("qwen-max 回答：{}", aiResponse);

        // 创建评估请求
        EvaluationRequest evaluationRequest = new EvaluationRequest(
                query,
                // RAG 检索到的参考文档
                chatResponse.getMetadata().get(RetrievalAugmentationAdvisor.DOCUMENT_CONTEXT),
                // AI 生成的最终回答
                chatResponse.getResult().getOutput().getText()
        );
        logger.info("evalute request: {}", evaluationRequest);
        // 使用 qwen-plus 模型评估相关性
        RelevancyEvaluator evaluator = new RelevancyEvaluator(ChatClient.builder(qwenPlusChatModel));
        EvaluationResponse evaluationResponse = evaluator.evaluate(evaluationRequest);
        boolean pass = evaluationResponse.isPass();
        logger.info("评估结果：{} - {}", pass, evaluationResponse.getFeedback());
        return chatResponse.getResult().getOutput().getText();
    }
}