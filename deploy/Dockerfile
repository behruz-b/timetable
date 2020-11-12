FROM openjdk:11

RUN apt-get update -y
RUN apt-get install zip -y
RUN echo "deb http://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823
RUN apt-get update -y
RUN apt-get install sbt -y

#VOLUME "zipFile" ["../target/universal/food-order-free-0.1.zip"]

#EXPOSE 8080

COPY run.sh /run.sh

ENTRYPOINT ["bash","/run.sh"]
