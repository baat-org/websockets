docker rmi $(docker images -qa 'sachingoyaldocker/baat-org-websockets')

./gradlew clean build bootJar
docker build --no-cache -t sachingoyaldocker/baat-org-websockets:1.0 . 
docker push sachingoyaldocker/baat-org-websockets:1.0
