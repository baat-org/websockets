export SERVICE=websockets
export VERSION=1.0

docker rmi $(docker images -qa -f 'dangling=true')

./gradlew clean build bootJar

docker build --no-cache -t sachingoyaldocker/baat-org-${SERVICE}:${VERSION} . 

docker push sachingoyaldocker/baat-org-${SERVICE}:${VERSION}
