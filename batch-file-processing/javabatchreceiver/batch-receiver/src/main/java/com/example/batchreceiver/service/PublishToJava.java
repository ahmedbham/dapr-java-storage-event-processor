package com.example.batchreceiver.service;

import org.springframework.stereotype.Service;

import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.Metadata;

import static java.util.Collections.singletonMap;

@Service
public class PublishToJava {
  // Time-to-live for messages published.
  private static final String MESSAGE_TTL_IN_SECONDS = "1000";

  // The title of the topic to be used for publishing
  private static final String TOPIC_NAME = "testingtopic";

  // The name of the pubsub
  private static final String PUBSUB_NAME = "messagebus";

  public void produce(String message) {
    DaprClient client = new DaprClientBuilder().build();
    client.publishEvent(PUBSUB_NAME, TOPIC_NAME, message, singletonMap(Metadata.TTL_IN_SECONDS, MESSAGE_TTL_IN_SECONDS))
        .block();
    System.out.println("Published message: " + message);
  }

}
