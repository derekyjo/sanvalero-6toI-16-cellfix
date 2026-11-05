# 02 · Arquitectura y decisiones técnicas

## 1. Visión general

CellFix sigue **Clean Architecture** con tres capas y **MVVM** en la presentación. La
regla de dependencia es estricta: la interfaz depende del dominio y el dominio no conoce
Android; la capa de datos implementa los contratos del dominio.

```
┌─────────────────────────────────────────────────────────────┐
│                         UI (Compose)                        │
│  Screens ── StateFlow ── ViewModels ── Hilt                  │
└───────────────────────────┬─────────────────────────────────┘
                            │ CellFixRepository (interfaz)
┌───────────────────────────▼─────────────────────────────────┐
│                        DOMAIN                               │
│  Modelos · Casos de uso · Validaciones · Reglas de negocio  │
└───────────────────────────▲─────────────────────────────────┘
                            │ implementa
┌───────────────────────────┴─────────────────────────────────┐
│                          DATA                               │
│  Room (entidades/DAO) · Repositorio · Mappers · Sesión      │
└─────────────────────────────────────────────────────────────┘
```

## 2. Capa de datos

- **Room** como fuente de verdad local.
- **8 entidades**: usuarios, clientes, equipos, técnicos, reparaciones, garantías, ventas
  y detalle de venta.
- **DAOs** exponen `Flow` para listas reactivas y funciones `suspend` para escrituras.
- **Mappers** convierten entidades (`*Entity`) en modelos de dominio y viceversa.
- **`CellFixRepositoryImpl`** implementa el contrato `CellFixRepository` y centraliza las
  transacciones (`crearVenta`, semilla inicial).
- **`SessionManager`** guarda el usuario autenticado en memoria como `StateFlow`.

### Decisiones

| Decisión | Motivo |
|---|---|
| `setJournalMode(TRUNCATE)` | Facilita exportar un único archivo `.db` sin WAL |
| `fallbackToDestructiveMigration` | Proyecto académico con versión 1; evita fallos al cambiar el esquema |
| Índices únicos en `username`, `cedula`, `imei` | Evita duplicados a nivel de base de datos |
| `ForeignKey.CASCADE` en relaciones de negocio | Al eliminar un cliente se eliminan sus equipos y reparaciones |
| `ForeignKey.RESTRICT` en reparaciones → técnicos | Protege el historial: no se borra un técnico con órdenes |

## 3. Capa de dominio

No tiene dependencias de Android salvo Kotlin y corrutinas.

- **Modelos** (`Cliente`, `Equipo`, `Reparacion`, `Garantia`, `Venta`, etc.).
- **`CellFixRepository`**: contrato único de datos.
- **Casos de uso**:
  - `LoginUseCase`: valida credenciales y devuelve `Result<Usuario>`.
  - `ValidadorUseCase`: cédula, RNC, teléfono, IMEI y correo.
  - `CalculadoraUseCase`: ITBIS 18 %, subtotal y total redondeados a 2 decimales.
  - `GarantiaUseCase`: regla de garantía por IMEI y bloqueo de duplicados vigentes.

## 4. Capa de presentación

- **Una Activity** (`MainActivity`) con `setContent` y `CellFixTheme`.
- **Navigation Compose** con rutas: `dashboard`, `clientes`, `equipos`, `tecnicos`,
  `reparaciones`, `garantias`, `ventas`, `reportes`.
- **ViewModels Hilt** (`@HiltViewModel`) que exponen `StateFlow<UiState>`.
- **Pantallas Compose** sin lógica de negocio; reciben eventos y estados.
- **Componentes reutilizables**: barra superior, tarjetas de indicador, búsqueda,
  selector de opciones, selector de fecha, chip de estado y diálogo de confirmación.

### Gestión de estado

1. La pantalla recoge el evento del usuario.
2. El ViewModel valida y llama al caso de uso o al repositorio.
3. Room emite un nuevo `Flow`.
4. El ViewModel transforma el flujo (`combine`, `map`) y actualiza el `StateFlow`.
5. Compose recompone la pantalla.

### Búsqueda

La búsqueda se hace en memoria dentro de cada ViewModel con `combine(flujo, consulta)`.
Esto mantiene la UI inmediata y evita APIs experimentales de Room. Los DAOs también
incluyen consultas `LIKE` como respaldo.

## 5. Inyección de dependencias

- `DatabaseModule`: construye `AppDatabase` y provee cada DAO.
- `RepositoryModule`: enlaza `CellFixRepositoryImpl` con `CellFixRepository`.
- `@HiltAndroidApp` en `CellFixApp`, `@AndroidEntryPoint` en `MainActivity`.
- `@HiltViewModel` en todos los ViewModels.

## 6. Seguridad

- Contraseñas con **PBKDF2WithHmacSHA256**, 60 000 iteraciones, sal de 16 bytes y clave de
  256 bits.
- La contraseña en claro nunca se escribe en la base de datos ni en logs.
- El login compara el hash calculado con el almacenado.
- `FileProvider` con autoridad `${applicationId}.fileprovider` para compartir reportes y
  respaldos sin exponer rutas internas.

## 7. Reportes

`ReportGenerator` usa `android.graphics.pdf.PdfDocument`:

- Encabezado con marca, fecha de emisión y título.
- Saltos de página automáticos a los 790 px.
- Pie con numeración.
- Guardado en carpeta privada y copia a `Descargas/CellFix` en Android 10+.
- Compartición con `Intent.ACTION_SEND` y `FileProvider`.

CSV con encabezado, escape de comas y comillas, y codificación UTF-8.

## 8. Respaldo

`BackupManager` copia `cellfix.db` a
`Android/data/com.sanvalero.cellfix/files/backups/` y lo comparte. La importación usa
`ActivityResultContracts.OpenDocument` y sobrescribe el archivo de base de datos. Como
Room mantiene la conexión abierta, se indica al usuario reiniciar la app después de
importar.

## 9. Manejo de errores

- Validaciones de formulario antes de tocar la base de datos.
- `runCatching` alrededor de operaciones de escritura y reportes.
- Mensajes claros en `Snackbar`.
- Confirmación antes de eliminar registros.
- Las violaciones de claves foráneas se traducen a mensajes entendibles.

## 10. Riesgos y mitigaciones

| Riesgo | Mitigación |
|---|---|
| Importar respaldo con la app abierta | Aviso de reinicio y cierre lógico de la sesión de datos |
| Reportes con muchas filas | Generación en `Dispatchers.IO` y salto de página |
| Permisos de almacenamiento en Android 13+ | Se usa carpeta propia + MediaStore, sin pedir permisos peligrosos |
| Pérdida de datos | Exportación manual del archivo SQLite |
| Cambios de esquema | Versión de BD y migración destructiva documentada |
