-- Esquema final para una base nueva. No elimina tablas ni datos existentes.
CREATE DATABASE IF NOT EXISTS laboratorio06;
USE laboratorio06;
CREATE TABLE IF NOT EXISTS producto (
 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 nombre VARCHAR(100) NOT NULL,
 precio DECIMAL(12,2) NOT NULL,
 stock INT NOT NULL
);
CREATE TABLE IF NOT EXISTS auditoria_log (
 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 accion VARCHAR(50), metodo VARCHAR(100), fecha TIMESTAMP,
 detalle VARCHAR(2000), usuario VARCHAR(100)
);
-- Si ya existe la tabla de la primera guía y falta usuario, ejecutar una sola vez:
-- ALTER TABLE auditoria_log ADD usuario VARCHAR(100);
-- ALTER TABLE auditoria_log MODIFY detalle VARCHAR(2000);
SELECT * FROM auditoria_log ORDER BY id DESC;
