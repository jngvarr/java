package ru.gb.sem12_example2.config;

import com.rometools.rome.feed.synd.SyndEntry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.feed.dsl.Feed;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.transformer.AbstractPayloadTransformer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

@Configuration
public class IntegrationConfig {

    @Bean
    public IntegrationFlow fitFlow() throws MalformedURLException {
        return IntegrationFlow.from(Feed.inboundAdapter(
                                new URL("https://lenta.ru/rss"), "news"),
                        e -> e.poller(p -> p.fixedDelay(5000))).transform(extractLinkFromRss())
                .handle(messageHandler()).get();
    }

    @Bean
//    @Transformer
    public AbstractPayloadTransformer<SyndEntry, String> extractLinkFromRss() {
        return new AbstractPayloadTransformer<SyndEntry, String>() {
            @Override
            protected String transformPayload(SyndEntry payload) {
                return "Title: " + payload.getTitle() + "\n" + "Author: " + payload.getAuthor() + "\n" + "link: " + payload.getLink();
            }
        };
    }

    @Bean
//    @ServiceActivator()
    public FileWritingMessageHandler messageHandler() {
        FileWritingMessageHandler handler =
                new FileWritingMessageHandler(new File("../files"));
        handler.setExpectReply(false);
        handler.setFileExistsMode(FileExistsMode.APPEND);
        handler.setAppendNewLine(true);
        handler.setAppendNewLine(true);
        handler.setCharset("UTF8");
        handler.setExpectReply(false);

        return handler;

    }
}
