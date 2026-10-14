# Verificación y apuntes de entrega

Se ejecutó `mvn test`: 3 pruebas, 0 fallos, 0 errores y 0 omitidas. Se ejecutaron peticiones reales mediante Spring MockMvc y se verificaron los registros en H2. Esto no equivale a ejecutar Postman ni a probar un servidor MySQL.

| Caso verificado | Resultado |
|---|---|
| Ana / USER crea producto | 201, CREAR con Ana (USER) |
| Luis / USER lista productos | 200, LISTAR con cantidad 1 |
| Ricardo / ADMIN actualiza | 200, ACTUALIZAR con ID |
| Ricardo / ADMIN elimina | 204, ELIMINAR con ID |
| Sin cabeceras | 401 y ERROR persistido |
| Ana afirma ser ADMIN | 403 y ERROR persistido |
| USER intenta eliminar | 403 y ERROR persistido |
| ADMIN elimina ID inexistente | 404 y ERROR persistido |
| Nombre vacío, precio y stock negativos | 400, no se crea producto |
| USER consulta auditoría | 403 |
| ADMIN consulta auditoría | 200 |

## Primera guía: observaciones propuestas

1. Los aspectos operan sobre servicios administrados por Spring; las llamadas deben atravesar el proxy para aplicar AOP.
2. El listado se audita después de obtener resultados para registrar la cantidad real.
3. La actualización y la eliminación reciben el identificador como primer parámetro, disponible mediante JoinPoint.
4. Los errores se conservan usando una transacción independiente en AuditoriaService.
5. Las validaciones del cuerpo se ejecutan en el controlador antes de invocar el servicio.

## Primera guía: conclusiones propuestas

1. AOP permite centralizar auditoría sin repetir registros en cada método del CRUD.
2. Los parámetros y resultados producen información más útil que mensajes fijos.
3. Una operación fallida genera ERROR y no un evento de éxito.
4. Las pruebas de integración verifican tanto las respuestas HTTP como la persistencia de eventos.
5. La persistencia independiente facilita diagnosticar fallos, aunque requiere definir cómo responder si la auditoría no está disponible.

## Segunda guía: observaciones propuestas

1. Se exige enviar conjuntamente Usuario y Rol; su ausencia devuelve 401.
2. El rol declarado se compara con el mapa centralizado para rechazar combinaciones incorrectas con 403.
3. USER puede crear y listar por la actividad final, mientras actualizar y eliminar requieren ADMIN.
4. Cada evento del CRUD registra usuario y rol, además de fecha, método, acción y detalle.
5. Las cabeceras ofrecen una demostración académica de permisos, pero no demuestran la identidad de quien realiza la petición.

## Segunda guía: conclusiones propuestas

1. La verificación centralizada mantiene una política consistente entre operaciones.
2. Separar 401, 403, 400 y 404 ayuda a interpretar los resultados del laboratorio.
3. Asociar usuarios a eventos mejora la trazabilidad de los cambios.
4. Verificar accesos rechazados es tan necesario como verificar el CRUD exitoso.
5. Para una aplicación real se necesitaría autenticación verificable y una política de auditoría más completa.

Estos apuntes se basan en esta implementación; revisarlos y adaptarlos a la experiencia personal antes de entregar.

## Prueba del JAR por HTTP

El puerto 8080 estaba ocupado por otro proceso. Se inicio el JAR en 8086 y se verificaron POST 201, GET 200, PUT 200 y DELETE 204 con peticiones HTTP reales, ademas de los cuatro eventos en /api/auditoria. El producto de prueba fue eliminado; sus eventos se conservan en la base local excluida de Git. El servidor queda ejecutandose en http://localhost:8086.
