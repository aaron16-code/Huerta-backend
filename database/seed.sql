-- HUERTO ESCOLAR - SEED
-- Ejecutar después de schema.sql. Datos iniciales mínimos verificados.
-- Seleccionar la misma base vacia donde se importo schema.sql.
INSERT INTO grupos (id, nombre) VALUES (1, 'Grupo A'), (2, 'Grupo B');
INSERT INTO usuarios (id, nombre, rol, grupo_id) VALUES
 (1, 'Administrador de prueba', 'ADMINISTRADOR', NULL),
 (2, 'Estudiante de prueba', 'ESTUDIANTE', 1);
-- Los sectores, cultivos, tareas, evidencias, validaciones y problemas
-- se crean durante las pruebas funcionales.
