package top.lrshuai.ai.toolcall.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.ai.util.json.schema.JsonSchemaGenerator;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.lrshuai.ai.common.resp.R;
import top.lrshuai.ai.toolcall.tool.function.WeatherFunction;
import top.lrshuai.ai.toolcall.tool.method.WeatherTool;

import java.lang.reflect.Method;

/**
 * demo 工具调用
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    private final ChatClient chatClient;

    public DemoController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.
                build();
    }

    /**
     * 默认询问天气
     */
    @GetMapping("/ask")
    public R ask(@RequestParam(value = "question", defaultValue = "深圳今天的天气怎样") String question) {
        return R.ok(chatClient.prompt(question).call().content());
    }

    /**
     * 调用 函数工具
     */
    @GetMapping("/askFunction")
    public R askFunction(@RequestParam(value = "question", defaultValue = "深圳今天的天气怎样") String question) {
        /**
         * 在调用 ChatClient 时，通过.toolNames() 传递函数工具的 Bean 名称，
         * 或者在实例化 ChatClient 对象的时候通过 .defalutToolNames() 方法传递函数工具
         */
        return R.ok(chatClient.prompt(question).toolNames("weatherFunction").call().content());
    }

    /**
     * 开发者也可以不用定义 Bean，直接定义 FunctionToolCallBack 对象，在调用 ChatClient 时通过 .toolCallBacks()
     * 或者在实例化 ChatClient 对象的时候通过 .defalutToolCallBacks() 传递 FunctionToolCallBack 对象：
     */
    @GetMapping("/askFunctionCallback")
    public R askFunctionCallback(@RequestParam(value = "question", defaultValue = "深圳今天的天气怎样") String question) {
        FunctionToolCallback<WeatherFunction.WeatherRequest, String> functionToolCallback = FunctionToolCallback
                .builder("functionCallback", new WeatherFunction())
                .description("获取城市今天的天气")
                .inputType(WeatherFunction.WeatherRequest.class)
                .build();
        return R.ok(chatClient.prompt(question).toolCallbacks(functionToolCallback).call().content());
    }

    /**
     * 调用 方法工具
     */
    @GetMapping("/askByTool")
    public R askByTool(@RequestParam(value = "question", defaultValue = "深圳今天的天气怎样") String question) {
        /**
         * 在调用 ChatClient 时，通过 .tools() 方法传递工具对象，
         * 或者在实例化 ChatClient 对象的时候通过 .defalutTools() 方法传递工具对象
         */
        return R.ok(chatClient.prompt(question)
                // 方法工具调用
                .tools(new WeatherTool())
                .call().content());
    }

    /**
     * 调用 方法工具
     */
    @GetMapping("/askByToolCallback")
    public R askByToolCallback(@RequestParam(value = "question", defaultValue = "深圳今天的天气怎样") String question) {
        /**
         * 可以使用 JsonSchemaGenerator.generateForMethodInput(method) 方法获取 Input Schema。
         * 在调用 ChatClient 时，通过.toolCallbacks() 传递 MethodToolCallBack 对象，
         * 或者在实例化 ChatClient 对象的时候通过 .defalutToolCallBacks() 方法传递工具对象：
         */
        Method method = ReflectionUtils.findMethod(WeatherTool.class, "geCityWeatherInfo", String.class);
        String inputSchema = JsonSchemaGenerator.generateForMethodInput(method);
        MethodToolCallback toolCallback = MethodToolCallback.builder()
                .toolDefinition(ToolDefinition.builder()
                        .description("获取城市天气信息")
                        .name("geCityWeatherInfo")
                        .inputSchema(inputSchema)
                        .build())
                .toolMethod(method)
                .toolObject(new WeatherTool())
                .build();
        return R.ok(chatClient.prompt(question)
                // 方法工具调用
                .toolCallbacks(toolCallback)
                .call().content());
    }

}
