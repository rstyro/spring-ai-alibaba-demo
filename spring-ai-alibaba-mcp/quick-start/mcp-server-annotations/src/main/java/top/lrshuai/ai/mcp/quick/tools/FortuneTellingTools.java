package top.lrshuai.ai.mcp.quick.tools;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import org.springaicommunity.mcp.annotation.McpProgressToken;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 趣味算命工具类
 */
@Component
public class FortuneTellingTools {

    private final Random random = new Random();

    // 运势等级
    private final String[] LUCK_LEVELS = {"大吉", "中吉", "小吉", "平", "凶", "大凶"};

    // 运势描述
    private final String[][] LUCK_DESCRIPTIONS = {
            {"诸事顺利，心想事成", "好运连连，惊喜不断", "贵人相助，事半功倍"},
            {"平稳发展，小有收获", "努力可见成效", "人际关系和谐"},
            {"稍有波折，但无大碍", "需要多加努力", "注意细节问题"},
            {"平平淡淡才是真", "保持现状就好", "顺其自然"},
            {"今日不宜做重大决定", "小心意外状况", "注意身体健康"},
            {"诸事不宜，低调行事", "谨言慎行，避免冲突", "保重身体最重要"}
    };

    // 幸运颜色
    private final String[] LUCKY_COLORS = {"红色", "蓝色", "绿色", "黄色", "紫色", "白色", "黑色", "金色"};

    // 幸运数字
    private final int[] LUCKY_NUMBERS = {7, 8, 3, 6, 9, 1, 5, 2};

    // 宜做事项
    private final String[] RECOMMENDED_ACTIONS = {
            "喝一杯奶茶", "给朋友发个消息", "学习新知识", "整理房间",
            "早睡半小时", "吃顿好的", "运动一下", "看部电影",
            "读一本好书", "写日记", "听音乐", "做规划"
    };

    // 忌做事项
    private final String[] AVOID_ACTIONS = {
            "熬夜", "冲动消费", "与人争执", "吃太多零食",
            "拖延工作", "过度思考", "暴饮暴食", "长时间刷手机"
    };

    /**
     * 综合今日运势测算
     */
    @McpTool(name = "daily_fortune", description = "测算今日综合运势，包含幸运颜色、数字等")
    public String getDailyFortune(
            @McpProgressToken String progressToken,
            McpSyncServerExchange exchange,
            @McpToolParam(description = "你的名字") String name) {

        // Send logging notification
        exchange.loggingNotification(McpSchema.LoggingMessageNotification.builder()
                .level(McpSchema.LoggingLevel.INFO)
                .data("工具调用姓名为: " + name)
                .build());
//        String progressToken = IdUtil.fastSimpleUUID();


        int luckIndex = random.nextInt(LUCK_LEVELS.length);
        String luckLevel = LUCK_LEVELS[luckIndex];
        String luckDesc = LUCK_DESCRIPTIONS[luckIndex][random.nextInt(LUCK_DESCRIPTIONS[luckIndex].length)];
        String luckyColor = LUCKY_COLORS[random.nextInt(LUCKY_COLORS.length)];
        int luckyNumber = LUCKY_NUMBERS[random.nextInt(LUCKY_NUMBERS.length)];
        String recommended = RECOMMENDED_ACTIONS[random.nextInt(RECOMMENDED_ACTIONS.length)];
        String avoid = AVOID_ACTIONS[random.nextInt(AVOID_ACTIONS.length)];

        // 发送进度
        exchange.progressNotification(new McpSchema.ProgressNotification(
                progressToken, 0.5, 1.0, "Processing..."));

        // 生成综合评分（0-100）
        int overallScore = 60 + luckIndex * 8 + random.nextInt(20);

        // 根据运势等级添加不同的表情
        String[] emojis = {"🎉", "😊", "🙂", "😐", "😟", "💀"};
        String finalEmoji = emojis[luckIndex];
        // 发送进度
        exchange.progressNotification(new McpSchema.ProgressNotification(
                progressToken, 0.9, 1.0, "准备返回数据"));

        return """
            🔮 %s 的今日运势报告
                        
            📊 综合评分：%d/100
            ✨ 运势等级：%s
            💫 运势解读：%s
            🎨 幸运颜色：%s
            🔢 幸运数字：%d
            ✅ 今日宜：%s
            ❌ 今日忌：%s
                        
            %s 保持好心情，明天会更好！
            """.formatted(name, overallScore, luckLevel, luckDesc, luckyColor,
                luckyNumber, recommended, avoid, finalEmoji);
    }

