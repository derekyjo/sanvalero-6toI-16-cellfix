# 05 · Pruebas y despliegue

## 1. Matriz de pruebas funcionales

| ID | Caso | Pasos | Resultado esperado |
|---|---|---|---|
| P-01 | Login correcto | `admin` / `admin123` | Entra al dashboard con módulos de administrador |
| P-02 | Login incorrecto | `admin` / `123` | Mensaje "Usuario o contraseña incorrectos" |
| P-03 | Login de operador | `operador` / `operador123` | Entra sin Técnicos ni Reportes |
| P-04 | Crear cliente válido | Nombre, cédula de 11 dígitos, teléfono | Cliente aparece en la lista |
| P-05 | Cédula inválida | Cédula de 8 dígitos | No guarda y muestra error |
| P-06 | Cédula duplicada | Repetir cédula existente | Room rechaza por índice único |
| P-07 | Crear equipo válido | Cliente + IMEI de 15 dígitos | Equipo aparece con dueño |
| P-08 | IMEI inválido | IMEI de 10 dígitos | No guarda y muestra error |
| P-09 | IMEI duplicado | Repetir IMEI existente | No guarda, mensaje de duplicado |
| P-10 | Crear técnico | Nombre, especialidad, teléfono | Técnico disponible para reparaciones |
| P-11 | Eliminar técnico con órdenes | Papelera sobre técnico usado | Error controlado por FK `RESTRICT` |
| P-12 | Crear reparación | Equipo, técnico, falla y costos | Total con ITBIS 18 % correcto |
| P-13 | Cambiar estados | Menú → En reparación → Listo | Chip de estado actualizado |
| P-14 | Entregar reparación | Menú → Entregado | Fecha de entrega + garantía de 30 días |
| P-15 | Garantía duplicada | Entregar dos veces el mismo equipo | No crea otra garantía vigente |
| P-16 | Buscar garantía por IMEI | Escribir parte del IMEI | Lista filtrada correctamente |
| P-17 | Garantía vencida | Garantía con fecha fin pasada | Etiqueta VENCIDA en rojo |
| P-18 | Venta con ITBIS | 2 artículos, efectivo | Subtotal + 18 % = total |
| P-19 | Venta sin artículos | Registrar sin descripción | No guarda, mensaje de validación |
| P-20 | Eliminar venta | Papelera + confirmar | Venta y detalle desaparecen |
| P-21 | Buscar clientes | Texto parcial | Filtra por nombre, cédula o teléfono |
| P-22 | Reporte de clientes | Generar PDF | PDF válido con lista de clientes |
| P-23 | Reporte por período | Rango de fechas + PDF | Solo reparaciones del rango |
| P-24 | Reporte CSV | Rango de fechas + CSV | Archivo abre en Excel |
| P-25 | Resumen gerencial | Generar PDF | Indicadores y totales correctos |
| P-26 | Exportar respaldo | Exportar | Archivo `.db` compartido |
| P-27 | Importar respaldo | Elegir `.db` | Datos restaurados al reiniciar |
| P-28 | Modo oscuro | Activar tema oscuro del sistema | Colores y contraste correctos |
| P-29 | Persistencia | Cerrar y abrir la app | Datos conservados |
| P-30 | Navegación | Abrir y volver de cada módulo | Sin pantallas en blanco ni crashes |

## 2. Pruebas automatizadas

El proyecto incluye pruebas unitarias de la lógica de dominio:

```text
app/src/test/java/com/sanvalero/cellfix/
├── domain/usecase/CalculadoraUseCaseTest.kt
└── domain/usecase/ValidadorUseCaseTest.kt
```

Ejecutar:

```powershell
.\gradlew.bat testDebugUnitTest
```

Resultado esperado: `BUILD SUCCESSFUL` y reporte en
`app/build/reports/tests/testDebugUnitTest/index.html`.

## 3. Compilación

### Requisitos exactos

| Herramienta | Versión |
|---|---|
| JDK | 17 |
| Gradle | 8.9 (wrapper incluido) |
| Android Gradle Plugin | 8.7.3 |
| Kotlin | 2.0.21 |
| KSP | 2.0.21-1.0.28 |
| Compile SDK | 35 |
| Build Tools | 35.0.0 |
| Android mínimo | 8.0 (API 26) |

