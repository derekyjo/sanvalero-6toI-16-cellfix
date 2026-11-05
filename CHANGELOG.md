# Changelog

Todas las versiones relevantes de CellFix se documentan en este archivo.
El formato sigue [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/) y
[Versionado Semántico](https://semver.org/lang/es/).

## [1.0.0] - 2026-11-05

### Añadido

- Autenticación local con dos roles (`ADMIN` y `OPERADOR`) y contraseñas con PBKDF2 + sal.
- Gestión completa de clientes con validación de cédula dominicana.
- Gestión de equipos identificados por IMEI único de 15 dígitos.
- Gestión de técnicos con especialidad, estado activo y protección de integridad.
- Órdenes de reparación con diagnóstico, costos de mano de obra y repuestos.
- Cálculo automático de ITBIS (18 %) y totales en RD$.
- Generación automática de garantía de 30 días al entregar una reparación.
- Control de garantías por IMEI, con bloqueo de duplicados vigentes.
- Ventas con múltiples artículos, subtotal, ITBIS y total.
- Panel con indicadores, ingresos y gráfico por estado de reparación.
- Búsqueda y filtros por texto y por rango de fechas.
- Reportes PDF, reporte de período PDF + CSV y resumen de gestión.
- Respaldo de la base de datos SQLite: exportar e importar.
- Tema Material 3 propio con modo claro y oscuro, icono adaptativo.
- Documentación completa, bitácora de IA y pruebas unitarias de reglas de negocio.

[1.0.0]: https://github.com/derekyjo/sanvalero-6toI-16-cellfix/releases/tag/v1.0.0
