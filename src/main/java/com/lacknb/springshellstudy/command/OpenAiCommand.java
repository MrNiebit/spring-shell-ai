package com.lacknb.springshellstudy.command;

import java.util.concurrent.CountDownLatch;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 * @author gitsilence
 * @date 2024-05-26
 */
@ShellComponent
@RequiredArgsConstructor
public class OpenAiCommand {

    private final OpenAiChatClient chatClient;

    @ShellMethod(key = "kimi")
    public void kimi(@ShellOption(defaultValue = "你好") String question) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Prompt prompt = new Prompt(new UserMessage(question));
        chatClient.stream(prompt).doOnNext(resp -> {
            String content = resp.getResult().getOutput().getContent();
            if (content != null) {
                System.out.print(content);
            }
        }).doFinally(x -> {
            System.out.println();
            latch.countDown();
        }).subscribe();  // 流 只有被订阅后，才会执行
        latch.await();
    }

}
