package top.lrshuai.ai.toolcall.tool.method;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 方法工具
 */
public class WeatherTool {

    @Tool(description = "获取城市天气信息")
    public String getWeatherInfo(@ToolParam(description = "城市名称") String city) {
        return String.format("%s 天气是多云转晴，温度38.9",city);
    }
}
