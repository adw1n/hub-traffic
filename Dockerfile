FROM openjdk:8

# get a newer yarn version than the default one
RUN apt-get update
RUN apt-get install -y apt-transport-https
RUN curl -sS https://dl.yarnpkg.com/debian/pubkey.gpg | apt-key add -
RUN echo "deb https://dl.yarnpkg.com/debian/ stable main" | tee /etc/apt/sources.list.d/yarn.list
RUN apt-get update && apt-get install -y yarn

RUN apt-get install -y nodejs

RUN mkdir /root/hub-traffic
ADD . /root/hub-traffic
WORKDIR /root/hub-traffic
RUN sed -i 's/jdbc:postgresql:\/\/localhost/jdbc:postgresql:\/\/db/g' src/main/resources/application.properties
RUN sed -i 's/debug: true/debug: false/g' webpack.config.js
RUN yarn install
RUN yarn deploy
RUN cd src/main/resources/static/js/ \
    && yarn add startbootstrap-freelancer@4.0.0-alpha \
    && mkdir -p node_modules/ \
    && mv /root/hub-traffic/node_modules/startbootstrap-freelancer node_modules/
RUN ./mvnw package -DskipTests

ENTRYPOINT ["java", "-jar", "/root/hub-traffic/target/hub-traffic-0.0.1-SNAPSHOT.jar"]