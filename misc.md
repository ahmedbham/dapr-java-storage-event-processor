docker login ahmedbham.azurecr.io

docker tag php-apache ahmedbham.azurecr.io/php-apache
docker push ahmedbham.azurecr.io/php-apache

docker build -t custom-metric .
docker tag custom-metric ahmedbham.azurecr.io/custom-metric
docker push ahmedbham.azurecr.io/custom-metric


cd custom-metric
k apply -f manifests/deployment.yaml
cd ..

cd batch-file-processing/php-apache
k apply -f php-apache.yaml