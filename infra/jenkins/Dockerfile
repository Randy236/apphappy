FROM jenkins/jenkins:lts-jdk17

USER root

# Cliente Docker estático (docker.io en Debian a veces no deja el CLI usable en PATH)
ARG DOCKER_VERSION=27.3.1
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    ca-certificates \
    git \
    && rm -rf /var/lib/apt/lists/* \
    && curl -fsSL "https://download.docker.com/linux/static/stable/x86_64/docker-${DOCKER_VERSION}.tgz" \
    | tar -xzC /usr/local/bin --strip-components=1 docker/docker \
    && chmod +x /usr/local/bin/docker \
    && docker --version

# Acceso al socket (en Docker Desktop suele ser root:root)
RUN usermod -aG root jenkins

COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt

USER jenkins
