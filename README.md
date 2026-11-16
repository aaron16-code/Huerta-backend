# Huerto API (microservicio Spring Boot + MySQL)

Backend de la app **Huerto Escolar** (DSY1105). Un solo microservicio con **una sola base de datos** (`huerto_escolar`) y relaciones uno-a-muchos.

## Modelo de datos

```
usuarios 1 ──< sectores (responsable)
usuarios 1 ──< tareas (responsable, creada_por, realizada_por, validador)
usuarios 1 ──< problemas (reportado_por)
sectores 1 ──< cultivos
sectores 1 ──< tareas
sectores 1 ──< problemas
cultivos 1 ──< tareas / problemas (opcional)
```

## Cómo ejecutarlo

1. Abrir XAMPP y encender **MySQL** (root, sin contraseña).
2. Abrir esta carpeta en IntelliJ (el `pom.xml` está en la raíz) y activar *annotation processing* (Lombok).
3. Ejecutar `HuertoApiApplication`. La base `huerto_escolar` y las 5 tablas se crean solas.
4. Probar: <http://localhost:8080/api/sectores>

Si tu MySQL tiene otra contraseña: variable de entorno `DB_PASSWORD` (y `DB_USER`).

Desde el emulador Android la URL es `http://10.0.2.2:8080/`.

## Endpoints (todos con GET, GET/{id}, POST, PUT/{id}, DELETE/{id})

| Recurso | Ruta | Filtros |
|---|---|---|
| Usuarios | `/api/usuarios` | — |
| Sectores | `/api/sectores` | — |
| Cultivos | `/api/cultivos` | `?sectorId=` |
| Tareas | `/api/tareas` | `?estado=` `?responsableId=` |
| Problemas | `/api/problemas` | `?reportadoPorId=` |

Errores: 400 (datos inválidos o regla de negocio), 404 (no existe), 409 (restricción de BD, p. ej. borrar un sector con cultivos).

## Reglas de negocio

- Tarea: debe asignarse a un estudiante o a un grupo; nace `PENDIENTE`; `VALIDADA` exige validador; `RECHAZADA` exige comentario del docente; el cultivo debe pertenecer al sector.
- Problema: nace `INFORMADO`; al pasar a `CERRADO` se fija la fecha de cierre, al reabrir se borra.
- Sector: nombre único. Cultivo: el sector debe existir. Usuario: un estudiante debe tener grupo.

## Tests

`mvn test` (o botón verde en IntelliJ sobre `src/test`): pruebas unitarias de los 5 servicios con Mockito, sin base de datos.
