package com.example.batchreceiver.service;

import org.springframework.stereotype.Service;

import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.Metadata;

import static java.util.Collections.singletonMap;

@Service
public class PublishToJava {

  static final String BINDING_NAME = "outputqueue";

  static final String BINDING_OPERATION = "create";

  public void produce(Long contentLength) {
    try {
      DaprClient client = new DaprClientBuilder().build();

      client.invokeBinding(BINDING_NAME, BINDING_OPERATION, contentLength).block();
      client.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Published message: " + contentLength);
  }

}
