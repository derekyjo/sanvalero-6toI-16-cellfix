# 06 · Guía para publicar en GitHub

Repositorio objetivo:

```text
https://github.com/derekyjo/sanvalero-6toI-16-cellfix
```

> El proyecto ya está preparado para Git: tiene `.gitignore`, README, documentación y una
> estructura de carpetas limpia.

## 1. Instalar Git (si no lo tienes)

### Opción A · Instalador

1. Descarga Git para Windows desde <https://git-scm.com/download/win>.
2. Instala con las opciones por defecto.
3. Abre una terminal nueva y comprueba:

```powershell
git --version
```

### Opción B · GitHub Desktop

1. Descarga <https://desktop.github.com/>.
2. Inicia sesión con la cuenta `derekyjo`.
3. `File → Add local repository` y selecciona la carpeta del proyecto.

### Opción C · Git portable (sin permisos de administrador)

Descomprime MinGit o PortableGit en una carpeta y agrega `...\cmd` al `PATH`. También puedes
usar la ruta completa a `git.exe`.

## 2. Configurar identidad

```powershell
git config --global user.name "Derek Yasunary Jiménez Oviedo"
git config --global user.email "TU_CORREO@ejemplo.com"
```

## 3. Crear el repositorio local

Desde la carpeta `sanvalero-6toI-16-cellfix`:

```powershell
git init
git branch -M main
git add .
git commit -m "chore: estructura inicial del proyecto CellFix"
```

Si la carpeta ya tiene un repositorio creado, omite `git init`.

## 4. Crear el repositorio remoto

### Desde la web

1. Entra a <https://github.com/new>.
2. Nombre: `sanvalero-6toI-16-cellfix`.
3. Visibilidad: público (recomendado para la entrega).
4. **No** marques "Add a README" ni ".gitignore" porque ya existen.
5. Pulsa **Create repository**.

### Con GitHub CLI

```powershell
gh auth login
gh repo create derekyjo/sanvalero-6toI-16-cellfix --public --source=. --remote=origin --push
```

## 5. Conectar y subir

```powershell
git remote add origin https://github.com/derekyjo/sanvalero-6toI-16-cellfix.git
git push -u origin main
```

Si ya existe `origin`:

```powershell
git remote set-url origin https://github.com/derekyjo/sanvalero-6toI-16-cellfix.git
git push -u origin main
```

### Autenticación con token (PAT)

GitHub no acepta contraseña normal por HTTPS. Crea un **Personal Access Token**:

1. GitHub → `Settings → Developer settings → Personal access tokens → Tokens (classic)`.
2. `Generate new token (classic)`.
3. Scope mínimo: `repo`.
4. Copia el token (solo se muestra una vez).
5. Al hacer `git push`, usa:
   - Usuario: `derekyjo`
   - Contraseña: el token

Para no escribirlo cada vez:

```powershell
git config --global credential.helper manager
```

> Nunca escribas el token dentro del código, del README ni de un archivo del repositorio.

## 6. Ramas main y develop

```powershell
git checkout -b develop
git push -u origin develop
git checkout main
```

Flujo recomendado:

```text
develop → commits de avance → Pull Request → main → Release
```

## 7. Mínimo 15 commits

La entrega exige al menos 15 commits. Si el repositorio se creó de una sola vez, puedes
hacer commits temáticos con `git add` selectivo:

