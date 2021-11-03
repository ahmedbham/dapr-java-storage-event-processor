Write-Host "Building and pushing batch-receiver image to ACR"

$acrLoginServer = "dapr1batch.azurecr.io"
$acrName = "dapr1batch"

# Log in to the registry
az acr login --name $acrName

# Build an image from a Dockerfile
docker build -t batch-receiver:v1 $PSScriptRoot/../batchReceiver

# Tag the image
docker tag batch-receiver:v1 $acrLoginServer/batch-receiver:v1

# Push the image to the Azure Container Registry instance
docker push $acrLoginServer/batch-receiver:v1