Write-Host "Building and pushing batch-processor image to ACR"

$acrLoginServer = "dapr1batch.azurecr.io"
$acrName = "dapr1batch"

# Log in to the registry
az acr login --name $acrName

# Build an image from a Dockerfile
docker build -t batch-processor:v1 $PSScriptRoot/../batchProcessor

# Tag the image
docker tag batch-processor:v1 $acrLoginServer/batch-processor:v1

# Push the image to the Azure Container Registry instance
docker push $acrLoginServer/batch-processor:v1