# --- Etapa 1: Construcción (Build) ---
# Usamos Java 21 (jdk) solo para compilar el código
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

# --- Etapa 2: Ejecución (Run) ---
# Empezamos con una imagen limpia de Java 21 (jre)
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# ---- ¡AQUÍ ESTÁ EL ARREGLO! ----
# 1. Instalamos ffmpeg y curl
# 2. Descargamos el binario DE LINUX (yt-dlp_linux) y lo guardamos como "yt-dlp"
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    ffmpeg \
    curl && \
    curl -L https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp_linux -o /usr/local/bin/yt-dlp && \
    chmod a+rx /usr/local/bin/yt-dlp && \
    rm -rf /var/lib/apt/lists/*
# -------------------------------

# Copiamos solo el JAR construido de la etapa anterior
COPY --from=builder /app/build/libs/*-SNAPSHOT.jar /app/app.jar

# Exponemos el puerto
EXPOSE 8080

# Comando para arrancar el bot
CMD java -jar -Dserver.port=$PORT /app/app.jar
