# 01 · Análisis de requisitos

## 1. Contexto

CellFix es el sistema de gestión de un negocio dominicano dedicado a la venta y
reparación de teléfonos móviles. El negocio necesita controlar clientes, equipos
(identificados por IMEI), órdenes de reparación, técnicos, garantías y ventas, además de
emitir reportes para la administración.

El proyecto es individual y debe ejecutarse en un dispositivo Android real, con el
código publicado en GitHub.

## 2. Actores

| Actor | Descripción | Permisos |
|---|---|---|
| Administrador | Dueño o encargado del negocio | Todos los módulos, gestión de técnicos, reportes y respaldo |
| Operador | Empleado del mostrador | Clientes, equipos, reparaciones, garantías y ventas |
| Sistema | Automatizaciones internas | Cálculo de ITBIS, generación de garantías, reportes y respaldo |

## 3. Requisitos funcionales

| Código | Requisito | Implementación |
|---|---|---|
| RF-01 | Inicio de sesión local con dos roles y contraseña protegida | `LoginScreen`, `LoginUseCase`, `CryptoUtils`, `SessionManager` |
| RF-02 | CRUD de clientes con validaciones dominicanas | `ClientesScreen`, `ClienteViewModel`, `ValidadorUseCase` |
| RF-03 | CRUD de equipos con IMEI único de 15 dígitos | `EquiposScreen`, `EquipoViewModel`, índice único `imei` |
| RF-04 | CRUD de técnicos con especialidad y estado | `TecnicosScreen`, `TecnicoViewModel` |
| RF-05 | CRUD de reparaciones con equipo, técnico, costos y estados | `ReparacionesScreen`, `ReparacionViewModel` |
| RF-06 | Cálculo automático de ITBIS 18 % y total en RD$ | `CalculadoraUseCase`, `Formato.moneda` |
| RF-07 | Garantía automática de 30 días al entregar y control por IMEI | `GarantiaUseCase`, `ReparacionViewModel.cambiarEstado` |
| RF-08 | Ventas con varios artículos, ITBIS y método de pago | `VentasScreen`, `VentaViewModel`, `DetalleVenta` |
| RF-09 | Dashboard con indicadores y gráfico | `DashboardScreen`, `DashboardViewModel` |
| RF-10 | Búsqueda de texto en clientes, equipos, reparaciones y garantías | Campos de búsqueda en cada ViewModel |
| RF-11 | Reportes PDF y CSV por período | `ReportGenerator`, `ReportesScreen` |
| RF-12 | Respaldo de la base de datos (exportar/importar) | `BackupManager`, `ReportesScreen` |
| RF-13 | Tema Material 3 propio, icono y modo oscuro | `Theme.kt`, `ic_launcher_foreground.xml` |
| RF-14 | Datos de demostración | `CellFixRepositoryImpl.seedIfEmpty` |

## 4. Requisitos no funcionales

| Código | Requisito | Decisión |
|---|---|---|
| RNF-01 | Código mantenible y separado por capas | Clean Architecture + MVVM |
| RNF-02 | Interfaz moderna y adaptable | Compose + Material 3, tema claro/oscuro |
| RNF-03 | Operaciones de base de datos fuera del hilo principal | Corrutinas + Room + Flow |
| RNF-04 | Contraseñas seguras | PBKDF2-HMAC-SHA256 con sal aleatoria |
| RNF-05 | Compatibilidad | `minSdk 26`, `targetSdk 35` |
| RNF-06 | Idioma y formatos locales | Español, RD$, cédula/RNC, `dd/MM/yyyy` |
| RNF-07 | Portabilidad de datos | Exportar/importar SQLite |
| RNF-08 | Documentación y trazabilidad de IA | `docs/`, `AGENTS.md`, bitácora |

## 5. Casos de uso principales

### CU-01 · Iniciar sesión

- Actor: administrador u operador.
- Flujo: escribe usuario y contraseña → el sistema valida el hash → guarda la sesión →
  muestra el dashboard.
- Excepción: credenciales incorrectas → mensaje de error sin revelar cuál dato falló.

### CU-02 · Registrar cliente y equipo

- Actor: operador.
- Flujo: crea cliente → valida cédula de 11 dígitos → guarda → crea equipo asociado →
  valida IMEI de 15 dígitos y unicidad.
- Excepción: cédula o IMEI duplicado → mensaje y no se guarda.

### CU-03 · Abrir y entregar una reparación

- Actor: operador o administrador.
- Flujo: selecciona equipo y técnico → describe falla → ingresa costos → el sistema
  calcula ITBIS y total → guarda con estado `RECIBIDO` → cambia estados → al elegir
  `ENTREGADO` registra fecha y crea garantía de 30 días.
- Excepción: el equipo ya tiene garantía vigente → no se duplica y se informa.

### CU-04 · Consultar garantía por IMEI

- Actor: operador.
- Flujo: entra a Garantías → escribe total o parcialmente el IMEI → ve equipo, cliente,
  fechas y si está vigente o vencida.

### CU-05 · Registrar venta

- Actor: operador.
- Flujo: selecciona cliente → agrega artículos con cantidad y precio → el sistema calcula
  subtotal, ITBIS y total → elige método de pago → guarda venta y detalle en una
  transacción.

### CU-06 · Generar reportes y respaldo

- Actor: administrador.
- Flujo: elige rango de fechas → genera PDF/CSV → comparte o guarda → exporta el archivo
  SQLite de respaldo.
- Excepción: no hay datos en el período → el reporte se genera con total 0.

## 6. Alcance

### Incluido

- Operación local sin servidor ni Internet.
- Un solo negocio y un solo dispositivo.
- Datos de demostración y usuarios de prueba.
- Reportes en PDF/CSV y respaldo manual.

### Fuera de alcance

- Sincronización en la nube o multiusuario simultáneo.
- Facturación electrónica con la DGII.
- Pasarela de pagos.
- Firma digital de garantías.
- Notificaciones push.

## 7. Trazabilidad con la asignación

| Exigencia de la asignación | Evidencia |
|---|---|
| App individual en Kotlin instalada en dispositivo real | `docs/05-pruebas-y-despliegue.md` |
| Compose + Material 3 | `ui/theme`, todas las pantallas |
| Room/SQLite | `data/local` |
| MVVM + Clean Architecture (3 capas) | `data`, `domain`, `ui` |
| Hilt | `di/`, `@HiltViewModel`, `@AndroidEntryPoint` |
| Corrutinas/Flow | DAOs `Flow`, `StateFlow` en ViewModels |
| Navigation Compose | `ui/navigation/CellFixRoot.kt` |
| PDF/CSV | `report/ReportGenerator.kt` |
| Gradle Kotlin DSL | `*.gradle.kts`, `libs.versions.toml` |
| Mínimo 8 requisitos funcionales | 14 requisitos RF-01 a RF-14 |
| Login con 2 roles y hash | RF-01 |
| ≥ 4 entidades relacionadas con FK | 8 entidades |
| CRUD + validaciones | Clientes, equipos, técnicos, reparaciones, ventas |
| Búsqueda y filtro por fecha | RF-10 y RF-11 |
| Dashboard con indicadores y gráfico | RF-09 |
| Reglas dominicanas | ITBIS 18 %, RD$, cédula/RNC |
| Tema propio, icono y modo oscuro | RF-13 |
| Respaldo exportar/importar | RF-12 |
| 3 reportes | RF-11: listado, período PDF+CSV, resumen gerencial |
| GitHub con 15+ commits, main/develop, Release v1.0.0 | `docs/06-guia-github.md` |
| README, docs, bitácora de IA, video y defensa | `README.md`, `docs/` |