    /**
     * 缘分指数测算
     */
    @McpTool(name = "fate_index", description = "测算两人之间的缘分指数")
    public String calculateFateIndex(
            @McpToolParam(description = "第一个人名") String person1,
            @McpToolParam(description = "第二个人名") String person2) {

        // 基于名字长度和字符生成一个"伪随机"但可重复的分数
        int baseScore = Math.abs((person1 + person2).hashCode()) % 101;
        // 添加一些随机波动
        int finalScore = Math.min(100, Math.max(0, baseScore + random.nextInt(21) - 10));

        String level, description, advice;
        String[] emojis;

        if (finalScore >= 90) {
            level = "天作之合";
            description = "你们之间的缘分很深，默契十足！";
            advice = "好好珍惜这段缘分吧～";
            emojis = new String[]{"💖", "🌟", "🎉"};
        } else if (finalScore >= 70) {
            level = "缘分不错";
            description = "你们相处会很愉快，有很多共同话题";
            advice = "多交流会让关系更好哦";
            emojis = new String[]{"👍", "😊", "✨"};
        } else if (finalScore >= 50) {
            level = "普通缘分";
            description = "缘分适中，需要双方共同努力";
            advice = "主动一点，缘分就会更深厚";
            emojis = new String[]{"🙂", "🔮", "💫"};
        } else if (finalScore >= 30) {
            level = "缘分尚浅";
            description = "缘分还需要时间来培养";
            advice = "顺其自然，不必强求";
            emojis = new String[]{"🤔", "🌀", "🌙"};
        } else {
            level = "缘分未到";
            description = "目前缘分比较浅薄";
            advice = "也许时机还未成熟";
            emojis = new String[]{"🌌", "💤", "⌛"};
        }

        String emoji = emojis[random.nextInt(emojis.length)];

        return """
            %s %s 与 %s 的缘分测算：
                        
            💝 缘分指数：%d%%
            📈 缘分等级：%s
            📖 缘分解读：%s
            💡 缘分建议：%s
                        
            %s 缘分天注定，事在人为！
            """.formatted(emoji, person1, person2, finalScore, level, description, advice, emoji);
    }

    /**
     * 财运预测
     */
    @McpTool(name = "wealth_fortune", description = "预测今日财运情况")
    public String getWealthFortune(@McpToolParam(description = "你的名字") String name) {

        int wealthLevel = random.nextInt(5); // 0-4，0最差，4最好
        String[] levels = {"财运不佳", "财运平平", "小有财运", "财运亨通", "财运爆棚"};
        String[] descriptions = {
                "今天可能会有些小破财，注意保管财物",
                "财运平稳，收支平衡",
                "可能有意外小收入，比如捡到钱或者收到红包",
                "投资理财有好运，但也要谨慎",
                "财运大好，可能会有惊喜收入！"
        };
        String[] advices = {
                "今日不宜投资，捂紧钱包",
                "正常消费，理性购物",
                "可以买张彩票试试手气",
                "考虑小额投资或理财",
                "机会难得，但要保持理性"
        };

        // 生成具体金额预测（娱乐性质）
        int minAmount = wealthLevel * 50;
        int maxAmount = wealthLevel * 200 + 100;
        String amountPrediction = wealthLevel > 0 ?
                "可能涉及金额：%d-%d元".formatted(minAmount, maxAmount) :
                "可能损失金额：不超过100元";

        return """
            💰 %s 的今日财运预测：
                        
            📊 财运等级：%s
            📈 财运分析：%s
            💸 %s
            💡 财运建议：%s
                        
            🎯 记住：小富靠勤，大富靠命！
            """.formatted(name, levels[wealthLevel], descriptions[wealthLevel],
                amountPrediction, advices[wealthLevel]);
    }


