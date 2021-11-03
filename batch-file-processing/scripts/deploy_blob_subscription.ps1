# batch-receiver microservice should already be running for the subscription validation handshake
az eventgrid event-subscription create `
    --source-resource-id "/subscriptions/0005c093-d9ad-44b4-abe8-f507848419ca/resourceGroups/dapr-eks-rg/providers/Microsoft.Storage/storageaccounts/dapr1batch" `
    --name blob-created `
    --endpoint-type webhook `
    --endpoint https://nginx-daprbatch.westus2.cloudapp.azure.com/api/blobAddedHandler `
    --included-event-types Microsoft.Storage.BlobCreated