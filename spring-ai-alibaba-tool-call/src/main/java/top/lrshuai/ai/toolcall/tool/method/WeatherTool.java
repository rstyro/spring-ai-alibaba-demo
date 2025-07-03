package top.lrshuai.ai.toolcall.tool.method;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 天气方法工具
 */
public class WeatherTool {

    @Tool(description = "获取城市天气信息")
    public String getWeatherInfo(@ToolParam(description = "城市名称") String city) {
        // todo 请求具体的接口得到城市天气
        return String.format("%s 天气是多云转晴，温度38.9",city);
    }

    /**
     * 没有注解，用与toolCallback回调
     */
    public String geCityWeatherInfo(String city) {
        // todo 请求具体的接口得到城市天气
        return String.format("%s 天气是小雨多云，微风清爽",city);
    }
}
