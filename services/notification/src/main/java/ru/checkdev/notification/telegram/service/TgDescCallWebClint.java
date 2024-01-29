package ru.checkdev.notification.telegram.service;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.checkdev.notification.domain.TopicDTO;

import java.util.List;

@Setter
@Service
@Slf4j
public class TgDescCallWebClint {
    private WebClient webClient;

    public TgDescCallWebClint(@Value("${server.desc}") String urlDesc) {
        this.webClient = WebClient.create(urlDesc);
    }

    public Mono<List<TopicDTO>> doGetList(String url) {
        return webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<TopicDTO>>() {})
                .doOnError(err -> log.error("API not found: {}", err.getMessage()));
    }

}
