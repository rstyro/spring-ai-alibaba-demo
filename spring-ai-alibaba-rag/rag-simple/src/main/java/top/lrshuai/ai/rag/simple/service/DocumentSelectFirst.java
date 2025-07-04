package top.lrshuai.ai.rag.simple.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.postretrieval.document.DocumentPostProcessor;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 检索后处理阶段
 */
@Slf4j
public class DocumentSelectFirst implements DocumentPostProcessor {

    private static final int TOP_K = 3;

    @Override
    public List<Document> process(Query query, List<Document> documents) {
        log.info("query={}",query.text());
        if(ObjectUtils.isEmpty(documents)) {
            return new ArrayList<>();
        }
        // 过滤时间范围（如 2025 年的文档）
//        List<Document> filtered = documents.stream()
//                .filter(doc -> {
//                    Object year = doc.getMetadata().get("year");
//                    return year != null && Integer.parseInt(year.toString()) >= 2025;
//                })
//                .toList();

        // 取前 TOP_K 个文档
        List<Document> topK = documents.stream()
                .sorted((d1, d2) -> Double.compare(d2.getScore(), d1.getScore()))
                .limit(TOP_K)
                .toList();
        // 随机选择一个，增加多样性。
        Random random = new Random();
        return List.of(topK.get(random.nextInt(topK.size())));
    }
}
