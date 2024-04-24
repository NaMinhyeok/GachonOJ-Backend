FROM openjdk:17

# Install Python, Node.js and gcc
#RUN apt-get update && \
#    apt-get install -y python3 python3-pip nodejs npm gcc g++ \
#    && apt-get clean \
#    && rm -rf /var/lib/apt/lists/* \

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]