package com.example.batchreceiver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.azure.storage.queue.*;
import com.azure.storage.queue.models.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;

class Stats
{
  int tasks;

  public int gettasks() {return tasks;}
  public void settasks(int value) { tasks = value;} 
} 

class myEventGridEvent {
    private String topic;
    private String subject;
    private String eventType;
    private String id;
    private Data data;
    private String dataVersion;
    private String metadataVersion;
    private String eventTime;

    public String getTopic() { return topic; }
    public void setTopic(String value) { this.topic = value; }

    public String getSubject() { return subject; }
    public void setSubject(String value) { this.subject = value; }

    public String getEventType() { return eventType; }
    public void setEventType(String value) { this.eventType = value; }

    public String getID() { return id; }
    public void setID(String value) { this.id = value; }

    public Data getData() { return data; }
    public void setData(Data value) { this.data = value; }

    public String getDataVersion() { return dataVersion; }
    public void setDataVersion(String value) { this.dataVersion = value; }

    public String getMetadataVersion() { return metadataVersion; }
    public void setMetadataVersion(String value) { this.metadataVersion = value; }

    public String getEventTime() { return eventTime; }
    public void setEventTime(String value) { this.eventTime = value; }
}

class Data {
    private String api;
    private String requestId;
    private String eTag;
    private String contentType;
    private long contentLength;
    private String blobType;
    private String blobUrl;
    private String url;
    private String sequencer;
    private String identity;
    private StorageDiagnostics storageDiagnostics;

    public String getAPI() { return api; }
    public void setAPI(String value) { this.api = value; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String value) { this.requestId = value; }

    public String geteTag() { return eTag; }
    public void seteTag(String value) { this.eTag = value; }

    public String getContentType() { return contentType; }
    public void setContentType(String value) { this.contentType = value; }

    public long getContentLength() { return contentLength; }
    public void setContentLength(long value) { this.contentLength = value; }

    public String getBlobType() { return blobType; }
    public void setBlobType(String value) { this.blobType = value; }

    public String getblobUrl() { return blobUrl; }
    public void setblobUrl(String value) { this.blobUrl = value; }

    public String getURL() { return url; }
    public void setURL(String value) { this.url = value; }

    public String getSequencer() { return sequencer; }
    public void setSequencer(String value) { this.sequencer = value; }

    public String getIdentity() { return identity; }
    public void setIdentity(String value) { this.identity = value; }

    public StorageDiagnostics getStorageDiagnostics() { return storageDiagnostics; }
    public void setStorageDiagnostics(StorageDiagnostics value) { this.storageDiagnostics = value; }
}

class StorageDiagnostics {
    private String batchId;

    public String getBatchId() { return batchId; }
    public void setBatchdD(String value) { this.batchId = value; }
}

@RestController
public class StatsController {

  static final String BINDING_NAME = "";
  static final String connectStr = "";
 
  @GetMapping(value = "/api/blobstats")
  public Stats getBlobStats() {

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

          byte[] decodedBytes = Base64.getDecoder().decode(message.getBody().toBytes());
          String decodedString = new String(decodedBytes);
          
          ObjectMapper mapper = new ObjectMapper();
          var event = mapper.readValue(decodedString, myEventGridEvent.class);
          
          if(event.getEventType().equals("Microsoft.Storage.BlobCreated"))
          {
            var data = event.getData();
            if(null != data )
            {
              filesize = Long.toString(data.getContentLength());
            }
          }           
          queueClient.deleteMessage(message.getMessageId(), message.getPopReceipt());
       }       
    }
    catch (QueueStorageException e)
    {
        // Output the exception message and stack trace
        System.out.println(e.getMessage());
        e.printStackTrace();
    } catch (JsonMappingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (JsonProcessingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    int value = 0;
    if(Integer.valueOf(filesize) > 100000 && Integer.valueOf(filesize) < 500000)
    {
      stats.tasks = 1;
    }  
    else if(Integer.valueOf(filesize) >= 500000)
    {
      stats.tasks = 2;
    }
          
    return stats; 
  }
}