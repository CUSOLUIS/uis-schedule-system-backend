# 📅 uis-schedule-system-backend

Backend del sistema de agendamiento de la UIS, desarrollado con Spring Boot y ejecutado mediante Docker.

---
##📂 Estructura
```
uis-schedule-system-backend/
├── src/
│ └── main/
│ ├── java/
│ │ └─ com/uis/schedule/backend/
│ │  ├── configuration/ # Configuración del proyecto
│ │  ├── persistence/ # Acceso a datos (entidades, repositorios)
│ │  ├── presentation/ # Controladores REST
│ │  ├── service/ # Lógica de negocio
│ │  ├── util/ # Funciones auxiliares
│ │  └── UisScheduleSystemBackendApplication.java
│ └── resources/
│  └── application.properties
├── docker-compose.yml
├── Dockerfile
└── README.md


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



