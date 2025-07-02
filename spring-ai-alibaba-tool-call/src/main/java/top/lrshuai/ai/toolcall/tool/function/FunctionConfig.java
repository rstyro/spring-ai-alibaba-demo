package top.lrshuai.ai.toolcall.tool.function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Configuration
public class FunctionConfig {

    /**
     * 函数调用类
     */
    @Bean
    @Description("获取指定城市天气的信息")
    public Function<WeatherFunction.WeatherRequest, String> weatherFunction() {
        return new WeatherFunction();
    }
}
