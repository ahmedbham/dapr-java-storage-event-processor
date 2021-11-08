## Prerequisites

* [Docker](https://docs.docker.com/engine/install/)
* kubectl
* Azure CLI
* Helm3
* Java JDK11
* Maven


## Set up Cluster

In this sample we'll be using Azure Kubernetes Service, but you can install Dapr on any Kubernetes cluster.
Run [this script](deploy/deploy_aks.sh) to deploy an AKS cluster 

References:

* [Deploy AKS using Portal](https://docs.microsoft.com/en-us/azure/aks/kubernetes-walkthrough-portal)
* [Deploy AKS using CLI](https://docs.dapr.io/operations/hosting/kubernetes/cluster/setup-aks/)
* [Dapr Environment - Setup Cluster](https://docs.dapr.io/getting-started/install-dapr/#setup-cluster)

## Install Dapr

Run [this script](scripts/deploy_dapr_aks.sh) to install Dapr on the Kubernetes cluster or follow the steps below.

```bash
helm repo add dapr https://dapr.github.io/helm-charts/
helm repo update
kubectl create namespace dapr-system
helm install dapr dapr/dapr --namespace dapr-system
```

References:

* [Dapr Environment Setup](https://docs.dapr.io/getting-started/install-dapr/)
* [Install Dapr on a Kubernetes cluster using Helm](https://docs.dapr.io/getting-started/install-dapr/#install-with-helm-advanced)

## Create Blob Storage

1. Run [this script](deploy/deploy_storage.sh).

2. create the Kubernetes secret (replace *** with storage account key):
   ```bash
     kubectl create secret generic output-queue-secret --from-literal=connectionString=*********
    ```
3. Replace <storage_account_name> in [deploy/blob-storage.yaml](deploy/blob-storage.yaml) with your storage account name.

4. Create two Storage Account Queues, named "dapr-batch-queue" and "dapr-output-queue" in the same Storage Account.

5. Using Azure Portal, create an "Event" for the Storage Account, with the following settings:
        a. Name: <Anything>
        b. System Topic Name: <Anything>
        c. Event Types: "Blob Created"
        d. Endpoint Type: "Storage Queues"
        e. Endpoint: <select 'dapr-batch-queue' you had previously created>


References:

* [Use Azure Event Grid to route Blob storage events to web endpoint (Azure portal) - Portal](https://docs.microsoft.com/en-us/azure/event-grid/blob-event-quickstart-portal)
* [Manage Azure Storage resources - CLI](https://docs.microsoft.com/en-us/cli/azure/storage?view=azure-cli-latest)

## Build and push images to AKS

1. Create an Azure Container Registry (ACR) (Lowercase registry name is recommended to avoid warnings):

    ```powershell
    az acr create --resource-group <resource-group-name> --name <acr-name> --sku Basic
    ```

    Take note of loginServer in the output.

2. Integrate an existing ACR with existing AKS clusters:

    ```powershell
    az aks update -n <cluster-name> -g <resource-group-name> --attach-acr <acr-name>
    ```

3. Change ACR loginServer and name in batch-file-processing/javabatchreceiver/batch-receiver/java-batch-receiver.sh

References:
[Create a private container registry using the Azure CLI](https://docs.microsoft.com/en-us/azure/container-registry/container-registry-get-started-azure-cli)

## Install Kafka on AKS

helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update
kubectl create ns kafka
helm install dapr-kafka bitnami/kafka --wait --namespace kafka -f batch-file-processing/deploy/kafka-non-persistence.yaml

## Deploy microservices

1. Deploy Dapr components:

    ```bash
    kubectl apply -f batch-file-processing/deploy/blob-storage.yaml   
    kubectl apply -f batch-file-processing/deploy/queue-storage.yaml 
    kubectl apply -f batch-file-processing/deploy/output-queue.yaml
    kubectl apply -f batch-file-processing/deploy/messagebus.yaml
    kubectl apply -f batch-file-processing/deploy/subscriptions.yaml
    kubectl apply -f batch-file-processing/deploy/kafka-pubsub.yaml
    ```

2. Deploy Java Batch Receiver microservice:

    ```bash
    cd batch-file-processing/javabatchreceiver/batch-receiver
    ./java-batch-receiver.sh
    cd ../../../
    ```

    Check the logs for java-batch-receiver.
    ```bash 
    javabatchreceivername=$(kubectl get po --selector=app=java-batch-receiver -o jsonpath='{.items[*].metadata.name}')
    kubectl logs $javabatchreceivername -c java-batch-receiver -f
    ```
3. Test the application.

    a. Upload a file to 'order' container storage.
    b. view the log output in java-batch-receiver
    c. view entry in 'dapr-output-queue'
