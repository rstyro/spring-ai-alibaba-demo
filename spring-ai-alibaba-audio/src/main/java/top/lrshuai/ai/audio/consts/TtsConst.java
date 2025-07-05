package top.lrshuai.ai.audio.consts;

public interface TtsConst {

    String DEFAULT_TEXT = "我是无敌美少女，喵~";
    // 保存文字转语音的路径
    String FILE_PATH = "spring-ai-alibaba-audio/src/main/resources/gen/tts";

    // 语音模型
    String DEFAULT_MODEL_1 = "sensevoice-v1";
    String DEFAULT_MODEL_2 = "paraformer-realtime-v2";
    String DEFAULT_MODEL_3 = "paraformer-v2";
    // 语音文件路径
    String AUDIO_RESOURCES_URL = "https://dashscope.oss-cn-beijing.aliyuncs.com/samples/audio/paraformer/hello_world_female2.wav";

}
