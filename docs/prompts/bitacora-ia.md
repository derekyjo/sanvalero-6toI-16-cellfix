# Bitácora de uso de inteligencia artificial

**Alumno:** Derek Yasunary Jiménez Oviedo
**Proyecto:** CellFix · ventas y reparaciones de celulares
**Curso:** 6to I · San Valero
**Periodo:** noviembre de 2026

Esta bitácora documenta cómo se utilizó la IA durante el desarrollo. La IA se empleó como
apoyo para estructurar el proyecto, generar borradores de código, revisar la arquitectura y
redactar documentación. Todas las decisiones y la validación final corresponden al alumno.

---

## Entrada 01 · Lectura de la asignación

**Fecha:** 2026-11-05
**Herramienta:** asistente de IA en el editor
**Prompt:**

> "Necesito el proyecto completo para mañana. Resume la asignación y dime qué requisitos
> son obligatorios para una app Android de ventas y reparaciones."

**Resultado:**

- Se identificaron los requisitos obligatorios: Kotlin, Compose, Material 3, Room, MVVM,
  Clean Architecture, Hilt, corrutinas/Flow, Navigation Compose, PDF/CSV, Gradle KTS,
  login con roles, 8 requisitos funcionales, 4 entidades relacionadas, dashboard, reglas
  dominicanas y respaldo.
- Se definió el alcance: app local, sin servidor, con datos semilla.

**Decisión del alumno:** mantener el alcance local y priorizar una demo funcional.

---

## Entrada 02 · Elección del tema

**Fecha:** 2026-11-05
**Prompt:**

> "Propón un tema de negocio dominicano que permita aplicar IMEI, ITBIS, cédula y
> garantías."

**Resultado:**

La IA propuso varias ideas: taller de celulares, ferretería, farmacia y renta de vehículos.
Se eligió **CellFix** porque el IMEI es un identificador natural único y permite una regla
de negocio clara: garantía controlada por IMEI.

**Decisión del alumno:** CellFix, con garantía automática de 30 días al entregar.

---

## Entrada 03 · Arquitectura

**Fecha:** 2026-11-05
**Prompt:**

> "Diseña la estructura de paquetes para Clean Architecture con tres capas, MVVM, Hilt y
> Room. Explica la regla de dependencia."

**Resultado:**

Se definieron las capas:

```text
data → implementa domain ← ui
```

- `data/local`: Room, entidades y DAOs.
- `domain`: modelos, contrato de repositorio y casos de uso.
- `ui`: pantallas, ViewModels, navegación y tema.
- `di`: módulos de Hilt.
- `report` y `util`: generación de PDF/CSV, formatos, criptografía y respaldo.

**Decisión del alumno:** un único contrato `CellFixRepository` para evitar que la UI toque
los DAOs.

---

## Entrada 04 · Modelo de datos

**Fecha:** 2026-11-05
**Prompt:**

> "Propón al menos cuatro entidades relacionadas con claves foráneas para un taller de
> celulares, incluyendo garantías y ventas."

**Resultado:**

Se diseñaron 8 entidades:

1. `usuarios`
2. `clientes`
3. `equipos`
4. `tecnicos`
5. `reparaciones`
6. `garantias`
7. `ventas`
8. `detalle_venta`

Se añadieron índices únicos en `username`, `cedula` e `imei`, y una FK única en
`garantias.reparacionId`.

**Decisión del alumno:** usar `CASCADE` para las relaciones del cliente y `RESTRICT` para
proteger el historial de técnicos.

---

## Entrada 05 · Seguridad del login

**Fecha:** 2026-11-05
**Prompt:**

> "¿Cómo guardo contraseñas en Android sin texto plano y sin dependencias externas?"

**Resultado:**

La IA propuso PBKDF2 con `SecretKeyFactory`, sal aleatoria de 16 bytes, 60 000 iteraciones
y SHA-256. Se implementó en `CryptoUtils` y se usó en la semilla y en `LoginUseCase`.

**Decisión del alumno:** no guardar nunca la contraseña en claro y comparar hashes.

---

## Entrada 06 · Regla de garantía por IMEI

**Fecha:** 2026-11-05
**Prompt:**

> "Implementa la regla: al entregar una reparación se crea una garantía de 30 días, pero
> no debe duplicarse si el mismo equipo ya tiene una vigente."

**Resultado:**

Se creó `GarantiaUseCase` con estas validaciones:

1. La reparación existe.
2. El equipo existe.
3. El IMEI tiene 15 dígitos.
4. No hay otra garantía vigente para la reparación.

