FROM eclipse-temurin:17-jre-focal

# Set working directory
WORKDIR /app

# Install dependencies (curl, unzip for Updater)
RUN apt-get update && apt-get install -y curl unzip && rm -rf /var/lib/apt/lists/*

# Create user for Pterodactyl
RUN useradd -d /home/container -m container
USER container
ENV USER=container HOME=/home/container
WORKDIR /home/container

# Copy entrypoint script
COPY ./entrypoint.sh /entrypoint.sh

# Default command
CMD ["/bin/bash", "/entrypoint.sh"]
