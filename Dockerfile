FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8081

ENV JAVA_OPTS="-Xms512m -Xmx1024m -Duser.timezone=GMT+08"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
