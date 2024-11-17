FROM gradle:8.10.1-jdk21 AS build
COPY  . /home/gradle/src
WORKDIR /home/gradle/src

ARG GRAD_USER
ARG GRAD_PASS
ENV GRAD_USER=$GRAD_USER
ENV GRAD_PASS=$GRAD_PASS

RUN gradle assemble

RUN unset GRAD_USER GRAD_PASS

FROM amazoncorretto:21.0.4
EXPOSE 8082
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/spring-boot-application.jar
COPY newrelic-agent/newrelic.jar /app/newrelic.jar
COPY ./newrelic-agent/newrelic.yml /app/newrelic.yml
WORKDIR /app
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=production", "-javaagent:/app/newrelic.jar", "/app/spring-boot-application.jar"]
