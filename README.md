# UIS Schedule System - Backend

Backend del **Sistema de Gestión de Horarios Académicos** de la Universidad Industrial de Santander (UIS). Expone una API REST construida con Spring Boot que centraliza la administración de usuarios, roles, invitaciones, facultades, escuelas, materias, docentes, grupos, aulas y horarios de clase.

## Tabla de contenido

- [Tecnologías](#tecnologías)
- [Arquitectura](#arquitectura)
- [Modelo de datos](#modelo-de-datos)
- [Requisitos previos](#requisitos-previos)
- [Configuración](#configuración)
- [Ejecución en local](#ejecución-en-local)
- [Ejecución con Docker](#ejecución-con-docker)
- [Documentación de la API](#documentación-de-la-api)
- [Endpoints principales](#endpoints-principales)
- [Roles y seguridad](#roles-y-seguridad)
- [Migraciones de base de datos](#migraciones-de-base-de-datos)
- [Pruebas](#pruebas)
- [CI/CD](#cicd)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Licencia](#licencia)

## Tecnologías

- **Java 17** con **Spring Boot 3.3.12**
- **Spring Web** – API REST
- **Spring Security** + **JWT** (`jjwt`) – autenticación y autorización basada en tokens
- **Spring Data JPA** – persistencia
- **PostgreSQL 16** – base de datos relacional
- **Flyway** – control de versiones de la base de datos
- **Spring Mail** – envío de correos (invitaciones, recuperación de contraseña)
- **Springdoc OpenAPI** – documentación interactiva (Swagger UI)
- **Lombok** – reducción de código repetitivo
- **Docker / Docker Compose** – contenedorización y despliegue

## Arquitectura

El proyecto sigue una organización por capas dentro del paquete base `com.uis.schedule.backend`:

```
configuration/     Configuración de la app, seguridad y JWT
persistence/
  ├── entity/       Entidades JPA
  └── repository/   Repositorios Spring Data
presentation/
  ├── controller/   Controladores REST
  ├── dto/          Objetos de transferencia de datos
  └── handler/      Manejo global de excepciones
service/
  ├── interfaces/   Contratos de servicio
  ├── implementation/ Lógica de negocio
  └── exception/    Excepciones de dominio
util/               Utilidades y mappers
```

## Modelo de datos

Las principales entidades del dominio son:

- **Usuarios y seguridad**: `users`, `roles`, `user_roles`, `password_reset_tokens`, `user_invitations`
- **Estructura académica**: `faculty`, `school`, `subject`, `academic_period`
- **Recursos**: `teacher`, `classroom`, `day_of_week`, `class_hour`
- **Programación de clases**: `groups`, `class`, `schedule`
- **Auditoría**: `audit_log`

## Requisitos previos

- Java 17
- Maven (o usar el wrapper `./mvnw` incluido)
- PostgreSQL 16 (si se ejecuta en local sin Docker)
- Docker y Docker Compose (opcional, recomendado)
- Una cuenta de correo con contraseña de aplicación para el envío de emails (SMTP Gmail)

## Configuración

1. Copia el archivo de ejemplo de variables de entorno:

   ```bash
   cp .env.example .env
   ```

2. Completa los valores en `.env`:

   | Variable | Descripción |
   |---|---|
   | `SPRING_PROFILE` | Perfil activo (`dev` para local, `prod` para Docker) |
   | `DB_HOST` | Host de PostgreSQL |
   | `DB_PORT` | Puerto de PostgreSQL |
   | `DB_NAME` | Nombre de la base de datos |
   | `DB_USERNAME` | Usuario de la base de datos |
   | `DB_PASSWORD` | Contraseña de la base de datos |
   | `SECURITY_USER_NAME` | Usuario básico de Spring Security |
   | `SECURITY_USER_PASSWORD` | Contraseña del usuario básico |
   | `JWT_PRIVATE_KEY` | Clave privada para firmar los JWT |
   | `JWT_USER_GENERATOR` | Identificador del emisor del token |
   | `CORS_ALLOWED_ORIGINS` | Orígenes permitidos (separados por coma) |
   | `SERVER_PORT` | Puerto del servidor (por defecto `8080`) |
   | `MAIL_USERNAME` / `MAIL_PASSWORD` | Credenciales SMTP para envío de correos |
   | `INVITATION_BASE_URL` | URL base del frontend para el enlace de invitación |
   | `PASSWORD_RESET_BASE_URL` | URL base del frontend para restablecer contraseña |

## Ejecución en local

```bash
# Levantar solo la base de datos con Docker (opcional)
docker-compose up -d postgres

# Ejecutar la aplicación
./mvnw spring-boot:run
```

La API quedará disponible en `http://localhost:8080` (o el puerto definido en `SERVER_PORT`).

## Ejecución con Docker

```bash
docker-compose up -d --build
```

Esto levanta dos servicios:

- **postgres**: base de datos PostgreSQL 16
- **spring-api-image**: la aplicación Spring Boot, construida a partir del `Dockerfile`

Las migraciones de Flyway se ejecutan automáticamente al iniciar la aplicación.

## Documentación de la API

Con la aplicación en ejecución, la documentación interactiva está disponible en:

- Swagger UI: `http://localhost:8080/swagger-ui`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Endpoints principales

### Autenticación (`/auth`)

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/auth/signup` | Registro de usuario |
| `POST` | `/auth/login` | Inicio de sesión |
| `POST` | `/auth/password/forgot` | Solicitar recuperación de contraseña |
| `POST` | `/auth/password/reset/{token}` | Restablecer contraseña con token |
| `PUT` | `/auth/password/change` | Cambiar contraseña (usuario autenticado) |

### Usuarios (`/api/users`)

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/users` | Listar usuarios |
| `GET` | `/api/users/status/{status}` | Filtrar por estado |
| `GET` | `/api/users/role/{roleName}` | Filtrar por rol |
| `GET` | `/api/users/{id}` | Obtener usuario por ID |
| `POST` | `/api/users` | Crear usuario |
| `PUT` | `/api/users/{id}` | Actualizar usuario |
| `DELETE` | `/api/users/{id}` | Eliminar usuario |

### Roles (`/api/roles`)

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/roles` | Listar roles |
| `GET` | `/api/roles/all` | Listar todos los roles |
| `GET` | `/api/roles/status/{status}` | Filtrar por estado |
| `GET` | `/api/roles/{id}` | Obtener rol por ID |
| `POST` | `/api/roles` | Crear rol |
| `PUT` | `/api/roles/{id}` | Actualizar rol |
| `DELETE` | `/api/roles/{id}` | Eliminar rol |

### Invitaciones (`/api/v1/invitations`)

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/v1/invitations` | Crear invitación |
| `GET` | `/api/v1/invitations/validate/{token}` | Validar token de invitación |
| `POST` | `/api/v1/invitations/complete/{token}` | Completar registro por invitación |
| `GET` | `/api/v1/invitations/pending` | Listar invitaciones pendientes de aprobación |
| `PUT` | `/api/v1/invitations/{invitationId}/approve` | Aprobar invitación |
| `PUT` | `/api/v1/invitations/{invitationId}/reject` | Rechazar invitación |

## Roles y seguridad

La autenticación se realiza mediante **JWT**. Los roles definidos en el sistema son:

- `ADMINISTRADOR`
- `DOCENTE`
- `ESTUDIANTE`

La mayoría de los endpoints de gestión de usuarios, roles e invitaciones están restringidos al rol `ADMINISTRADOR` mediante `@PreAuthorize`, mientras que los endpoints de autenticación son de acceso público.

## Migraciones de base de datos

Las migraciones se gestionan con Flyway y se ubican en `src/main/resources/db/migration`:

| Script | Descripción |
|---|---|
| `V1__init.sql` | Creación del esquema inicial (usuarios, roles, facultades, escuelas, materias, docentes, grupos, clases, horarios, auditoría) |
| `V2__cusol_data_dml.sql` | Datos iniciales (seed) de roles y catálogos |
| `V3__invitation_system.sql` | Sistema de invitaciones de usuario |
| `V4__password_reset_tokens.sql` | Tokens de recuperación de contraseña |

## Pruebas

```bash
./mvnw test
```

## CI/CD

El proyecto usa GitHub Actions:

- **`ci.yml`**: ejecuta las pruebas con Maven en cada `push` o `pull request` hacia `develop` y `main`.
- **`deploy.yml`**: en cada `push` a `develop`, compila el proyecto y reconstruye/reinicia el contenedor Docker en el runner autoalojado de despliegue.

## Estructura del proyecto

```
uis-schedule-system-backend/
├── .github/workflows/       # Pipelines de CI/CD
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/uis/schedule/backend/
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       ├── application-prod.properties
│   │   │       └── db/migration/
│   │   └── test/
│   ├── docker-compose.yml
│   ├── Dockerfile
│   ├── .env.example
│   └── pom.xml
├── LICENSE
└── README.md
```

## Licencia

Este proyecto está licenciado bajo los términos de la **GNU General Public License v3.0**. Consulta el archivo [LICENSE](./LICENSE) para más detalles.
