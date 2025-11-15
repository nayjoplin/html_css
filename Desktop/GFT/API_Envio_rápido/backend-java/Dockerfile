FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/target/envio-rapido-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 3000
RUN apt-get update && apt-get install -y curl \
	&& rm -rf /var/lib/apt/lists/*

ENTRYPOINT ["java","-jar","/app/app.jar"]
