FROM gradle:5.4-jdk11 as builder
COPY --chown=gradle:gradle . /home/gradle
WORKDIR /home/gradle
RUN gradle clean build

FROM openjdk:11-jdk-stretch
RUN mkdir -p /app/logs/
#RUN apt update && apt install -y tomcat-native
ENV JAVA_OPTS="-Xms256m -Xmx256m"
EXPOSE 8080 8081
COPY --from=builder /home/gradle/build/libs/*.jar /app/app.jar
RUN chmod +x /app/app.jar
CMD exec java $JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError -jar /app/app.jar
