# 🚀 uis-schedule-system-backend

Backend del sistema de agendamiento de citas para la Universidad Industrial de Santander (UIS), desarrollado con Spring Boot y Docker.

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-green.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-✓-blue.svg)](https://www.docker.com)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## 📋 Tabla de Contenidos
- [Estructura del Proyecto](#-estructura-del-proyecto)


## 📂 Estructura del Proyecto

```text
uis-schedule-system-backend/
├── src/
│ └── main/
│ ├── java/
│ │ └── com/uis/schedule/backend/
│ │ ├── config/ # Configuraciones Spring
│ │ ├── controllers/ # Controladores REST
│ │ ├── models/ # Entidades JPA
│ │ ├── repositories/ # Interfaces JpaRepository
│ │ ├── services/ # Lógica de negocio
│ │ ├── exceptions/ # Manejo de excepciones
│ │ └── utils/ # Utilidades comunes
│ └── resources/
│ ├── application.yml # Configuración principal
│ └── application-dev.yml # Configuración desarrollo
├── docker/
│ ├── Dockerfile # Configuración Docker
│ └── init.sql # Scripts iniciales DB
├── docker-compose.yml # Orquestación contenedores
├── mvnw # Maven Wrapper
├── pom.xml # Dependencias Maven
└── README.md # Documentación
``` 
## ✅ Requisitos previos

Antes de comenzar, asegúrate de tener instalado:

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)

Verifica que están correctamente instalados ejecutando:

```bash
docker -v
docker compose version

##📥 Clonar Repositorio

git clone https://github.com/usuario/nombre-del-proyecto.git
cd nombre-del-proyecto


##🚀Levantar Backend puerto 8080

docker compose up --build



