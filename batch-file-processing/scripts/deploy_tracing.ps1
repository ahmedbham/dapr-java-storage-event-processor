# Add App Insights extension to Azure CLI
az extension add -n application-insights

# Create an App Insights resource
az monitor app-insights component create `
    --app "app-insight-dapr-batch" `
    --location "westus2" `
    --resource-group "dapreks-rg"

# Copy the value of the instrumentationKey, we will need it later