# Klari API — Backend

API REST para **Klari**, una aplicación mobile de skincare que permite la creación y gestión de rutinas personalizadas según el tipo de piel y las metas del usuario.

Este backend es consumido por la app mobile desarrollada en **React Native (Expo)**.

👉 **Repositorio del frontend:**  
https://github.com/isialval/klari-app

---

## ✨ Qué hace

- Registro e inicio de sesión de usuarios con **JWT**
- Definición del tipo de piel del usuario
- Gestión de metas de cuidado de la piel (poros, manchas, acné, etc.)
- Catálogo de productos de skincare
- Recomendaciones de productos generadas por el sistema según:
  - Tipo de piel
  - Metas del usuario
  - Categoría del producto
  - Momento de aplicación (día / noche)
- Gestión de favoritos y productos del usuario (mis productos)
- Creación y edición de rutinas personalizadas

---

## 🧴 Lógica de personalización

- El usuario define su **tipo de piel**
- El usuario selecciona sus **metas de cuidado**
- El sistema recomienda productos compatibles con ese perfil
- Los productos pueden:
  - Ser sugeridos automáticamente
  - Guardarse como favoritos
  - Agregarse a "mis productos"
- Las rutinas se construyen combinando recomendaciones del sistema y elecciones del usuario

## 🛠️ Stack

- Java + Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security + JWT
- PostgreSQL
- Lombok

---

## 🔗 Frontend

La API es consumida por la aplicación mobile:

👉 https://github.com/isialval/klari

---

## 👤 Mi rol

- Desarrollo completo del backend
- Diseño de la API REST
- Implementación de autenticación y seguridad
- Modelado de datos y lógica de negocio

---

## ⚙️ Configuración

Este proyecto utiliza **PostgreSQL** y variables de entorno para la configuración sensible.

### Variables de entorno requeridas

```env
DB_URL=jdbc:postgresql://localhost:5432/klari
DB_USERNAME=postgres
DB_PASSWORD=tu_password

JWT_SECRET=tu_secret
JWT_EXPIRATION=86400000
```

Ejemplo de configuración en `application.example.properties`:

```properties
spring.application.name=klari-api

spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION}
```

> Nota: los archivos reales de configuración no se incluyen en el repositorio por seguridad.

---

## 🚀 Cómo correr el proyecto

1. Crear la base de datos en PostgreSQL:

```
klari
```

2. Instalar dependencias y levantar el servidor:

```
mvn spring-boot:run
```

La API quedará disponible en:

```
http://localhost:8080/api
```

---

## 📌 Endpoints principales

### Autenticación (`/api/auth`)

- `POST /api/auth/register` — registro de usuario
- `POST /api/auth/login` — inicio de sesión (JWT)

---

### Usuarios (`/api/users`)

- `GET /api/users/{id}` — obtener usuario
- `GET /api/users/{id}/skin-type` — obtener tipo de piel
- `PATCH /api/users/{id}/skin-type?skinType=...` — definir tipo de piel
- `GET /api/users/{id}/goals` — obtener metas del usuario
- `POST /api/users/{id}/goals/{goal}` — agregar meta
- `DELETE /api/users/{id}/goals/{goal}` — eliminar meta

#### Favoritos

- `GET /api/users/{id}/favorites`
- `POST /api/users/{userId}/favorites/{productId}`
- `DELETE /api/users/{userId}/favorites/{productId}`

#### Mis productos (inventario)

- `GET /api/users/{id}/inventory`
- `POST /api/users/{userId}/inventory/{productId}`
- `DELETE /api/users/{userId}/inventory/{productId}`

#### Rutinas del usuario

- `GET /api/users/{id}/routines`

---

### Productos (`/api/products`)

- `GET /api/products` — listar productos
- `GET /api/products/{id}` — obtener producto por id
- `POST /api/products` — crear producto
- `PUT /api/products/{id}` — actualizar producto
- `DELETE /api/products/{id}` — eliminar producto
- `POST /api/products/bulk` — carga masiva de productos

#### Búsqueda y filtrado

- `GET /api/products/category/{category}`
- `GET /api/products/brand/{brand}`
- `GET /api/products/search?q=...&category=...`

#### Recomendaciones del sistema

- `GET /api/products/routine/recommend`
  - Parámetros:
    - `category`
    - `time`
    - `skinType`
    - `goals`

---

### Rutinas (`/api/routines`)

- `POST /api/routines` — crear rutina
- `GET /api/routines/{id}` — obtener rutina
- `DELETE /api/routines/{id}` — eliminar rutina
- `GET /api/routines/user/{userId}` — listar rutinas del usuario

#### Obtener rutina de día y noche

- `GET /api/routines/user/{userId}/day/active` — obtener rutina activa de día
- `GET /api/routines/user/{userId}/night/active` — obtener rutina activa de noche

#### Rutinas inactivas (historial)

- `GET /api/routines/user/{userId}/day/inactive` — listar rutinas inactivas de día
- `GET /api/routines/user/{userId}/night/inactive` — listar rutinas inactivas de noche

#### Crear rutinas iniciales

- `POST /api/routines/user/{userId}/day/initial`
- `POST /api/routines/user/{userId}/night/initial`

#### Productos en rutina

- `POST /api/routines/{routineId}/products/{productId}` — agregar producto
- `DELETE /api/routines/{routineId}/products/{productId}` — quitar producto

#### Activar / desactivar rutina

- `PATCH /api/routines/{id}/activate`
- `PATCH /api/routines/{id}/deactivate`

## 💡 Aprendizajes

- Diseño de APIs REST orientadas a aplicaciones mobile
- Implementación de autenticación con JWT
- Manejo de relaciones complejas entre entidades
- Separación de responsabilidades por capas (controller, service, repository)
