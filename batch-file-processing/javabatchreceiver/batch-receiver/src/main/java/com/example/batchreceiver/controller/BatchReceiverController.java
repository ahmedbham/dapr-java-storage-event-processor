package com.example.batchreceiver.controller;

import java.util.List;

import com.azure.core.util.BinaryData;
import com.azure.messaging.eventgrid.EventGridEvent;
import com.azure.messaging.eventgrid.systemevents.StorageBlobCreatedEventData;
import com.azure.messaging.eventgrid.systemevents.StorageBlobDeletedEventData;
import com.example.batchreceiver.service.PublishToJava;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class BatchReceiverController {

  public static final String validationEventType = "Microsoft.EventGrid.SubscriptionValidationEvent";
  public static final String storageBlobCreatedEvent = "Microsoft.Storage.BlobCreated";
  public static final String storageBlobDeletedEvent = "Microsoft.Storage.BlobDeleted";

  @Autowired
  private PublishToJava publishToJava;

  /**
   * Handles input binding from Dapr.
   * 
   * @param body Content from Dapr's sidecar.
   * @return String Mono.
   */
  @PostMapping(path = "/batchqueue")
  public Mono<String> handleInputBinding(@RequestBody(required = false) String item) {

    return Mono.fromRunnable(() -> {
      try {

        System.out.println("message from storage queue: " + item);
        List<EventGridEvent> events = EventGridEvent.fromString(item);

        for (EventGridEvent event : events) {
          BinaryData eventData = event.getData();
          switch (event.getEventType()) {
          case validationEventType:
            System.out.println("validation event received");
            break;
          case storageBlobCreatedEvent:

            StorageBlobCreatedEventData blobCreatedData = eventData.toObject(StorageBlobCreatedEventData.class);
            System.out.println("blob created url : " + blobCreatedData.getUrl());
            System.out.println("blob created url : " + blobCreatedData.getContentLength());
            publishToJava.produce(blobCreatedData.getUrl());
            break;
          case storageBlobDeletedEvent:

            StorageBlobDeletedEventData blobDeletedData = eventData.toObject(StorageBlobDeletedEventData.class);
            System.out.println("blob deleted url : " + blobDeletedData.getUrl());
            publishToJava.produce(blobDeletedData.getUrl());
            break;
          default:
            System.out.printf("%s isn't an AppConfiguration event data%n", event.getEventType());
            break;
          }
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });

  }

}
