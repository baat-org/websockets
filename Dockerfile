FROM sachingoyaldocker/baat-org-custom-jre15:latest
MAINTAINER Sachin Goyal <sachin.goyal.se@gmail.com>
VOLUME /opt
ARG SERVICE=service

ADD build/libs/$SERVICE.jar /opt/service/service.jar

ENTRYPOINT ["java","-jar","/opt/service/service.jar"]
