# GELI Backend

Este proyecto es un backend desarrollado en **Java** utilizando el framework **Spring Boot** y gestionado con **Maven**. Proporciona servicios relacionados con la autenticación y la gestión de usuarios a través de Keycloak.

## Endpoints Disponibles

### Keycloak User Controller

Controlador para la gestión de usuarios en Keycloak.

- **GET /api/keycloak/users**  
  Devuelve una lista de usuarios registrados en Keycloak.


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