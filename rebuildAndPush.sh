SERVICE=${PWD##*/}
VERSION=latest

./gradlew clean build
docker rmi $(docker images -qa 'sachingoyaldocker/baat-org-'$SERVICE)
docker build --no-cache -t sachingoyaldocker/baat-org-$SERVICE:$VERSION --build-arg SERVICE=$SERVICE .
docker push sachingoyaldocker/baat-org-$SERVICE:$VERSION
