FROM adoptopenjdk/openjdk8:ubi

RUN mkdir /opt/app
WORKDIR /opt/app

EXPOSE 80
EXPOSE 25565

COPY docker .
CMD ["sh", "start.sh"]