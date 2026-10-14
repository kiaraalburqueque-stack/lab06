# Laboratorio 06 — Productos, AOP y roles

Implementación conjunta de las dos guías GLAB-S06: auditoría completa de productos y extensión Usuario/Rol. Incluye todas las actividades finales. Como no se proporcionó el proyecto de semana 5, se creó la API base desde cero.

## Ejecutar en Windows

Requiere JDK 17 o superior; se verificó con JDK 25. La primera ejecución necesita internet para descargar Maven y dependencias.

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

API: http://localhost:8080/api/productos. Para detenerla: Ctrl+C.

La configuración predeterminada usa H2 persistente en `data/`, sin instalar un servidor de base de datos. Hibernate crea las tablas. No se cargan productos ficticios.

Para generar y ejecutar el archivo distribuible:

```powershell
.\mvnw.cmd clean package
java -jar target/laboratorio06-1.0.0.jar
```

## MySQL

Con un servidor MySQL en ejecución:

```powershell
$env:DB_USER = "root"
$env:DB_PASSWORD = "tu_clave_local"
# Opcional: $env:DB_URL = "jdbc:mysql://localhost:3306/laboratorio06?createDatabaseIfNotExist=true"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=mysql"
```

Se incluye `docs/schema-mysql.sql` para inspección o creación manual. El perfil MySQL no se ha probado contra un servidor real en esta entrega. Las pruebas automatizadas usan H2.

## Usuarios y permisos

Enviar las cabeceras `Usuario` y `Rol` en cada petición:

| Usuario | Rol | Crear | Listar | Actualizar | Eliminar | Consultar auditoría |
|---|---|---|---|---|---|---|
| Ricardo | ADMIN | Sí | Sí | Sí | Sí | Sí |
| Ana | USER | Sí | Sí | No | No | No |
| Luis | USER | Sí | Sí | No | No | No |

La actividad final amplía USER para permitir creación. Las cabeceras reproducen la seguridad básica de la guía; no autentican mediante contraseña y no deben usarse como autenticación de producción.

| Método | Ruta | Respuesta correcta |
|---|---|---|
| POST | /api/productos | 201 y producto creado |
| GET | /api/productos | 200 y lista |
| PUT | /api/productos/{id} | 200 y producto actualizado |
| DELETE | /api/productos/{id} | 204 |
| GET | /api/auditoria | 200, eventos recientes primero, solo ADMIN |

POST y PUT reciben:

```json
{"nombre":"disco duro","precio":2500,"stock":10}
```

Nombre obligatorio (máximo 100 caracteres), precio positivo con máximo dos decimales y stock entero no negativo. Respuestas: 400 datos inválidos; 401 cabeceras ausentes; 403 identidad/rol incorrecto o permiso insuficiente; 404 producto inexistente.

## Postman y evidencia

Importar `docs/Laboratorio06.postman_collection.json`. Ejecutar la colección en orden; la creación captura `productoId` automáticamente. Incluye verificaciones de estados HTTP. `baseUrl` se puede modificar.

`AuditoriaAspect` valida permisos y registra CREAR, ACTUALIZAR, LISTAR y ELIMINAR después de una operación exitosa. Usa parámetros de JoinPoint, el producto devuelto y la cantidad de resultados. `LoggingAspect` muestra inicio/final y `ErrorAspect` persiste ERROR en una transacción independiente para conservarlo tras un fallo. Se limita el aspecto de errores a ProductoService para evitar recursión al guardar auditoría.

La validación del JSON sucede antes de entrar al servicio: los errores 400 se devuelven al cliente y no generan un evento del aspecto de servicio. Los rechazos del endpoint de consulta de auditoría tampoco se auditan. La auditoría de operaciones exitosas se confirma en una transacción separada; si falla su almacenamiento, la operación de producto puede haber sido confirmada.

Pruebas: tres escenarios de integración comprueban CRUD completo, USER creador, auditoría con usuario/cantidad/ID, 401, 403, 404, persistencia de errores, validación y acceso a la auditoría. Ver `docs/RESULTADOS.md`.

## GitHub

El proyecto está preparado para subir desde esta carpeta; `target/`, `data/`, archivos de entorno y logs están excluidos. No contiene credenciales de base de datos. Para un repositorio vacío:

```powershell
git init
git add .
git commit -m "Implementar laboratorio 06: AOP, auditoria y roles"
git branch -M main
git remote add origin https://github.com/kiaraalburqueque-stack/lab06.git
git push -u origin main
```

Repositorio: https://github.com/kiaraalburqueque-stack/lab06.

## Puerto usado en esta sesion

Como 8080 ya estaba ocupado, se dejo el servidor del laboratorio en http://localhost:8086/api/productos. En Postman cambiar baseUrl a http://localhost:8086. Para repetir este arranque: `java -jar target/laboratorio06-1.0.0.jar --server.port=8086`. Para detener el proceso actual en PowerShell: `Stop-Process -Id 43300`, tras comprobar que corresponde a este JAR.

## Interfaz web

Abre http://localhost:8086/ si ejecutas el servidor en 8086, o http://localhost:8080/ con el puerto predeterminado. Spring Boot sirve el frontend incluido en `src/main/resources/static/`; no necesita Node ni un servidor separado.

- Selecciona Ricardo (ADMIN), Ana (USER) o Luis (USER). La interfaz envía las cabeceras Usuario y Rol automáticamente.
- Crea productos, busca por nombre y revisa el número de productos y unidades de stock.
- Ricardo puede editar, eliminar con confirmación y consultar la auditoría. Ana y Luis pueden crear y listar.
- Los permisos también se validan en el backend. El selector es una demostración académica, no autenticación por contraseña.

La pantalla muestra estados vacíos, errores de la API y confirmación de las operaciones. Cada actualización del listado genera el evento LISTAR previsto por el laboratorio.
