# 08 · Mejoras futuras

Esta lista recoge el trabajo previsto después de la versión `v1.0.0`. No forma parte de
los requisitos entregados, pero sirve como hoja de ruta para la rama `develop`.

## 1. Sincronización y respaldo en la nube

- Copia de seguridad automática a Google Drive o a un servidor propio.
- Exportación programada con WorkManager.
- Restauración guiada con validación de versión de esquema.

## 2. Notificaciones

- Recordatorio de garantías próximas a vencer.
- Aviso cuando una reparación lleva demasiado tiempo en estado `EN_REPARACION`.
- Notificación al cliente por WhatsApp o correo con el estado del equipo.

## 3. Ventas y facturación

- Impresión térmica de facturas (ESC/POS).
- Envío de factura en PDF por correo.
- Devoluciones y notas de crédito.
- Control de inventario de accesorios y repuestos.

## 4. Seguridad

- Bloqueo biométrico de la aplicación.
- Cierre de sesión automático por inactividad.
- Registro de auditoría de acciones por usuario.
- Recuperación de contraseña mediante pregunta de seguridad local.

## 5. Experiencia de usuario

- Modo tablet con lista y detalle en dos paneles.
- Accesibilidad: contenido para lectores de pantalla y tamaños de fuente dinámicos.
- Filtros guardados por usuario.
- Exportación de reportes a Excel (XLSX).

## 6. Calidad

- Pruebas instrumentadas con Compose UI Test.
- Pruebas de integración de Room con base de datos en memoria.
- Integración continua con GitHub Actions para `assembleDebug` y `testDebugUnitTest`.
- Análisis estático con Detekt y Ktlint.

## 7. Internacionalización

- Soporte de inglés además del español.
- Formatos regionales configurables (país, moneda e impuesto).
