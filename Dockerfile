FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B
COPY src/ src/
RUN ./mvnw package -DskipTests -B

FROM eclipse-temurin:21-jre
WORKDIR /app

# Crear directorio para uploads temporales (si se usa almacenamiento local como fallback)
RUN mkdir -p /app/uploads && chmod 755 /app/uploads

COPY --from=build /app/target/*.jar app.jar

# Render usa la variable PORT
ENV PORT=8080
EXPOSE ${PORT}

# Configurar memoria para el plan free de Render (512MB RAM)
ENTRYPOINT ["java", "-Xmx400m", "-Xms200m", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
