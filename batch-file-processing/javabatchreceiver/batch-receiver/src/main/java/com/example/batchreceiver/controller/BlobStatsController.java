package com.example.batchreceiver.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.azure.messaging.eventgrid.systemevents.StorageBlobCreatedEventData;
import com.azure.storage.queue.*;
import com.azure.storage.queue.models.*;

import reactor.core.publisher.Mono;

class Stats
{
  int tasks;
} 

@RestController
public class BlobStatsController {

  static final String BINDING_NAME = "dapr-output-queue";
  static final String connectStr = "";
  // EndpointSuffix=core.windows.net";

  @GetMapping("/api/blobstats")
  public ResponseEntity<Stats> getBlobStats() {

    String filesize = "";
    Stats stats = new Stats();
    
    try
    {
        // Instantiate a QueueClient which will be
        // used to create and manipulate the queue
        QueueClient queueClient = new QueueClientBuilder()
                                    .connectionString(connectStr)
                                    .queueName(BINDING_NAME)
                                    .buildClient();

       // Get the first queue message
       QueueMessageItem message = queueClient.receiveMessage();
       

       if (null != message)
       {
           System.out.println("Message retrieved: " + message);
           
           StorageBlobCreatedEventData blobCreatedData = message.getBody().toObject(StorageBlobCreatedEventData.class);
           filesize = blobCreatedData.getContentLength().toString();
           stats.tasks = Integer.valueOf(filesize);          
       }       
    }
    catch (QueueStorageException e)
    {
        // Output the exception message and stack trace
        System.out.println(e.getMessage());
        e.printStackTrace();
    }
    return new ResponseEntity<>(stats, HttpStatus.OK); 
  }
}
