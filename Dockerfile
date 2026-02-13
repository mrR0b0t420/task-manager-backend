# Etapa 1: Compilación (Usamos Maven con Eclipse Temurin)
FROM maven:3.9.6-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Usamos solo el JRE, que es más ligero que el JDK completo)
FROM eclipse-temurin:17-jre-jammy
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]