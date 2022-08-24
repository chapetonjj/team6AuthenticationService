FROM gradle:jdk10 as builder
COPY --chown=gradle:gradle . /app
WORKDIR /app
RUN gradle -b mcc-authentication-service/build.gradle clean build bootjar

FROM openjdk:8-jdk-alpine
EXPOSE 8081
VOLUME /tmp
ARG targethost=localhost:8080
ENV API_HOST=$targethost
ARG LIBS=app/mcc-authentication-service/build/libs
COPY --from=builder ${LIBS}/ /app/lib
ENTRYPOINT ["java","-jar","./app/lib/mcc-authentication-service-0.0.1-SNAPSHOT.jar"]