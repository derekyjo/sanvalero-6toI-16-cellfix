# CLAUDE.md

Las instrucciones para agentes de IA de este repositorio están centralizadas en
[`AGENTS.md`](AGENTS.md).

Resumen rápido:

- Proyecto: **CellFix**, app Android Kotlin con Compose, Room, Hilt y MVVM.
- Paquete: `com.sanvalero.cellfix`.
- Arquitectura: `ui → domain ← data`; la UI nunca accede a Room.
- Reglas críticas: ITBIS 18 %, IMEI de 15 dígitos único, garantía de 30 días por IMEI.
- Compilar: `.\gradlew.bat assembleDebug` (Windows) o `./gradlew assembleDebug`.
- Probar: `.\gradlew.bat testDebugUnitTest`.
- No subir secretos, `local.properties` ni keystores.

Lee `AGENTS.md` completo antes de modificar el código.
