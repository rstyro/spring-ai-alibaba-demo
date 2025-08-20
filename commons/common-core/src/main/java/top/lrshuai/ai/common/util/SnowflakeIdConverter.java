package top.lrshuai.ai.common.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 雪花算法ID与短字符串互转工具类
 * 使用Base62编码实现长整数ID与字符串ID的互相转换
 */
public class SnowflakeIdConverter {
    /**
     * Base62字符集 (0-9, a-z, A-Z)(可以按需调整顺序)
     */
    private static final String BASE_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final char[] CHAR_SET = BASE_CHARS.toCharArray();
    private static final int BASE = CHAR_SET.length;
    private static final Map<Character, Integer> CHAR_INDEX_MAP = new HashMap<>();

    static {
        // 初始化字符索引映射，用于快速查找
        for (int i = 0; i < BASE; i++) {
            CHAR_INDEX_MAP.put(CHAR_SET[i], i);
        }
    }

    /**
     * 将雪花算法生成的长整数ID转换为短字符串
     * @param id 雪花算法ID
     * @return 短字符串ID
     */
    public static String encode(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("ID必须是正数");
        }
        // 处理特殊情况：id为0
        if (id == 0) {
            return String.valueOf(CHAR_SET[0]);
        }
        StringBuilder sb = new StringBuilder();
        // 将长整数转换为Base62
        while (id > 0) {
            // 计算当前数字除以62的余数
            int remainder = (int)(id % BASE);
            // 将余数作为索引，从字符集中获取对应的字符
            sb.append(CHAR_SET[remainder]);
            /**
             * 关键步骤：将id更新为除以62后的整数部分
             * 这相当于将数字向右移动一位(62进制下)
             */
            id = id / BASE;
        }
        // 反转字符串得到正确顺序,因为我们在循环中是从最低位开始添加字符的
        return sb.reverse().toString();
    }

    /**
     * 将短字符串ID转换回雪花算法的长整数ID
     * @param shortId 短字符串ID
     * @return 原始的长整数ID
     */
    public static long decode(String shortId) {
        if (shortId == null || shortId.isEmpty()) {
            throw new IllegalArgumentException("字符ID不能为空");
        }
        long id = 0;
        // 将Base62字符串转换为长整数
        for (int i = 0; i < shortId.length(); i++) {
            char c = shortId.charAt(i);
            if (!CHAR_INDEX_MAP.containsKey(c)) {
                throw new IllegalArgumentException("输入字符串中的字符无效: " + c);
            }
            int digit = CHAR_INDEX_MAP.get(c);
            id = id * BASE + digit;
        }
        return id;
    }

    // 测试示例
    public static void main(String[] args) {
        // 测试一些雪花算法ID
        long[] testIds = {0,1,2,3,4,5,6,7,8,9,10,11,123456789L, 9876543210L, 1234567890123456789L, 1L, 999999999999999999L};

        for (long id : testIds) {
            String shortId = encode(id);
            long decodedId = decode(shortId);

            System.out.println("原始ID: " + id +
                    " -> 短字符串: " + shortId +
                    " -> 解码ID: " + decodedId +
                    " (匹配: " + (id == decodedId) + ")");
        }

        Snowflake snowflake = IdUtil.getSnowflake();
        for (int i = 0; i < 1000; i++) {
            long originalId = snowflake.nextId();
            String shortId = encode(originalId);
            System.out.println("Base62短字符串: " + shortId); // 输出如 "6NtG3fK"

            long decodedId = decode(shortId);
            System.out.println("还原的长整数ID: " + decodedId);
            if(originalId != decodedId){
                System.err.println(String.format("ID不匹配,originalId=%s,decodeId=%s", originalId, decodedId));
            }else {
                System.out.println("ID一致性验证: " + (originalId == decodedId)); // 应输出true
            }
            System.out.println("============");
        }
    }
}
