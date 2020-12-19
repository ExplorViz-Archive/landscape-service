docker tag explorviz/landscape-service-jvm:latest eu.gcr.io/imposing-kite-298510/explorviz/landscape-service-jvm:lotzk-seminar
docker push  eu.gcr.io/imposing-kite-298510/explorviz/landscape-service-jvm:lotzk-seminar
kubectl apply -f manifest.yml