### Android Studio

1. `File → Open` y selecciona la carpeta del proyecto.
2. Espera a que Gradle sincronice.
3. `Build → Clean Project` y luego `Build → Rebuild Project`.
4. Conecta el teléfono y pulsa **Run**.

### Línea de comandos

```powershell
.\gradlew.bat clean
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest
```

APK generado:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 4. Instalación en dispositivo real

1. En el teléfono: `Ajustes → Acerca del teléfono → Número de compilación` (7 toques).
2. Activar `Opciones de desarrollador → Depuración USB`.
3. Conectar por USB y aceptar la huella RSA.
4. Verificar:

```powershell
adb devices
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

También puedes copiar el APK al teléfono y abrirlo desde el administrador de archivos.

## 5. APK de release

Para una entrega formal se recomienda un APK firmado:

1. En Android Studio: `Build → Generate Signed Bundle / APK`.
2. Elegir **APK**.
3. Crear o seleccionar un keystore (guardar la contraseña).
4. Variante **release**.
5. Copiar el APK a `docs/evidencias/` o adjuntarlo a la Release de GitHub.

> No subas el keystore ni las contraseñas al repositorio. Están excluidos en `.gitignore`.

## 6. Evidencias para la entrega

Capturas recomendadas:

1. Login.
2. Dashboard con indicadores y gráfico.
3. Lista y formulario de clientes.
4. Equipos con IMEI.
5. Reparaciones con estados.
6. Garantía generada al entregar.
7. Venta con ITBIS.
8. Reporte PDF abierto.
9. Reporte CSV.
10. Respaldo exportado.

Guardar en `docs/evidencias/` con nombres como `01-login.png`, `02-dashboard.png`, etc.

## 7. Video de 2 a 3 minutos

Guion sugerido:

1. **0:00–0:15** Presentación: nombre, curso, nombre del proyecto.
2. **0:15–0:30** Login con `admin` y explicación de roles.
3. **0:30–0:55** Dashboard e indicadores.
4. **0:55–1:20** Crear cliente y equipo con IMEI.
5. **1:20–1:50** Crear reparación, mostrar ITBIS y cambiar a Entregado.
6. **1:50–2:10** Ver la garantía creada por IMEI.
7. **2:10–2:30** Registrar venta con ITBIS.
8. **2:30–2:50** Generar PDF y CSV.
9. **2:50–3:00** Mostrar repositorio GitHub y despedida.

## 8. Defensa oral

Puntos que debes poder explicar:

- Qué es Clean Architecture y por qué la UI no accede a Room.
- Cómo funciona el flujo `UI → ViewModel → UseCase → Repository → DAO → Room`.
- Por qué se usa `StateFlow` y `Flow`.
- Cómo se protege la contraseña (PBKDF2 + sal).
- Cómo se garantiza la unicidad del IMEI.
- Cómo se crea la garantía al entregar y cómo se evita duplicarla.
- Cómo se calcula el ITBIS y por qué se redondea.
- Cómo se genera un PDF con `PdfDocument`.
- Cómo funciona el respaldo SQLite.

## 9. Solución de problemas

| Problema | Causa probable | Solución |
|---|---|---|
| Gradle pide JDK 17 | Android Studio usa un JDK antiguo | `Settings → Build Tools → Gradle → Gradle JDK → 17` |
| `SDK location not found` | Falta `local.properties` | Crear `local.properties` con `sdk.dir=...` |
| No compila Hilt | KSP o Hilt desactualizado | Verificar versiones del catálogo |
| No se ve Reportes | Sesión de operador | Entrar como `admin` |
| No se comparte el PDF | Falta app de destino | Instalar Drive, Gmail o un visor de PDF |
| Tras importar no cambian los datos | Room mantiene la conexión | Cerrar y reabrir la app |
| El emulador no tiene archivos | Ruta distinta | Revisar `Android/data/com.sanvalero.cellfix/files` |
