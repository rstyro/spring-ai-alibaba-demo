package top.lrshuai.ai.audio.controller;

import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioSpeechOptions;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisModel;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisPrompt;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.lrshuai.ai.audio.consts.TtsConst;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

/**
 * TTS(Text-to-Speech) 文字转语音
 */
@Slf4j
@RestController
@RequestMapping("/tts")
public class TTSController implements ApplicationRunner {

    @Resource
    private SpeechSynthesisModel speechSynthesisModel;


    /**
     * 文字转语音下载
     */
    @GetMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam(defaultValue = TtsConst.DEFAULT_TEXT)  String content,
                                           @RequestParam(defaultValue = "sambert-zhichu-v1")String model,
                                           @RequestParam(required = false)String voice) {
        // 使用正确的实现类构建选项
        DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions
                .builder()
                .model(model)
                .volume(80)
                .build();

        // 配置音色
        if (voice != null && !voice.trim().isEmpty()) {
            options.setVoice(voice);
        }
        SpeechSynthesisPrompt speechSynthesisPrompt = new SpeechSynthesisPrompt(content, options);
        // 语音合成
        SpeechSynthesisResponse speechSynthesisResponse = speechSynthesisModel.call(speechSynthesisPrompt);
        ByteBuffer audioBuffer = speechSynthesisResponse.getResult().getOutput().getAudio();
        byte[] audioByte = new byte[audioBuffer.remaining()];
        audioBuffer.get(audioByte);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=output.mp3")
                .body(audioByte);
    }

    @GetMapping("/stream")
    public void streamTTS(@RequestParam(defaultValue = TtsConst.DEFAULT_TEXT)  String content) {
        Path outputDir = Paths.get(TtsConst.FILE_PATH);
        // 构建带时间戳的输出文件
        Path outputFile = outputDir.resolve("output-stream-" +  Instant.now().toEpochMilli() + ".mp3");

        // 使用CompletableFuture异步处理避免阻塞Servlet线程
        CompletableFuture.runAsync(() -> {
            try (FileOutputStream fos = new FileOutputStream(outputFile.toFile())) {
                speechSynthesisModel.stream(new SpeechSynthesisPrompt(content))
                        .doOnNext(response -> {
                            ByteBuffer buffer = response.getResult().getOutput().getAudio();
                            byte[] bytes = new byte[buffer.remaining()];
                            buffer.get(bytes);
                            try {
                                fos.write(bytes); // 流式写入文件
                                fos.flush();
                            } catch (IOException e) {
                                throw new RuntimeException("写入文件失败", e);
                            }
                        })
                        .doOnError(e -> {
                            log.error("音频流处理失败", e);
                            deleteFileSilently(outputFile);
                        })
                        .doOnComplete(() -> log.info("音频生成完成: {}", outputFile))
                        .blockLast(); // 需阻塞等待流完成
            } catch (IOException e) {
                throw new RuntimeException("文件操作失败", e);
            }

        }).exceptionally(ex -> {
            // 统一异常处理
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
            log.error("TTS处理失败: {}", cause.getMessage(), cause);
            return null;
        });
    }

    @Override
    public void run(ApplicationArguments args) {
        File file = new File(TtsConst.FILE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

//    @PreDestroy
    public void destroy(){
        // 删除默认的示例路径
        try {
            Path dir =  Paths.get(TtsConst.FILE_PATH);
            if (Files.exists(dir)) {
                Files.walk(dir)
                        .sorted(Comparator.reverseOrder()) // 反向排序先删文件
                        .forEach(this::deleteFileSilently);
                log.info("已清理TTS目录: {}", dir);
            }
        } catch (IOException e) {
            log.error("目录清理失败", e);
        }
    }

    private void deleteFileSilently(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            log.warn("文件删除失败: {}", path, ex);
        }
    }
}
