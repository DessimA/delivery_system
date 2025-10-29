FROM openjdk:17-alpine

WORKDIR /app

# Copia os arquivos de build do Maven
COPY mvnw ./
COPY .mvn ./.mvn
COPY pom.xml .

# Garante que o script seja executável e corrige quebras de linha
RUN chmod +x ./mvnw && apk add --no-cache dos2unix && dos2unix ./mvnw

# Instala dependências (para cachear a camada)
RUN ./mvnw dependency:go-offline -B

# Copia o código-fonte
COPY src ./src

# Empacota a aplicação (cria o JAR)
# O -B (batch mode) evita logs de download extensos
RUN ./mvnw package -B -DskipTests

# Explode o JAR para que o devtools possa monitorar as classes
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

EXPOSE 8080

# Inicia a aplicação com o JarLauncher para permitir o hot-reload
CMD ["java", "-cp", "target/dependency/BOOT-INF/classes:target/dependency/BOOT-INF/lib/*", "com.delivery.DeliveryApplication"]