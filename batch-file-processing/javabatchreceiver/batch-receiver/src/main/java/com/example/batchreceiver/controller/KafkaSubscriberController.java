package com.example.batchreceiver.controller;

import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.CloudEvent;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class KafkaSubscriberController {

  static final String BINDING_NAME = "outputqueue";

  static final String BINDING_OPERATION = "create";

  @PostMapping(path = "/testingtopic")
  public Mono<Void> handleMessage(@RequestBody(required = false) CloudEvent<String> cloudEvent) {
    return Mono.fromRunnable(() -> {
      try (DaprClient client = new DaprClientBuilder().build()) {
        String url = cloudEvent.getData();
        System.out.println("kafka message received: " + url);

        client.invokeBinding(BINDING_NAME, BINDING_OPERATION, url).block();
        client.close();

      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }

}
