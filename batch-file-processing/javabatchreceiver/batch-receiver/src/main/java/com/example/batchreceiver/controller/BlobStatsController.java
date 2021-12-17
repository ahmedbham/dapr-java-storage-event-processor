package com.example.batchreceiver.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

  public int getTasks() {
    return tasks;
  }

  public void setTasks(int tasks) {
    this.tasks = tasks;
  }
  
} 

@RestController
public class BlobStatsController {

  static final String BINDING_NAME = "dapr-output-queue";

  static final String connectStr = "";


  @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
  public Stats getBlobStats() {
    String filesize = "";
    Stats stats = new Stats();
    stats.setTasks(1);

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

        // Check for a specific string
        if (null != message)
        {
            System.out.println("Dequeing message: " + message.getBody().toString());
            int size = Integer.valueOf(message.getBody().toString());
            if (size > 1000) stats.setTasks(2);
            else if (size > 10000) stats.setTasks(3);
            // Delete the message
            queueClient.deleteMessage(message.getMessageId(), message.getPopReceipt());
            
            return stats;
        }
        else
        {
            System.out.println("No visible messages in queue");
            
        }
    }
    catch (QueueStorageException e)
    {
        // Output the exception message and stack trace
        System.out.println(e.getMessage());
        e.printStackTrace();
    }
    
    return stats;
  }

}
