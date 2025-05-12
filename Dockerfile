# Etapa 1: Construcción
FROM maven:3.9.6-eclipse-temurin-17 AS builder
LABEL authors="Ssnati"

# Crear directorio de la app
WORKDIR /app

# Copiar archivos del proyecto
COPY pom.xml .
COPY src ./src

# Compilar la aplicación y generar el JAR
RUN mvn clean package -DskipTests

# Etapa 2: Imagen de ejecución
FROM eclipse-temurin:17-jdk-alpine

# Variables de entorno opcionales (puedes sobrescribirlas en tiempo de ejecución)
ENV JAVA_OPTS=""

# Crear usuario no root por seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Directorio de la app
WORKDIR /app

# Copiar el JAR desde la etapa de construcción
COPY --from=builder /app/target/GELI-Backend-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto configurado en application.properties (puede ser 8080 por defecto)
EXPOSE 8080

# Comando para ejecutar el JAR con opciones adicionales si se pasan
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
