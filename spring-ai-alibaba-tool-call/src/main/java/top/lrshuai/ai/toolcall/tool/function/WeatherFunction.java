package top.lrshuai.ai.toolcall.tool.function;

import lombok.Data;

import java.util.function.Function;

public class WeatherFunction implements Function<WeatherFunction.WeatherRequest, String> {

    @Override
    public String apply(WeatherRequest weatherRequest) {
        System.out.println("cityName="+weatherRequest.getCityName());
        // todo 这里可以通过cityName 去请求真实的天气api
        return String.format("%s 天气是晴天",weatherRequest.getCityName());
    }

    @Data
    public static class WeatherRequest{
        private String cityName;
    }
}
