# --- Etapa 1: Construcción (Build) ---
# Usamos una imagen de Java 21 (jdk) para compilar
FROM eclipse-temurin:21-jdk-jammy AS builder

# Instalamos las dependencias que necesitamos (yt-dlp y ffmpeg)
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    ffmpeg \
    yt-dlp && \
    rm -rf /var/lib/apt/lists/*

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos todo el código fuente al contenedor
COPY . .

# Damos permisos de ejecución a gradlew
RUN chmod +x ./gradlew

# Construimos el proyecto, saltando los tests (como ya hacíamos)
RUN ./gradlew build -x test

# --- Etapa 2: Ejecución (Run) ---
# Usamos una imagen de Java 21 (jre) más liviana para correr la app
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copiamos solo el JAR construido de la etapa anterior
COPY --from=builder /app/build/libs/*-SNAPSHOT.jar /app/app.jar

# Exponemos el puerto (Railway lo maneja, pero es buena práctica)
EXPOSE 8080

# Comando para arrancar el bot (usando la variable $PORT de Railway)
CMD java -jar -Dserver.port=$PORT /app/app.jar
