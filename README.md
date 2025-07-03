# 🚀 uis-schedule-system-backend

Backend del sistema de agendamiento de citas para la Universidad Industrial de Santander (UIS), desarrollado con Spring Boot y Docker.

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.12-green.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-✓-blue.svg)](https://www.docker.com)


## 📋 Tabla de Contenidos
- [Estructura del Proyecto](#-estructura-del-proyecto)


## 📂 Estructura del Proyecto

```text

uis-schedule-system-backend/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/uis/schedule/backend/
│       │       ├── configuration/         # Configuración general
│       │       ├── persistence/           # Entidades y repositorios
│       │       ├── presentation/          # Controladores REST
│       │       ├── service/               # Lógica de negocio
│       │       ├── util/                  # Utilidades
│       │       └── UisScheduleSystemBackendApplication.java
│       └── resources/
│           └── application.properties
├── docker-compose.yml
├── Dockerfile
└── README.md
``` 
## ✅ Requisitos previos

Antes de comenzar, asegúrate de tener instalado:

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)

Verifica que están correctamente instalados ejecutando:

```bash
docker -v
docker compose version
```
## 📥 Clonar Repositorio
```
git clone https://github.com/usuario/nombre-del-proyecto.git
cd nombre-del-proyecto
```

## 🚀Levantar Backend puerto 8080
```
docker compose up --build
```


