FROM java:8
WORKDIR /service
COPY target/codeway-server-eureka-1.0.0.jar server-eureka.jar
RUN bash -c "touch /server-eureka.jar"
EXPOSE 5000
ENV JAVA_OPTS "-server -Xmx128m -Xms128m -Xmn36m  -DACTIVE_PROFILE=prod"
ENTRYPOINT exec java $JAVA_OPTS -jar /service/server-eureka.jar
