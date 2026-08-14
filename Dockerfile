## base
FROM amazoncorretto:25.0.1-al2023-headless AS base

## builder
FROM base AS builder
WORKDIR /builder

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

RUN java -Djarmode=tools -jar app.jar extract --layers --destination extracted

## runner
FROM base AS runner
WORKDIR /app

ENV TZ=Asia/Seoul
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=40.0 -XX:MaxMetaspaceSize=384m -XX:MaxDirectMemorySize=128m"

RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime

COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./

EXPOSE 8080
HEALTHCHECK --interval=10s --timeout=10s --retries=5 CMD curl --fail http://localhost:8080/api/actuator/health || exit 1

CMD ["java", "-jar", "app.jar"]
