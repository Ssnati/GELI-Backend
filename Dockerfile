# Etapa 1: Construcción
FROM maven:3.9.6-eclipse-temurin-17 AS builder
LABEL authors="Ssnati"

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen de ejecución
FROM eclipse-temurin:17-jdk-alpine

# Variables de entorno
ENV JAVA_OPTS=""
ENV WALLET_B64=""
ENV WALLET_PATH="/app/wallet"

# Instalar unzip, base64, mkdir
RUN apk add --no-cache unzip coreutils

# Crear directorio de trabajo
WORKDIR /app

# Copiar el script y dar permisos
COPY unzip-wallet.sh ./unzip-wallet.sh
RUN chmod +x unzip-wallet.sh

# Crear usuario no root
RUN addgroup -S spring && adduser -S spring -G spring

# Crear subdirectorio wallet y asignar permisos antes de cambiar de usuario
RUN mkdir -p /app/wallet && chown -R spring:spring /app

# Cambiar a usuario no root
USER spring:spring


# Copiar el JAR
COPY --from=builder /app/target/GELI-Backend-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto configurado en application.properties
EXPOSE 8080

# Ejecutar script de wallet + app
ENTRYPOINT ["sh", "-c", "./unzip-wallet.sh && java $JAVA_OPTS -jar app.jar"]
