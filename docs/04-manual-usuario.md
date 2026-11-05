# 04 · Manual de usuario

## 1. Instalación

1. Copia el APK `app-release.apk` al teléfono o instálalo con Android Studio.
2. Si Android lo solicita, permite la instalación de aplicaciones de origen desconocido
   para la app de archivos.
3. Abre **CellFix**.

## 2. Inicio de sesión

Al abrir la app verás la pantalla de bienvenida con el logo y el formulario.

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `admin123` | Administrador |
| `operador` | `operador123` | Operador |

Escribe las credenciales y pulsa **Entrar**. Si los datos son incorrectos verás un mensaje
sin que se indique cuál campo falló.

## 3. Panel general

El dashboard muestra:

- Cantidad de clientes y equipos.
- Total de reparaciones.
- Garantías activas.
- Ingresos acumulados por reparaciones entregadas y por ventas.
- Gráfico de barras con las reparaciones por estado.
- Accesos directos a cada módulo.

El administrador ve además **Técnicos** y **Reportes**. El botón de la esquina superior
derecha cierra la sesión.

## 4. Clientes

1. Pulsa el botón flotante **+**.
2. Escribe nombre, cédula, teléfono y correo.
3. Pulsa **Guardar**.

Validaciones:

- La cédula debe tener 11 dígitos.
- El teléfono debe tener al menos 10 dígitos.
- El correo es opcional, pero si se escribe debe tener formato válido.

Usa el buscador para filtrar por nombre, cédula o teléfono. Los iconos de lápiz y papelera
permiten editar y eliminar. Al eliminar un cliente también se eliminan sus equipos y el
historial asociado; la app lo advierte antes de continuar.

## 5. Equipos

1. Pulsa **+**.
2. Selecciona el cliente dueño.
3. Escribe marca, modelo, IMEI y color.
4. Pulsa **Guardar**.

El IMEI debe tener exactamente 15 dígitos y no puede repetirse. Es la clave con la que se
controlan las garantías.

## 6. Técnicos

Solo visible para el administrador.

1. Pulsa **+**.
2. Escribe nombre, especialidad y teléfono.
3. Deja el interruptor **Activo** si puede recibir órdenes.
4. Pulsa **Guardar**.

No se puede eliminar un técnico que tenga reparaciones asociadas.

## 7. Reparaciones

1. Pulsa **+**.
2. Selecciona el equipo y el técnico.
3. Describe la falla y, si ya la conoces, el diagnóstico.
4. Escribe costo de mano de obra y de repuestos.
5. La app muestra en vivo subtotal, ITBIS (18 %) y total.
6. Pulsa **Guardar**.

La orden se crea con estado **Recibido**. En cada tarjeta puedes usar el menú de tres puntos
para cambiar el estado:

`Recibido → En diagnóstico → En reparación → Listo para entrega → Entregado`

o **Cancelado**.

### Entrega y garantía automática

Al seleccionar **Entregado**, la app:

1. Guarda la fecha de entrega.
2. Crea una garantía de 30 días.
3. Muestra un mensaje de confirmación.

Si el equipo ya tiene una garantía vigente, esta no se duplica y se avisa al usuario.

## 8. Garantías

- Busca por IMEI, cliente o equipo.
- Cada tarjeta muestra equipo, IMEI, cliente, fecha de inicio, fecha de fin, días y estado
  **VIGENTE** o **VENCIDA**.
- La papelera elimina la garantía seleccionada, previa confirmación.

## 9. Ventas

1. Pulsa el botón de venta.
2. Selecciona el cliente.
3. Elige el método de pago: efectivo, tarjeta, transferencia o crédito.
4. Agrega uno o más artículos con descripción, cantidad y precio unitario.
5. Revisa subtotal, ITBIS y total.
6. Pulsa **Registrar venta**.

La venta y su detalle se guardan en una sola transacción. Puedes eliminar una venta desde
la papelera; el detalle se elimina en cascada.

## 10. Reportes

Solo visible para el administrador.

### Listado general de clientes (PDF)

Genera un PDF con todos los clientes y sus datos de contacto.

### Reparaciones por período (PDF y CSV)

1. Selecciona **Desde** y **Hasta**.
2. Pulsa **Generar PDF** o **Generar CSV**.
3. El archivo se comparte con el selector de Android.

El PDF incluye total del período. El CSV se puede abrir en Excel o Google Sheets.

### Resumen gerencial (PDF)

Incluye indicadores generales, ingresos, reparaciones por estado y equipos con más
órdenes.

### Ubicación de los archivos

- `Descargas/CellFix` (Android 10 o superior).
- Carpeta privada de la app: `Android/data/com.sanvalero.cellfix/files/reportes`.

## 11. Respaldo de la base de datos

- **Exportar**: crea una copia `cellfix_backup_AAAAMMDD_HHMMSS.db` y la comparte.
- **Importar**: abre el selector de archivos y elige un respaldo `.db`.
- Después de importar, **cierra y vuelve a abrir CellFix** para que Room cargue el archivo
  restaurado.

> Guarda los respaldos fuera del teléfono (correo, Drive o computadora) para no perderlos
> si se desinstala la app.

## 12. Preguntas frecuentes

**¿Necesito Internet?** No. Todo funciona localmente.

**¿Se pueden duplicar IMEI?** No. La base de datos tiene un índice único.

**¿Qué pasa si elimino un cliente?** Se eliminan sus equipos, reparaciones, garantías y
ventas por las claves foráneas en cascada.

**¿Cómo cambio la contraseña?** En esta versión los usuarios son de demostración y se
crean en la semilla. La gestión de usuarios queda como mejora futura.

**¿Por qué no aparece Reportes?** Porque la sesión es de operador. Inicia con `admin`.

**¿Dónde veo las garantías vencidas?** En el módulo Garantías, con la etiqueta **VENCIDA**.
