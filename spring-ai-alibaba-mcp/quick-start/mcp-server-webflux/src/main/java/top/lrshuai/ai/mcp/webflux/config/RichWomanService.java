package top.lrshuai.ai.mcp.webflux.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class RichWomanService {

    private final List<RichWoman> database = new ArrayList<>();
    private final Random random = new Random();

    // 名字生成组件
    private final List<String> chineseSurnames = List.of(
            "李", "王", "张", "刘", "陈", "杨", "黄", "周", "吴", "赵",
            "林", "徐", "孙", "马", "朱", "胡", "郭", "何", "高", "郑"
    );

    private final List<String> femaleGivenNames = List.of(
            "婉如", "雅婷", "思琪", "梦瑶", "欣怡", "雨萱", "晓雯", "静怡", "诗涵", "美玲",
            "慧琳", "嘉欣", "雪梅", "丽华", "春燕", "秋月", "紫薇", "若兰", "芷晴", "慧敏",
            "雅静", "燕妮", "薇薇", "晓彤", "梦洁", "心怡", "玉婷", "思思", "雅雯", "诗琪",
            "雨婷", "慧君", "秀英", "丽娜", "芳芳", "婷婷", "艳艳", "燕燕", "莉莉", "娟娟",
            "琳琳", "丹丹", "萍萍", "颖颖", "璐璐", "晶晶", "敏敏", "倩倩", "婷婷", "雪莲"
    );

    // 已使用名字集合（确保名字不重复）
    private final Set<String> usedNames = new HashSet<>();

    // 国内城市列表
    private final List<String> chineseCities = List.of(
            "北京", "上海", "深圳", "广州", "杭州", "成都", "重庆", "南京",
            "武汉", "苏州", "天津", "西安", "长沙", "青岛", "郑州", "宁波",
            "厦门", "香港", "澳门", "台北", "佛山", "东莞", "无锡", "合肥",
            "昆明", "大连", "沈阳", "济南", "福州", "珠海"
    );

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RichWoman(
            String name,
            String phone,     // 新增手机号字段
            int age,
            double fortune,  // 资产（单位：亿元）
            String industry,
            String city,
            String hobby
    ) {}

    public RichWomanService() {
        this.initMockData();
    }

//    @PostConstruct
    public void initMockData() {
        // 国内行业
        String[] industries = {"互联网科技", "房地产开发", "金融投资", "生物医药", "新能源", "影视娱乐",
                "教育培训", "餐饮连锁", "制造业", "电子商务", "文化传媒", "医疗健康"};

        // 业余爱好
        String[] hobbies = {"收藏珠宝", "环球旅行", "艺术品收藏", "慈善事业", "马术", "品茶",
                "高尔夫", "瑜伽冥想", "古典音乐", "书画收藏", "时尚设计", "国学研习"};

        // 生成50位富婆数据（确保名字不重复）
        for (int i = 0; i < 50; i++) {
            String name;
            do {
                name = generateRandomName();
            } while (usedNames.contains(name));

            usedNames.add(name);

            database.add(new RichWoman(
                    name,
                    generateRandomPhone(),
                    25 + random.nextInt(35), // 25-60岁
                    3 + random.nextDouble() * 97, // 3-100亿资产
                    industries[random.nextInt(industries.length)],
                    chineseCities.get(random.nextInt(chineseCities.size())),
                    hobbies[random.nextInt(hobbies.length)]
            ));
        }

        // 添加几位特别富有的角色（确保名字不重复）
        addSpecialWoman("张雨薇", "深圳", "科技投资", 285.7, "AI研究");
        addSpecialWoman("王雅婷", "杭州", "电子商务", 320.5, "慈善事业");
        addSpecialWoman("李静怡", "香港", "房地产开发", 450.2, "书画收藏");
        addSpecialWoman("陈晓雯", "北京", "新能源", 380.0, "国学研习");
        addSpecialWoman("刘诗涵", "上海", "金融投资", 420.8, "古典音乐");
    }

    // 添加特殊富婆
    private void addSpecialWoman(String name, String city, String industry, double fortune, String hobby) {
        if (!usedNames.contains(name)) {
            usedNames.add(name);
            database.add(new RichWoman(
                    name,
                    generateRandomPhone(),
                    35 + random.nextInt(20), // 35-55岁
                    fortune,
                    industry,
                    city,
                    hobby
            ));
        }
    }

    // 生成随机手机号
    private String generateRandomPhone() {
        // 手机号前缀（中国）
        String[] prefixes = {"130", "131", "132", "133", "134", "135", "136", "137", "138", "139",
                "150", "151", "152", "153", "155", "156", "157", "158", "159",
                "180", "181", "182", "183", "184", "185", "186", "187", "188", "189"};

        String prefix = prefixes[random.nextInt(prefixes.length)];
        StringBuilder sb = new StringBuilder(prefix);

        // 生成后8位数字
        for (int i = 0; i < 8; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }

    // 生成随机中文名
    private String generateRandomName() {
        String surname = chineseSurnames.get(random.nextInt(chineseSurnames.size()));
        String givenName = femaleGivenNames.get(random.nextInt(femaleGivenNames.size()));
        return surname + givenName;
    }

    @Tool(description = "获取所有富婆数据")
    public String findAll() {
        log.info("查询所有富婆");
        return formatRichWomen(database);
    }

    @Tool(description = "通过城市名称获取富婆信息")
    public String findByCityName(String cityName) {
        log.info("查询富婆，cityName={}",cityName);
        List<RichWoman> result = database.stream()
                .filter(w -> w.city().contains(cityName.trim()))
                .toList();
        return formatRichWomen(result);
    }

    @Tool(description = "获取资产在某个数值之上的富婆数据")
    public String findByFortuneGreaterThan(@ToolParam(description = "资产数量单位：亿元") double minFortune) {
        log.info("查询富婆，minFortune={}",minFortune);
        List<RichWoman> result = database.stream()
                .filter(w -> w.fortune() >= minFortune)
                .toList();
        return formatRichWomen(result);
    }

    @Tool(description = "通过关键词搜索富婆信息")
    public String search(String keyword) {
        log.info("查询富婆，keyword={}",keyword);
        String lowerKeyword = keyword.toLowerCase();
        List<RichWoman> result = database.stream()
                .filter(w ->
                        w.name().toLowerCase().contains(lowerKeyword) ||
                                w.phone().contains(keyword) ||
                                w.industry().toLowerCase().contains(lowerKeyword) ||
                                w.city().toLowerCase().contains(lowerKeyword) ||
                                w.hobby().toLowerCase().contains(lowerKeyword)
                )
                .toList();
        return formatRichWomen(result);
    }

    @Tool(description = "通过手机号查找富婆")
    public String findByPhone(@ToolParam(description = "手机号码") String phone) {
        log.info("查询富婆，phone={}",phone);
        Optional<RichWoman> result = database.stream()
                .filter(w -> w.phone().equals(phone))
                .findFirst();
        return result.map(this::formatRichWoman)
                .orElse("未找到手机号[" + phone + "]对应的富婆信息");
    }

    // 格式化单个富婆信息为中文
    private String formatRichWoman(RichWoman woman) {
        return String.format(
                "姓名：%s，手机号：%s，年龄：%d岁，资产：%.1f亿元，行业：%s，所在城市：%s，爱好：%s",
                woman.name(), woman.phone(), woman.age(), woman.fortune(),
                woman.industry(), woman.city(), woman.hobby()
        );
    }

    // 格式化富婆列表为中文（多条用换行分隔）
    private String formatRichWomen(List<RichWoman> women) {
        if (women.isEmpty()) {
            return "未找到符合条件的富婆信息";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < women.size(); i++) {
            sb.append(i + 1).append(". ").append(formatRichWoman(women.get(i)));
            if (i < women.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        RichWomanService womanService = new RichWomanService();
        System.out.println( womanService.search("深圳"));
    }
}