VOLUME /tmp
ARG JAR_FILE=target/*.jar
ENV JAVA_OPTS=""
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]
