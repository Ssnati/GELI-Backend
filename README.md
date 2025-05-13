# GELI Backend

Este proyecto es un backend desarrollado en **Java** utilizando el framework **Spring Boot** y gestionado con **Maven**. Proporciona servicios relacionados con la autenticación y la gestión de usuarios a través de Keycloak, así como la gestión de equipos y laboratorios.

## Endpoints Disponibles

### Keycloak User Controller

Controlador para la gestión de usuarios en Keycloak.

- **GET /api/keycloak/users**  
  Devuelve una lista de usuarios registrados en Keycloak.

### Equipment Controller

Controlador para la gestión de equipos.

- **GET /api/v1/equipments**  
  Devuelve una lista de todos los equipos registrados.

- **GET /api/v1/equipments/{id}**  
  Devuelve los detalles de un equipo específico por su ID.

- **POST /api/v1/equipments**  
  Crea un nuevo equipo.

- **PUT /api/v1/equipments/{id}**  
  Actualiza los detalles de un equipo existente.

- **DELETE /api/v1/equipments/{id}**  
  Elimina un equipo por su ID.

- **POST /api/v1/equipments/filter**  
  Filtra equipos según criterios como nombre, marca, número de inventario, laboratorio y disponibilidad.

### Laboratory Controller

Controlador para la gestión de laboratorios.

- **GET /api/v1/laboratories**  
  Devuelve una lista de todos los laboratorios registrados.

- **GET /api/v1/laboratories/{id}**  
  Devuelve los detalles de un laboratorio específico por su ID.

- **POST /api/v1/laboratories**  
  Crea un nuevo laboratorio.

- **PUT /api/v1/laboratories/{id}**  
  Actualiza los detalles de un laboratorio existente.

- **DELETE /api/v1/laboratories/{id}**  
  Elimina un laboratorio por su ID.

- **POST /api/v1/laboratories/filter**  
  Filtra laboratorios según criterios como nombre, disponibilidad, ubicación y descripción.

## Requisitos Previos

- **Java 17** o superior.
- **Maven** para la gestión de dependencias.
- Un servidor Keycloak configurado para la integración en el puerto **9090**.

## Configuración de Keycloak

1. **Puerto**: Asegúrate de que Keycloak esté configurado para ejecutarse en el puerto **9090**.

2. **Realm**: Crea un `Realm` llamado **geli-dev**.

3. **Cliente**: 
   - Crea un cliente llamado **geli-backend** en el `Realm` **geli-dev**.
   - Configura el tipo de acceso del cliente como **confidential**.
   - Genera un secreto para el cliente y guárdalo en una variable de entorno con el nombre **CLIENT_SECRET**.

4. **Permisos**: 
   - Asegúrate de que el cliente **geli-backend** tenga permisos para realizar consultas a la API de administración de Keycloak.

5. **Usuarios y Roles**: 
   - Define los usuarios y roles necesarios en el `Realm` **geli-dev** según los requisitos del proyecto.

#### Documentación completa del código
[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/Ssnati/GELI-Backend)
