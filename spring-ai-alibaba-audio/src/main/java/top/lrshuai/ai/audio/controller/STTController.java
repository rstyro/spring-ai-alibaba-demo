package top.lrshuai.ai.audio.controller;

import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioTranscriptionOptions;
import com.alibaba.cloud.ai.dashscope.audio.transcription.AudioTranscriptionModel;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.lrshuai.ai.audio.consts.TtsConst;
import top.lrshuai.ai.common.resp.R;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

/**
 * STT(Speech-to-Text) 语音转文字
 */
@Slf4j
@RestController
@RequestMapping("/stt")
public class STTController {

    @Resource
    private AudioTranscriptionModel transcriptionModel;

    /**
     * 文字转语音下载
     */
    @SneakyThrows
    @GetMapping("/audio")
    public R audio(@RequestParam(defaultValue = TtsConst.AUDIO_RESOURCES_URL) String url) {
        UrlResource urlResource = new UrlResource(url);
        AudioTranscriptionPrompt audioTranscriptionPrompt = new AudioTranscriptionPrompt(urlResource,
                DashScopeAudioTranscriptionOptions.builder()
                        .withModel(TtsConst.DEFAULT_MODEL_1)
                        .build());
        return R.ok(transcriptionModel.call(audioTranscriptionPrompt).getResult().getOutput());
    }

    /**
     * 文字转语音下载
     */
    @SneakyThrows
    @GetMapping("/audioByFilePath")
    public String audioByFilePath(String filePath,@RequestParam(defaultValue = TtsConst.DEFAULT_MODEL_2) String modelName) {
        Path path = Paths.get(TtsConst.FILE_PATH ,filePath);
        log.info("path={}",path.toFile().getAbsolutePath());
        FileSystemResource fileSystemResource = new FileSystemResource(path);
        AudioTranscriptionPrompt audioTranscriptionPrompt = new AudioTranscriptionPrompt(fileSystemResource,
                DashScopeAudioTranscriptionOptions.builder()
                        .withModel(modelName)
                        .withFormat(DashScopeAudioTranscriptionOptions.AudioFormat.MP3)
                        .build());
        // 报错
//        transcriptionModel.call(audioTranscriptionPrompt).getResult().getOutput();
        CountDownLatch latch = new CountDownLatch(1);
        StringBuilder stringBuilder = new StringBuilder();
        transcriptionModel.stream(audioTranscriptionPrompt)
                .doFinally(signal -> latch.countDown())
                .subscribe(resp -> {
                    String output = resp.getResult().getOutput();
                    stringBuilder.append(output);
                    System.out.println("内容=="+output);
                });
        latch.await();
        return stringBuilder.toString();
    }

}
