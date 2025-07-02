package top.lrshuai.ai.toolcall.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.lrshuai.ai.common.resp.R;
import top.lrshuai.ai.toolcall.tool.method.WeatherTool;

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
    @GetMapping("/askCall")
    public R askCall(@RequestParam(value = "question", defaultValue = "深圳今天的天气怎样") String question) {
        /**
         * 在调用 ChatClient 时，通过.toolNames() 传递函数工具的 Bean 名称，
         * 或者在实例化 ChatClient 对象的时候通过 .defalutToolNames() 方法传递函数工具
         */
        return R.ok(chatClient.prompt(question).toolNames("weatherFunction").call().content());
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

}
