# --- Etapa 1: Construcción (Build) ---
# Usamos Java 21 (jdk) solo para compilar el código
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

# --- Etapa 2: Ejecución (Run) --
# Empezamos con una imagen limpia de Java 21 (jre)
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# ---- ¡AQUÍ ESTÁ EL ARREGLO! ----
# Instalamos las dependencias (ffmpeg/yt-dlp) en la imagen FINAL
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    ffmpeg \
    yt-dlp && \
    rm -rf /var/lib/apt/lists/*
# -------------------------------

# Copiamos solo el JAR construido de la etapa anterior
COPY --from=builder /app/build/libs/*-SNAPSHOT.jar /app/app.jar

# Exponemos el puerto
EXPOSE 8080

# Comando para arrancar el bot
CMD java -jar -Dserver.port=$PORT /app/app.jar
