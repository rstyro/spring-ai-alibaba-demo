package top.lrshuai.ai.prompt;

import cn.hutool.core.net.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

@Slf4j
@SpringBootApplication
public class PromptSystemApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(PromptSystemApplication.class, args);
        printfUrl(applicationContext);
    }

    /**
     * 打印地址
     * @param application
     */
    public static void printfUrl(ConfigurableApplicationContext application) {
        Environment env = application.getEnvironment();
        String ip = NetUtil.getLocalhostStr();
        String port = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path","");
        String banner = """
        \n\t
        ----------------------------------------------------------
        Application is running! Access URLs:
        Local: \t\thttp://localhost:%s%s/
        External: \thttp://%s:%s%s/
        ----------------------------------------------------------
        """.formatted(port, contextPath, ip, port, contextPath);
        log.info(banner);
    }

}
