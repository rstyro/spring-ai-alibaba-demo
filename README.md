# spring-ai-alibaba-demo
spring-ai-alibaba-demo

## 参考自：
- [spring-ai-alibaba-examples](https://github.com/springaialibaba/spring-ai-alibaba-examples)
- [spring-ai-tutorial](https://github.com/GTyingzi/spring-ai-tutorial)


## 环境：

- JDK17
- SpringBoot3.4.0
- SpringAI 1.0.0
- SpringAI Alibaba 1.0.0.2

## 项目目录

- **commons** 公共包，通用接口返回和全局异常捕获，可不要
- **spring-ai-alibaba-chat** 聊天模块
  - **chat-dashscope** 基于阿里百炼模型的聊天demo
  - **chat-deepseek** 基于deepseek模型的聊天demo
- **spring-ai-alibaba-images** 基于阿里百炼模型的文生图demo
- **spring-ai-alibaba-rag**  RAG相关demo
  - **rag-bailian**  基于阿里百炼模型的
  - **rag-evaluation** 这个是RAG评估结果
  - **rag-simple**  这个是快速入门的RAG示例
- **spring-ai-alibaba-tool-call** 工具函数调用demo
- **spring-ai-alibaba-audio** 语音合成与解析demo
- **spring-ai-alibaba-mcp** MCP相关demo
  - **quick-start** 快速开始
    - **mcp-server** MCP服务
    - **mcp-client** mcp客户端，测试
    - **mcp-server-webflux** MCP webflux服务
    - **mcp-client-webflux** mcp webflux客户端，测试