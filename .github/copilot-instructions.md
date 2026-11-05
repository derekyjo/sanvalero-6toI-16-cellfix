# Instrucciones para GitHub Copilot · CellFix

Este repositorio es una app Android nativa en Kotlin para ventas y reparaciones de
celulares.

## Contexto

- Paquete base: `com.sanvalero.cellfix`.
- Stack: Kotlin 2.0.21, Compose, Material 3, Room 2.6.1, Hilt 2.52, Navigation Compose,
  Corrutinas/Flow, Gradle Kotlin DSL.
- Arquitectura: Clean Architecture con tres capas (`data`, `domain`, `ui`) y MVVM.
- La interfaz no accede a DAOs; usa `CellFixRepository` a través de los ViewModels.

## Reglas al generar código

1. Sigue la estructura de carpetas existente.
2. Usa `@HiltViewModel` y `StateFlow` para el estado de pantalla.
3. Las entidades Room van en `data/local/entity`; los DAOs en `data/local/dao`.
4. Los modelos de dominio no deben importar `android.*`.
5. Mantén los nombres y mensajes en español.
6. No dupliques lógica de ITBIS, validaciones ni garantías: usa los casos de uso.
7. Añade pruebas unitarias para reglas de negocio.
8. No agregues dependencias sin actualizar `gradle/libs.versions.toml`.
9. No escribas contraseñas, tokens ni rutas absolutas.
10. Respeta `.gitignore`.

## Reglas de negocio

- ITBIS 18 % con redondeo a 2 decimales.
- IMEI de 15 dígitos y único.
- Cédula de 11 dígitos; RNC de 9 u 11.
- Garantía de 30 días al entregar una reparación.
- No duplicar garantía vigente por IMEI.
- Contraseñas con PBKDF2 + sal.
- Una venta requiere al menos un artículo válido.

## Comandos

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest
.\gradlew.bat lintDebug
```

Consulta `AGENTS.md` para más detalles.
