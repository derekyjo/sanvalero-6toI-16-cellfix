# AGENTS.md · Guía para agentes de IA

Este repositorio contiene **CellFix**, una app Android nativa en Kotlin para gestión de
ventas y reparaciones de celulares. Esta guía aplica a cualquier agente que modifique el
código.

## 1. Stack y versiones

- Kotlin 2.0.21
- Android Gradle Plugin 8.7.3
- Gradle 8.9 (wrapper incluido)
- Compose BOM 2024.10.01
- Material 3
- Room 2.6.1 con KSP
- Hilt 2.52 con KSP
- Navigation Compose 2.8.4
- `compileSdk` / `targetSdk` 35, `minSdk` 26
- JDK 17

No cambies versiones sin actualizar `gradle/libs.versions.toml` y probar la compilación.

## 2. Estructura

```text
app/src/main/java/com/sanvalero/cellfix/
├── data/
│   ├── local/entity/      Entidades Room
│   ├── local/dao/         DAOs con Flow y suspend
│   ├── local/AppDatabase.kt
│   ├── mapper/            Entidad ↔ dominio
│   ├── repository/        CellFixRepositoryImpl
│   └── session/           SessionManager
├── di/                    Módulos Hilt
├── domain/
│   ├── model/             Modelos y constantes
│   ├── repository/        Contrato CellFixRepository
│   └── usecase/           Login, validaciones, ITBIS, garantías
├── report/                PDF y CSV
├── ui/
│   ├── components/        Componentes Compose reutilizables
│   ├── navigation/        CellFixRoot + NavHost
│   ├── screens/           Una carpeta por módulo
│   ├── session/           SessionViewModel
│   └── theme/             Tema Material 3
├── util/                  Formatos, criptografía, respaldo
├── CellFixApp.kt
└── MainActivity.kt
```

## 3. Reglas de arquitectura

1. **La UI no accede a Room.** Siempre pasa por un ViewModel y por `CellFixRepository`.
2. **El dominio no importa Android.** Mantén `domain/` libre de `android.*`.
3. Los ViewModels exponen `StateFlow<UiState>` y no conocen `Context`.
4. Las pantallas Compose no contienen lógica de negocio; solo eventos y render.
5. Los DAOs devuelven `Flow` para lectura y `suspend` para escritura.
6. Las transacciones se hacen en el repositorio (`db.withTransaction`).
7. Usa Hilt para dependencias; no instancies base de datos ni repositorios manualmente.

## 4. Reglas de negocio que no se pueden romper

- ITBIS = **18 %** y redondeo a 2 decimales (`CalculadoraUseCase`).
- IMEI = **15 dígitos** y único.
- Cédula = **11 dígitos**; RNC = 9 u 11.
- Garantía = **30 días** desde la entrega.
- No duplicar garantía vigente para el mismo equipo/IMEI.
- Al pasar una reparación a `ENTREGADO` se guarda `fechaEntrega` y se crea la garantía.
- Una venta debe tener al menos un artículo válido.
- No se elimina un técnico con reparaciones (`RESTRICT`).
- Las contraseñas se guardan con PBKDF2 + sal; nunca en texto plano.

## 5. Convenciones de código

- Paquete base: `com.sanvalero.cellfix`.
- Nombres de clases y funciones en español, siguiendo el código existente.
- Kotlin idiomático, `val` por defecto, data classes para estado.
- Comentarios solo cuando aporten contexto de negocio.
- No uses APIs experimentales sin `@OptIn` y justificación.
- Mantén el estilo de tema y componentes existentes.
- Archivos nuevos en la carpeta del módulo correspondiente.

## 6. Comandos

En Windows:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest
.\gradlew.bat lintDebug
.\gradlew.bat installDebug
```

En macOS/Linux:

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew installDebug
```

Si falla por JDK, configura Gradle para usar JDK 17. Si falla por SDK, crea
`local.properties` con `sdk.dir=...`.

## 7. Pruebas

- Unitarias: `app/src/test/java/com/sanvalero/cellfix/domain/usecase/`.
- Agrega pruebas para toda regla de negocio nueva.
- No uses `android.*` en pruebas unitarias.
- Para cambios de UI, verifica manualmente en emulador o dispositivo.

## 8. Base de datos

- Versión actual: `1`.
- Si cambias el esquema, incrementa la versión y documenta la estrategia.
- Índices únicos: `usuarios.username`, `clientes.cedula`, `equipos.imei`.
- `garantias.reparacionId` es único.
- Journal `TRUNCATE` para que el respaldo sea un solo archivo.

## 9. Reportes y respaldo

- `ReportGenerator` usa `PdfDocument`; no agregues librerías PDF pesadas sin justificar.
- Los archivos se guardan en la carpeta privada y se comparten con `FileProvider`.
- `BackupManager` sobrescribe `cellfix.db`; avisa del reinicio tras importar.
- No escribas rutas absolutas ni credenciales.

## 10. Git

- Ramas: `main` (estable) y `develop` (integración).
- Commits en formato Conventional Commits: `feat:`, `fix:`, `docs:`, `test:`, `build:`,
  `chore:`.
- No subas `local.properties`, keystores, tokens ni archivos de build.
- El repositorio debe mantener al menos 15 commits y el tag `v1.0.0`.

## 11. Qué no hacer

- No reemplaces Room por una solución en memoria.
- No elimines el login ni los roles.
- No hardcodees contraseñas en texto plano.
- No cambies el cálculo de ITBIS sin actualizar pruebas y documentación.
- No borres documentación ni la bitácora de IA.
- No agregues dependencias innecesarias.
- No expongas claves ni tokens en el código.

## 12. Al terminar una tarea

1. Compila (`assembleDebug`).
2. Ejecuta pruebas (`testDebugUnitTest`).
3. Revisa que no haya secretos.
4. Actualiza la documentación si cambió el comportamiento.
5. Resume los cambios y los comandos ejecutados.