    /**
     * 趣味人生建议
     */
    @McpTool(name = "life_advice", description = "根据当前心情给出趣味人生建议")
    public String getLifeAdvice(
            @McpToolParam(description = "你的名字", required = true) String name,
            @McpToolParam(description = "当前心情描述", required = false) String mood) {

        String[][] advicePool = {
                // 开心时的建议
                {"继续保持笑容！", "把快乐传递给别人", "记录下这美好的时刻", "做些创造性的事情"},
                // 平静时的建议
                {"享受当下的宁静", "读一本好书", "整理思绪", "规划未来"},
                // 疲惫时的建议
                {"好好休息一下", "喝杯热饮放松", "听听轻音乐", "早点睡觉"},
                // 迷茫时的建议
                {"和朋友聊聊天", "出去散散步", "尝试新事物", "给自己放个假"},
                // 动力不足时的建议
                {"设定小目标", "奖励自己一下", "寻找灵感", "回顾过去的成就"}
        };

        int moodIndex;
        if (mood == null || mood.isEmpty()) {
            moodIndex = random.nextInt(advicePool.length);
        } else {
            // 简单的心情关键词匹配
            String lowerMood = mood.toLowerCase();
            if (lowerMood.contains("开心") || lowerMood.contains("高兴") || lowerMood.contains("快乐")) {
                moodIndex = 0;
            } else if (lowerMood.contains("平静") || lowerMood.contains("安静") || lowerMood.contains("淡定")) {
                moodIndex = 1;
            } else if (lowerMood.contains("累") || lowerMood.contains("疲惫") || lowerMood.contains("困")) {
                moodIndex = 2;
            } else if (lowerMood.contains("迷茫") || lowerMood.contains("困惑") || lowerMood.contains("不知道")) {
                moodIndex = 3;
            } else if (lowerMood.contains("无聊") || lowerMood.contains("没动力") || lowerMood.contains("拖延")) {
                moodIndex = 4;
            } else {
                moodIndex = random.nextInt(advicePool.length);
            }
        }

        String[] suggestions = advicePool[moodIndex];
        String chosenAdvice = suggestions[random.nextInt(suggestions.length)];

        String[] philosophicalQuotes = {
                "人生就像骑自行车，想保持平衡就得往前走",
                "每一天都是新的开始，每一刻都值得珍惜",
                "快乐不在于拥有多少，而在于计较多少",
                "最好的总会在最不经意的时候出现",
                "生活不是等待风暴过去，而是学会在雨中跳舞"
        };

        String quote = philosophicalQuotes[random.nextInt(philosophicalQuotes.length)];
        String moodDescription = mood != null ? mood : "未知（随机建议）";
        return """
            🌟 给 %s 的人生建议：
                        
            💭 当前心情：%s
            🎯 建议行动：%s
                        
            📚 哲理时刻：%s
                        
            💝 愿你今天也有好心情！
            """.formatted(name, moodDescription, chosenAdvice, quote);
    }

    /**
     * 今日幸运物推荐
     */
    @McpTool(name = "lucky_item", description = "推荐今日幸运物品")
    public String getLuckyItem(
            @McpToolParam(description = "你的名字") String name) {

        String[] items = {
                "幸运钥匙扣", "红色笔记本", "水晶挂饰", "幸运手链",
                "特定颜色的笔", "幸运硬币", "护身符", "幸运贴纸",
                "特定图案的袜子", "幸运挂件", "能量石", "幸运香囊"
        };

        String[] benefits = {
                "带来好运和正能量", "提升工作效率", "增强人际关系",
                "带来财运", "保护平安", "提升自信心", "吸引贵人", "化解小人"
        };

        String item = items[random.nextInt(items.length)];
        String benefit = benefits[random.nextInt(benefits.length)];
        String placement = "可以放在包包里或桌面上";

        return """
            🍀 %s 的今日幸运物推荐：
                        
            🎁 幸运物品：%s
            ✨ 物品功效：%s
            📍 摆放建议：%s
                        
            💫 相信幸运，幸运就会降临！
            """.formatted(name, item, benefit, placement);
    }

    /**
     * 今日幸运食物推荐
     */
    @McpTool(name = "lucky_food", description = "推荐今日能带来好运的食物")
    public String getLuckyFood(@McpToolParam(description = "你的名字") String name) {

        String[] foods = {
                "🍎 苹果 - 象征平安", "🍊 橙子 - 象征成功", "🍓 草莓 - 象征甜蜜",
                "🍌 香蕉 - 象征招财", "🍇 葡萄 - 象征丰收", "🥬 青菜 - 象征健康",
                "🍫 巧克力 - 象征快乐", "🍵 绿茶 - 象征清净", "🍚 米饭 - 象征富足"
        };

        String[] effects = {
                "吃下去会带来一天的好心情",
                "享用后可能会有小惊喜",
                "品尝时记得心怀感恩",
                "慢慢享用，好运自然来",
                "与朋友分享会加倍幸运"
        };

        String food = foods[random.nextInt(foods.length)];
        String effect = effects[random.nextInt(effects.length)];

        return """
            🍽️ %s 的今日幸运食物：
                        
            🍴 推荐食物：%s
            ✨ 食用效果：%s
            💡 小贴士：最好在心情愉快时享用
                        
            🎉 美食与好运同在！
            """.formatted(name, food, effect);
    }
}
