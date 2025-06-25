
#IMAGEN MODELO
FROM eclipse-temurin:21.0.7_6-jdk


#INFORMAR EL PUERTO DONDE SE EJECUTA EL CONTENEDOR
EXPOSE 8080


#DEFINIR DIRECTORIO RAIZ DE NUESTRO CONTENEDOR
WORKDIR /root

#COPIAR Y PEGAR ARCHIVOS DENTRO DEL CONTENEDOR

COPY ./pom.xml /root
#Version liviana de maven, contiene configuración del wrapper
COPY ./.mvn /root/.mvn
#ejecutable del wrapper
COPY ./mvnw /root
#DESCARGAR DEPENDENCIAS

RUN ./mvnw dependency:go-offline

#COPIAR CODIGO FUENTE EN EL CONTENEDOR
COPY ./src /root/src

#CONSTRUIR APLICACIÓN sin test

RUN ./mvnw clean install -DskipTests

#LEVANTAR APLICACIÓN CUANDO EL CONTENEDOR INICIE

ENTRYPOINT ["java","-jar","/root/target/backend-0.0.1-SNAPSHOT.jar"]