```powershell
git add settings.gradle.kts build.gradle.kts gradle.properties
git commit -m "build: configuracion de Gradle y catalogo de versiones"

git add gradle/ gradlew gradlew.bat
git commit -m "build: wrapper de Gradle 8.9"

git add app/build.gradle.kts app/proguard-rules.pro
git commit -m "build: configuracion del modulo app"

git add app/src/main/AndroidManifest.xml app/src/main/res
git commit -m "feat: manifiesto, recursos, tema e icono"

git add app/src/main/java/com/sanvalero/cellfix/domain
git commit -m "feat: modelos y contratos de dominio"

git add app/src/main/java/com/sanvalero/cellfix/data/local/entity
git commit -m "feat: entidades Room con claves foraneas"

git add app/src/main/java/com/sanvalero/cellfix/data/local/dao
git commit -m "feat: DAOs con Flow y consultas"

git add app/src/main/java/com/sanvalero/cellfix/data/local/AppDatabase.kt app/src/main/java/com/sanvalero/cellfix/di
git commit -m "feat: base de datos Room y modulos Hilt"

git add app/src/main/java/com/sanvalero/cellfix/data/mapper app/src/main/java/com/sanvalero/cellfix/data/repository
git commit -m "feat: repositorio y mappers"

git add app/src/main/java/com/sanvalero/cellfix/domain/usecase
git commit -m "feat: casos de uso de login, ITBIS y garantias"

git add app/src/main/java/com/sanvalero/cellfix/util app/src/main/java/com/sanvalero/cellfix/report
git commit -m "feat: formatos, respaldo y generacion de PDF/CSV"

git add app/src/main/java/com/sanvalero/cellfix/ui/theme app/src/main/java/com/sanvalero/cellfix/ui/components
git commit -m "feat: tema Material 3 y componentes reutilizables"

git add app/src/main/java/com/sanvalero/cellfix/ui/screens
git commit -m "feat: pantallas y ViewModels de todos los modulos"

git add app/src/main/java/com/sanvalero/cellfix/ui/navigation app/src/main/java/com/sanvalero/cellfix/MainActivity.kt app/src/main/java/com/sanvalero/cellfix/CellFixApp.kt
git commit -m "feat: navegacion, sesion y arranque Hilt"

git add app/src/test
git commit -m "test: pruebas unitarias de calculadora y validaciones"

git add README.md docs AGENTS.md CLAUDE.md .github .vscode
git commit -m "docs: README, arquitectura, manual y bitacora de IA"

git push origin main
git push origin develop
```

## 8. Release v1.0.0

### Desde la web

1. GitHub → `Releases → Draft a new release`.
2. Tag: `v1.0.0`.
3. Target: `main`.
4. Título: `CellFix v1.0.0`.
5. Descripción: resumen de funcionalidades.
6. Adjuntar el APK `app-release.apk` o `app-debug.apk`.
7. Pulsar **Publish release**.

### Con GitHub CLI

```powershell
git tag -a v1.0.0 -m "CellFix v1.0.0"
git push origin v1.0.0
gh release create v1.0.0 app\build\outputs\apk\debug\app-debug.apk --title "CellFix v1.0.0" --notes "Primera version estable de CellFix."
```

## 9. Verificación final del repositorio

- [ ] El repositorio es público.
- [ ] La rama `main` contiene el proyecto completo.
- [ ] Existe la rama `develop`.
- [ ] Hay 15 commits o más (`git log --oneline | wc -l`).
- [ ] Existe el tag `v1.0.0`.
- [ ] La Release incluye el APK.
- [ ] El README muestra el enlace correcto.
- [ ] `docs/` está completo.
- [ ] La bitácora de IA está incluida.
- [ ] No hay archivos `local.properties`, keystores ni tokens.

## 10. Cómo puede subirlo el asistente

El asistente puede ejecutar los comandos de Git si en la máquina están disponibles:

1. **Git instalado** y accesible en el `PATH`.
2. Un **token de GitHub** con permiso `repo` (no la contraseña).
3. El token se usa solo en la sesión de Git, nunca se guarda en el proyecto.

Comandos que ejecutaría:

```powershell
git init
git branch -M main
git add .
git commit -m "feat: proyecto CellFix completo"
git remote add origin https://github.com/derekyjo/sanvalero-6toI-16-cellfix.git
git push https://derekyjo:TOKEN@github.com/derekyjo/sanvalero-6toI-16-cellfix.git main
```

> Si compartes un token, cancélalo al terminar en
> `Settings → Developer settings → Personal access tokens`.

## 11. Enlaces para la entrega

Copia estos enlaces en la plataforma del centro:

```text
Repositorio: https://github.com/derekyjo/sanvalero-6toI-16-cellfix
Release:     https://github.com/derekyjo/sanvalero-6toI-16-cellfix/releases/tag/v1.0.0
Video:       (pegar enlace de YouTube/Drive)
```
