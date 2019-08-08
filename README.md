# websockets
WebSockets Service

```
export SERVICE=websockets
export VERSION=1.0

./gradlew clean build bootJar

docker build -t sachingoyaldocker/baat-org-${SERVICE}:${VERSION} . 

docker push sachingoyaldocker/baat-org-${SERVICE}:${VERSION} 
```