`ReparacionViewModel.cambiarEstado` invoca el caso de uso cuando el estado es `ENTREGADO`
y guarda `fechaEntrega`.

**Decisión del alumno:** mostrar el resultado al usuario con un `Snackbar` y no bloquear la
entrega si la garantía ya existía.

---

## Entrada 07 · ITBIS y formatos dominicanos

**Fecha:** 2026-11-05
**Prompt:**

> "Centraliza el cálculo de ITBIS 18 %, el redondeo a dos decimales y el formato RD$."

**Resultado:**

Se creó `CalculadoraUseCase` con `calcularReparacion` y `calcularVenta`, y `Formato` con
`moneda`, `fecha` y `selloArchivo`. Los totales se recalculan en el ViewModel antes de
guardar para evitar inconsistencias.

**Decisión del alumno:** usar `Locale.US` para que el separador decimal sea estable en
todos los dispositivos.

---

## Entrada 08 · Reportes PDF y CSV

**Fecha:** 2026-11-05
**Prompt:**

> "Genera tres reportes obligatorios: listado general, reporte por período en PDF y CSV, y
> resumen gerencial. Deben poder compartirse."

**Resultado:**

Se implementó `ReportGenerator` con `android.graphics.pdf.PdfDocument`:

- Encabezado, pie y salto de página automático.
- Guardado en carpeta privada.
- Copia a `Descargas/CellFix` con MediaStore en Android 10+.
- Compartición con `FileProvider`.
- CSV con escape de comas y comillas.

**Decisión del alumno:** generar los reportes en `Dispatchers.IO` para no bloquear la UI.

---

## Entrada 09 · Respaldo de base de datos

**Fecha:** 2026-11-05
**Prompt:**

> "¿Cómo exporto e importo la base de datos Room desde la propia app?"

**Resultado:**

Se implementó `BackupManager`:

- Exportar: copia `cellfix.db` a una carpeta privada y la comparte.
- Importar: usa `ActivityResultContracts.OpenDocument` y sobrescribe el archivo.
- Se eliminaron restos `-wal` y `-shm`.

**Decisión del alumno:** usar journal `TRUNCATE` para facilitar el respaldo en un solo
archivo y avisar que hay que reiniciar la app tras importar.

---

## Entrada 10 · Revisión de compilación

**Fecha:** 2026-11-05
**Prompt:**

> "Revisa el código en busca de errores de compilación típicos de Compose, Hilt y Room."

**Resultado:**

La IA detectó y corrigió:

- Un `Result<Boolean>` que debía ser `Result<Unit>` en `BackupManager`.
- Un import faltante de `Box` en la pantalla de reparaciones.
- El uso de `Divider` sustituido por `HorizontalDivider`.
- La necesidad de reiniciar el estado de éxito del login para evitar un inicio de sesión
  automático después de cerrar sesión.

**Decisión del alumno:** revisar manualmente cada corrección y mantener el estilo del
proyecto.

---

## Entrada 11 · Documentación y GitHub

**Fecha:** 2026-11-05
**Prompt:**

> "Genera README, documentación de arquitectura, modelo de datos, manual, pruebas, guía de
> GitHub y bitácora de IA. Incluye el usuario derekyjo y el repositorio
> sanvalero-6toI-16-cellfix."

**Resultado:**

Se generaron los archivos de `docs/`, `README.md`, `AGENTS.md`, `CLAUDE.md`,
`.github/copilot-instructions.md` y la configuración de VS Code. También se documentó el
procedimiento para crear 15 commits, las ramas `main` y `develop`, el tag `v1.0.0` y la
Release con el APK.

**Decisión del alumno:** publicar el repositorio y adjuntar el APK compilado en su laptop.

---

## Resumen de aportes de la IA

| Área | Aporte | Validación del alumno |
|---|---|---|
| Requisitos | Resumen y checklist | Revisión contra la asignación |
| Arquitectura | Estructura de capas y paquetes | Ajuste a las convenciones del curso |
| Código | Borradores de entidades, DAOs, ViewModels y pantallas | Lectura y corrección de errores |
| Reglas de negocio | ITBIS, IMEI, garantías | Pruebas manuales |
| Documentación | README, manual, guía de GitHub | Revisión de datos personales y enlaces |
| Calidad | Detección de errores de compilación | Corrección y nueva revisión |

## Declaración

El alumno comprende el funcionamiento del código entregado y asume la responsabilidad de
explicarlo en la defensa oral. La IA se usó como herramienta de apoyo, no como sustituto
del aprendizaje.
