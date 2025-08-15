# =============================================================
# Dockerfile multi-stage para construir y ejecutar la app
# -------------------------------------------------------------
# Etapa 1 (builder): compila el proyecto con Maven y genera el JAR
# Etapa 2 (runtime): imagen liviana que solo contiene el JAR
# Comentarios en español siguiendo el estilo del repo
# =============================================================

# -------------------------------
# Etapa 1: Builder (Maven + JDK)
# -------------------------------
FROM maven:3.9.8-eclipse-temurin-17 AS builder

# Directorio de trabajo dentro del contenedor
WORKDIR /workspace

# Copio primero los archivos de build para aprovechar caché de Docker
# (si el pom no cambia, no se re-descargan dependencias)
COPY pom.xml ./

# Descargo dependencias sin compilar (optimiza builds posteriores)
RUN mvn -q -DskipTests dependency:go-offline

# Copio el código fuente
COPY src ./src

# Compilo y empaqueto el JAR (sin correr tests para acelerar la build de imagen)
RUN mvn -q -DskipTests package

# -------------------------------
# Etapa 2: Runtime (JRE liviano)
# -------------------------------
FROM eclipse-temurin:17-jre

# Creo un usuario no root por buenas prácticas en contenedores
RUN useradd -ms /bin/bash appuser
USER appuser

# Directorio de trabajo para la app
WORKDIR /app

# Copio el JAR construido en la etapa anterior
# Nota: el nombre del JAR viene de pom.xml (artifactId + version)
# Si cambia la versión, actualizar la ruta o usar un patrón más general.
COPY --from=builder /workspace/target/customer_management_app-0.0.1-SNAPSHOT.jar /app/app.jar

# Exponer el puerto por defecto de Spring Boot
EXPOSE 8080

# Variables útiles (perfil y opciones JVM)
# - SPRING_PROFILES_ACTIVE: por defecto apuntaremos a "prod" al usar docker-compose
#   (lo podés sobreescribir en tiempo de ejecución con -e SPRING_PROFILES_ACTIVE=dev)
# - JAVA_OPTS: flags opcionales para la JVM (memoria, GC, etc.)
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS=""

# Comando de arranque (permite inyectar JAVA_OPTS)
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
