# Backend normalizado - Huerta Escolar

## Tecnologías

- Java 17 o compatible (probado con Java 21).
- Spring Boot 3.3.5.
- Spring Data JPA.
- Hibernate.
- MariaDB 10.4.
- Maven.
- API REST.

## Instalación

1. Iniciar MariaDB desde XAMPP.
2. Crear una base de datos vacía llamada `huerto_normalizado`.
3. Importar `database/schema.sql`.
4. Importar `database/seed.sql` para obtener usuarios y grupos de prueba.
5. Configurar las variables DB_USER y DB_PASSWORD según corresponda.
6. Ejecutar `mvn clean test`.
7. Ejecutar `mvn spring-boot:run`.

El servidor utiliza el puerto 8080. Hibernate utiliza `ddl-auto=validate` para verificar el esquema sin modificarlo automáticamente.

No importar los scripts de instalación sobre una base de datos que ya contiene información.

## Base de datos

La base de datos normalizada contiene:

- 8 tablas.
- 16 claves foráneas.
- 7 restricciones CHECK.
- 2 vistas SQL.
- 32 índices.
- Eliminación en cascada de evidencias y validaciones asociadas a tareas.

Las evidencias y validaciones se almacenan independientemente de las tareas, evitando duplicación de información.

## Endpoints REST

- `/api/usuarios`
- `/api/sectores`
- `/api/cultivos`
- `/api/tareas`
- `/api/problemas`

Se mantienen los contratos de la API para facilitar la integración con Android mediante Retrofit.

## Pruebas realizadas

- CultivoService: 7 pruebas aprobadas.
- ProblemaService: 10 pruebas aprobadas.
- SectorService: 7 pruebas aprobadas.
- TareaService: 17 pruebas aprobadas.
- UsuarioService: 4 pruebas aprobadas.

Total: 45 pruebas aprobadas, 0 fallos y 0 errores.

Se comprobó la conexión con MariaDB, las operaciones REST, las restricciones de integridad y la conservación de los datos después de la migración.

## Pendientes de integración

- Actualización de Room y Retrofit en Android.
- Verificación completa de los flujos desde la aplicación móvil.
- Transferencia real de archivos fotográficos.
- Autenticación y autorización en el servidor.

La configuración actual está destinada al desarrollo académico local.