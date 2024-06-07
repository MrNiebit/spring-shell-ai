package com.lacknb.springshellstudy.controller;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;

import java.io.IOException;

/**
 * @author gitsilence
 * @date 2024-05-26
 */
@RestController
@RequestMapping("/test")
@AllArgsConstructor
public class TestController {

    private final OpenAiChatClient chatClient;

    @RequestMapping("/hello")
    public String hello() {
        return "Hello World Spring Boot !";
    }

    @RequestMapping("/chat")
    public SseEmitter chat(@RequestParam(defaultValue = "你好") String msg) {
        SseEmitter emitter = new SseEmitter();
        Prompt prompt = new Prompt(new UserMessage(msg));
        Disposable disposable = chatClient.stream(prompt).doOnNext(resp -> {
            try {
                String content = resp.getResult().getOutput().getContent();
                if (content != null) {
                    emitter.send(content);
                }
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }).subscribe();
        // 当客户端断开连接时取消订阅
        emitter.onCompletion(disposable::dispose);
        emitter.onTimeout(() -> {
            disposable.dispose();
            emitter.complete();
        });
        return emitter;
    }

}
