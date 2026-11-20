-- Huerta Escolar | MariaDB 10.4+
-- Seleccionar una base vacia antes de importar. No elimina datos.

CREATE TABLE grupos (
 id INT(11) NOT NULL AUTO_INCREMENT, nombre VARCHAR(255) NOT NULL,
 PRIMARY KEY (id), UNIQUE KEY uk_grupos_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE usuarios (
 id INT(11) NOT NULL AUTO_INCREMENT, nombre VARCHAR(255) NOT NULL,
 rol ENUM('ADMINISTRADOR','ESTUDIANTE') NOT NULL, grupo_id INT(11) DEFAULT NULL,
 PRIMARY KEY (id), KEY idx_usuarios_grupo (grupo_id),
 CONSTRAINT usuarios_ibfk_1 FOREIGN KEY (grupo_id) REFERENCES grupos(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE sectores (
 id INT(11) NOT NULL AUTO_INCREMENT, nombre VARCHAR(255) NOT NULL,
 descripcion VARCHAR(500) DEFAULT NULL, ubicacion VARCHAR(255) DEFAULT NULL,
 estado ENUM('DISPONIBLE','EN_USO','EN_MANTENIMIENTO','FUERA_DE_USO') NOT NULL DEFAULT 'DISPONIBLE',
 responsable_id INT(11) DEFAULT NULL, PRIMARY KEY (id), KEY idx_sectores_responsable (responsable_id),
 CONSTRAINT sectores_ibfk_1 FOREIGN KEY (responsable_id) REFERENCES usuarios(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE cultivos (
 id INT(11) NOT NULL AUTO_INCREMENT, nombre VARCHAR(255) NOT NULL, descripcion VARCHAR(500) DEFAULT NULL,
 sector_id INT(11) NOT NULL, fecha_siembra DATE DEFAULT NULL, fecha_cosecha_estimada DATE DEFAULT NULL,
 frecuencia_riego_dias INT(11) NOT NULL DEFAULT 1,
 estado ENUM('PLANIFICADO','SEMBRADO','EN_CRECIMIENTO','LISTO_PARA_COSECHAR','COSECHADO','CON_PROBLEMAS','FINALIZADO') NOT NULL DEFAULT 'PLANIFICADO',
 observaciones VARCHAR(1000) DEFAULT NULL, foto_uri VARCHAR(1000) DEFAULT NULL,
 PRIMARY KEY (id), KEY idx_cultivos_estado (estado), KEY idx_cultivos_sector (sector_id),
 CONSTRAINT cultivos_ibfk_1 FOREIGN KEY (sector_id) REFERENCES sectores(id),
 CONSTRAINT chk_cultivos_frecuencia CHECK (frecuencia_riego_dias > 0),
 CONSTRAINT chk_cultivos_fechas CHECK (fecha_siembra IS NULL OR fecha_cosecha_estimada IS NULL OR fecha_cosecha_estimada >= fecha_siembra)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tareas (
 id INT(11) NOT NULL AUTO_INCREMENT,
 tipo ENUM('PREPARACION_SECTOR','SIEMBRA','TRASPLANTE','RIEGO','LIMPIEZA','RETIRO_MALEZAS','OBSERVACION','MANTENIMIENTO','REVISION_SUELO','REGISTRO_CRECIMIENTO','COSECHA','OTRA') NOT NULL,
 descripcion VARCHAR(255) NOT NULL, instrucciones VARCHAR(1000) NOT NULL,
 sector_id INT(11) DEFAULT NULL, cultivo_id INT(11) DEFAULT NULL,
 fecha_creacion BIGINT(20) DEFAULT NULL, fecha_programada BIGINT(20) NOT NULL,
 responsable_id INT(11) DEFAULT NULL, grupo_asignado_id INT(11) DEFAULT NULL,
 creada_por_id INT(11) NOT NULL,
 estado ENUM('PENDIENTE','EN_DESARROLLO','REALIZADA','PENDIENTE_VALIDACION','VALIDADA','RECHAZADA','ATRASADA') NOT NULL DEFAULT 'PENDIENTE',
 fecha_realizacion BIGINT(20) DEFAULT NULL, realizada_por_id INT(11) DEFAULT NULL,
 PRIMARY KEY (id), KEY idx_tareas_estado (estado), KEY idx_tareas_fecha (fecha_programada), KEY idx_tareas_sector (sector_id), KEY idx_tareas_cultivo (cultivo_id),
 KEY idx_tareas_responsable (responsable_id), KEY idx_tareas_grupo (grupo_asignado_id),
 KEY idx_tareas_creada_por (creada_por_id), KEY idx_tareas_realizada_por (realizada_por_id),
 CONSTRAINT tareas_ibfk_1 FOREIGN KEY (sector_id) REFERENCES sectores(id),
 CONSTRAINT tareas_ibfk_2 FOREIGN KEY (cultivo_id) REFERENCES cultivos(id),
 CONSTRAINT tareas_ibfk_3 FOREIGN KEY (responsable_id) REFERENCES usuarios(id),
 CONSTRAINT tareas_ibfk_4 FOREIGN KEY (grupo_asignado_id) REFERENCES grupos(id),
 CONSTRAINT tareas_ibfk_5 FOREIGN KEY (creada_por_id) REFERENCES usuarios(id),
 CONSTRAINT tareas_ibfk_6 FOREIGN KEY (realizada_por_id) REFERENCES usuarios(id),
 CONSTRAINT chk_tareas_ubicacion CHECK ((sector_id IS NOT NULL AND cultivo_id IS NULL) OR (sector_id IS NULL AND cultivo_id IS NOT NULL)),
 CONSTRAINT chk_tareas_asignacion CHECK ((responsable_id IS NOT NULL AND grupo_asignado_id IS NULL) OR (responsable_id IS NULL AND grupo_asignado_id IS NOT NULL))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE evidencias (
 id INT(11) NOT NULL AUTO_INCREMENT, tarea_id INT(11) NOT NULL, usuario_id INT(11) DEFAULT NULL,
 foto_uri VARCHAR(1000) DEFAULT NULL, comentario VARCHAR(1000) DEFAULT NULL, fecha_registro BIGINT(20) DEFAULT NULL,
 PRIMARY KEY (id), KEY idx_evidencias_tarea (tarea_id, id), KEY idx_evidencias_usuario (usuario_id),
 CONSTRAINT evidencias_ibfk_1 FOREIGN KEY (tarea_id) REFERENCES tareas(id) ON DELETE CASCADE,
 CONSTRAINT evidencias_ibfk_2 FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
 CONSTRAINT chk_evidencias_contenido CHECK (COALESCE(CHAR_LENGTH(TRIM(foto_uri)),0) > 0 OR COALESCE(CHAR_LENGTH(TRIM(comentario)),0) > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE validaciones (
 id INT(11) NOT NULL AUTO_INCREMENT, tarea_id INT(11) NOT NULL, validador_id INT(11) NOT NULL,
 resultado ENUM('VALIDADA','RECHAZADA') NOT NULL, comentario VARCHAR(1000) DEFAULT NULL, fecha_validacion BIGINT(20) NOT NULL,
 PRIMARY KEY (id), KEY idx_validaciones_tarea (tarea_id, id), KEY idx_validaciones_validador (validador_id),
 CONSTRAINT validaciones_ibfk_1 FOREIGN KEY (tarea_id) REFERENCES tareas(id) ON DELETE CASCADE,
 CONSTRAINT validaciones_ibfk_2 FOREIGN KEY (validador_id) REFERENCES usuarios(id),
 CONSTRAINT chk_validaciones_rechazo CHECK (resultado <> 'RECHAZADA' OR COALESCE(CHAR_LENGTH(TRIM(comentario)),0) > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE problemas (
 id INT(11) NOT NULL AUTO_INCREMENT, sector_id INT(11) DEFAULT NULL, cultivo_id INT(11) DEFAULT NULL,
 tipo ENUM('FALTA_AGUA','DANO_PLANTA','PLAGA_SIMULADA','DETERIORO_SECTOR','PROBLEMA_MANTENCION','OTRO') NOT NULL,
 descripcion VARCHAR(1000) NOT NULL, fecha_reporte BIGINT(20) DEFAULT NULL, reportado_por_id INT(11) NOT NULL,
 foto_uri VARCHAR(1000) DEFAULT NULL,
 estado ENUM('INFORMADO','EN_REVISION','ATENDIDO','CERRADO') NOT NULL DEFAULT 'INFORMADO',
 comentario_docente VARCHAR(1000) DEFAULT NULL, fecha_cierre BIGINT(20) DEFAULT NULL,
 PRIMARY KEY (id), KEY idx_problemas_estado (estado), KEY idx_problemas_sector (sector_id), KEY idx_problemas_cultivo (cultivo_id), KEY idx_problemas_reportado_por (reportado_por_id),
 CONSTRAINT problemas_ibfk_1 FOREIGN KEY (sector_id) REFERENCES sectores(id),
 CONSTRAINT problemas_ibfk_2 FOREIGN KEY (cultivo_id) REFERENCES cultivos(id),
 CONSTRAINT problemas_ibfk_3 FOREIGN KEY (reportado_por_id) REFERENCES usuarios(id),
 CONSTRAINT chk_problemas_ubicacion CHECK ((sector_id IS NOT NULL AND cultivo_id IS NULL) OR (sector_id IS NULL AND cultivo_id IS NOT NULL))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Vistas sin DEFINER fijo para permitir importar en otros equipos.
CREATE SQL SECURITY INVOKER VIEW tareas_con_sector AS
SELECT t.id AS id, t.tipo AS tipo, t.descripcion AS descripcion, t.instrucciones AS instrucciones,
 t.sector_id AS sector_id, t.cultivo_id AS cultivo_id, t.fecha_creacion AS fecha_creacion,
 t.fecha_programada AS fecha_programada, t.responsable_id AS responsable_id,
 t.grupo_asignado_id AS grupo_asignado_id, t.creada_por_id AS creada_por_id, t.estado AS estado,
 t.fecha_realizacion AS fecha_realizacion, t.realizada_por_id AS realizada_por_id,
 (SELECT e.comentario FROM evidencias e WHERE e.tarea_id = t.id ORDER BY e.id DESC LIMIT 1) AS comentario_estudiante,
 COALESCE(t.sector_id, c.sector_id) AS sector_efectivo_id
FROM tareas t LEFT JOIN cultivos c ON c.id = t.cultivo_id;

CREATE SQL SECURITY INVOKER VIEW problemas_con_sector AS
SELECT p.id AS id, p.sector_id AS sector_id, p.cultivo_id AS cultivo_id, p.tipo AS tipo,
 p.descripcion AS descripcion, p.fecha_reporte AS fecha_reporte, p.reportado_por_id AS reportado_por_id,
 p.foto_uri AS foto_uri, p.estado AS estado, p.comentario_docente AS comentario_docente,
 p.fecha_cierre AS fecha_cierre, COALESCE(p.sector_id, c.sector_id) AS sector_efectivo_id
FROM problemas p LEFT JOIN cultivos c ON c.id = p.cultivo_id;
