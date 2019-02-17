FROM openjdk:8 as build-container

ARG SBT_VERSION=1.1.6

RUN curl -L -o sbt-$SBT_VERSION.deb http://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
    dpkg -i sbt-$SBT_VERSION.deb && \
    rm sbt-$SBT_VERSION.deb && \
    apt-get update && \
    apt-get install sbt
  
COPY . /build

WORKDIR /build

RUN sbt assembly

FROM openjdk:8 as server-container

ARG SCALA_VERSION=2.12.6

RUN curl -L -o scala-$SCALA_VERSION.deb https://downloads.lightbend.com/scala/$SCALA_VERSION/scala-$SCALA_VERSION.deb && \
    dpkg -i scala-$SCALA_VERSION.deb && \
    rm scala-$SCALA_VERSION.deb && \
    apt-get update && \
    apt-get install scala

COPY --from=build-container /build/target/store-backend.jar /app/

WORKDIR /app

EXPOSE 8080

ENTRYPOINT [ "scala", "store-backend.jar" ]