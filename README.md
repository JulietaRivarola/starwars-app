# Star Wars API

API REST que consume SWAPI y expone endpoints con autenticación JWT.

Stack: Java 21, Spring Boot 3, PostgreSQL, JWT

## Cómo probar la API desplegada (Render)

La aplicación está disponible online:

**API base:**  
https://starwars-app-eoda.onrender.com

**Swagger:**  
https://starwars-app-eoda.onrender.com/swagger-ui/index.html

Para probar los endpoints: click en "Authorize", pegar el token (sin Bearer).

### Registro/Login

```bash
# Registrar
POST /auth/register
{
  "email": "user@test.com",
  "password": "password123"
}

# Login
POST /auth/login
{
  "email": "user@test.com",
  "password": "password123"
}
```

Ambos devuelven `{"token": "..."}`. Usar el token en los demás endpoints.

### Endpoints

Todos requieren `Authorization: Bearer {token}`

```bash
# People
GET /api/people?page=1&limit=10&name=Luke
GET /api/people/{id}

# Films
GET /api/films?title=Empire
GET /api/films/{id}

# Starships
GET /api/starships?page=1&limit=10&name=wing&model=T-65&expanded=true
GET /api/starships/{id}

# Vehicles
GET /api/vehicles?page=1&limit=10&name=Sand
GET /api/vehicles/{id}
```

Films no tiene paginación (solo hay 6).

### Admin

Usuario admin pre creado: `admin@admin.com` / `admin123`

```bash
GET /api/admin/users  # Solo admin puede acceder
```

## Setup

```bash
# Levantar PostgreSQL con Docker
docker-compose up -d

# O crear la DB manualmente
createdb starwars_db
```

Correr con perfil local:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

El `application-local.yml` está configurado para usar PostgreSQL en localhost:5432.

## Notas

- Usuarios registrados tienen rol USER (acceso a todo Star Wars)
- Admin tiene acceso adicional a `/api/admin/users`
- Filtros por name/model implementan paginación manual en backend (SWAPI no soporta page/limit con filtros)
- `expanded=true` en starships devuelve info completa con model y propiedades (parámetro de SWAPI)