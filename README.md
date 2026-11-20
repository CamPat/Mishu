## Mishu 4.14 — Menú Dividir estable

La acción Dividir abre un diálogo independiente de la barra de selección. Se conserva el texto seleccionado incluso con guardado pendiente. Traducciones español/inglés.

## Mishu 4.13 — Menús de selección
- Dividir sustituye la barra principal por las opciones, sin superposición.
- Mover muestra únicamente la acción opuesta al campo seleccionado y la ejecuta directamente.
- Interfaz bilingüe.

# Mishu 4.5 — Android, Windows y Web

**Documentación:** `GUIA_DESARROLLO.md` · **Historial único:** `CHANGELOG.md`.

La versión Windows incluye las mismas correcciones del HTML que Android y web. La apertura externa admite `.json`.

# Mishu 🐱 — código fuente 1.2

Proyecto Android (WebView + SQLite) y Windows (pywebview + SQLite) de notas jerárquicas, **sin APK ni EXE compilados**.

## Funciones incorporadas

- Biblioteca con colecciones, carpetas, notas, papelera y moldes.
- Temas/subtemas plegables, cambio de jerarquía, orden, duplicación, edición, deshacer/rehacer estructural (hasta 35 estados).
- Búsqueda general y **Buscar relaciones** al seleccionar texto: título exacto, título parcial, menciones en contenido; previsualización flotante sin navegar fuera.
- Modo estudio con fragmentos ocultos; marcas de formato en texto plano; recursos multimedia externos por URL, incluida conversión de enlaces de Google Drive a previsualización cuando el proveedor lo permita.
- Editor de estilo con colores de fondos, entradas, títulos, subtítulos, fuente, tamaño, grosor, separación y patrones de líneas.
- Guardado local automático; SQLite con transacciones y hasta 12 instantáneas recientes en hosts Android y Windows; exportación/importación JSON; recuperación desde copias web y SQLite al iniciar.
- PDF mediante diálogo de impresión del navegador, con opción de conservar estilo; menú contextual y opciones de idioma.

## Instalación de desarrollo

Android: abrir el directorio raíz con Android Studio, sincronizar Gradle y ejecutar o compilar. GitHub Actions contiene un flujo de compilación debug. Para actualizaciones estables se requiere una clave de firma persistente.

Windows: `pip install pywebview`, luego `python windows/Mishu.py`. SQLite se guarda en `%LOCALAPPDATA%/Mishu/mishu.sqlite` en Windows.

Modo web: abrir `app/src/main/assets/index.html` (en navegador aislado usa localStorage y no SQLite).

## Límites y funciones aún no terminadas

Esta versión **no cumple todavía íntegramente el prompt maestro**. No tiene editor WYSIWYG verdadero: negrita/cursiva/subrayado se representan mediante marcas de texto. La traducción al inglés cubre etiquetas habituales pero no todas las pantallas ni mensajes. La exportación PDF usa impresión y no un generador PDF nativo. No existe un selector nativo de carpeta para exportaciones ni integración de exportación directa con Drive (puedes usar el selector de compartir del sistema). Los controles multimedia dependen de la posibilidad de incrustación de cada proveedor. Las operaciones de deshacer no cubren todas las ediciones de texto. La recuperación del estado local en la interfaz web usa localStorage, y la migración a SQLite en Android/Windows no está validada con bibliotecas grandes. No hay pruebas de interfaz o dispositivos reales. El proyecto no está compilado en APK/EXE.

**No utilices esta versión como única copia de datos importantes.** Exporta copias JSON periódicamente y conserva el proyecto anterior antes de actualizar.

## GitHub Actions: corrección de rutas

El workflow `.github/workflows/android.yml` localiza automáticamente el proyecto Gradle en la raíz (`app/`) o dentro de `Mishu/app/`, evitando el error `working directory .../android: No such file or directory`. Se han especificado paquetes SDK disponibles, sin `tools` obsoleto. Para subir este ZIP a un repositorio nuevo, coloca **el contenido de la carpeta Mishu** en la raíz del repositorio (no una carpeta Mishu adicional). El APK debug se publica como artifact `Mishu-Debug-APK` cuando la compilación tiene éxito. No se ha ejecutado la compilación en GitHub Actions desde este entorno.

## GitHub Actions (Android y Windows)
Sube **el contenido completo** de este ZIP a la raíz del repositorio `CamPat/Mishu` (no subas solamente el ZIP ni solamente `android.yml`). La raíz debe contener `app/`, `windows/`, `build.gradle`, `settings.gradle` y `.github/workflows/android.yml` y `windows.yml`. También se detecta una carpeta envolvente `Mishu/` si se subió así. Si GitHub sigue diciendo que no existe `/android`, está ejecutando un workflow antiguo: sustituye `.github/workflows/android.yml` y confirma el commit en la rama `main`.

En Actions ejecuta `Build Mishu Android APK` y `Build Mishu Windows EXE` por separado. Los artefactos se adjuntan solo si la compilación tiene éxito. No se ha probado la compilación en los runners de GitHub ni en dispositivos reales.


## Actualización 1.5 (interfaz)
- Entradas de texto ajustan su altura al contenido y al escribir.
- Reparado el botón de edición de título que mostraba código JavaScript.
- Menús de los tres puntos de temas anclados al botón, ajustados a los bordes de pantalla.
- Vista PDF conserva su diseño y ofrece «Exportar / guardar PDF» dentro de la vista, mediante el diálogo de impresión/guardar del sistema.
- No se han realizado pruebas en dispositivos físicos ni compilado binarios.


## Versión 1.6
- Selección de texto: barra contextual con Buscar relaciones y Ocultar en modo estudio, situada junto al campo seleccionado sin salirse de pantalla.
- En modo estudio: campos de solo lectura, controles de edición ocultos y bloqueo de operaciones de edición.
- Menús contextuales: anclados al botón, con ajuste automático horizontal y vertical.
- Los cambios no eliminan datos existentes.


## Actualización 1.7: cabecera y pantalla completa Android
- La cabecera de la interfaz muestra «¡Vamos a estudiar!» en español y «Let's study!» en inglés, cambiando al elegir idioma. El nombre Mishu y el icono de gato se conservan como identidad de la aplicación instalada, pero no en la cabecera interior.
- Android oculta la barra superior del sistema (hora, batería, señal) mediante FLAG_FULLSCREEN; mantiene la barra de navegación del sistema.
- Windows mantiene la barra de título propia del sistema operativo.
- No se han compilado APK/EXE ni realizado pruebas en dispositivos reales.


## Mishu 1.8 — Icono y autoría
- Icono Android rediseñado: gatito minimalista de mayor tamaño con fondo de color a sangre, sin márgenes blancos. Incluye recursos de icono adaptativo y versiones PNG para Android anteriores. El lanzador puede aplicar su propia máscara circular o cuadrada.
- Descripción de aplicación Android: «Mishu: biblioteca personal de estudio y notas plegables. Desarrollado por CamPat.»
- No se modificaron los datos guardados ni el identificador de aplicación.
- Para ver el nuevo icono, recompila e instala la actualización; algunos lanzadores mantienen temporalmente una copia en caché.

## Mishu 1.9 — Fuentes
- Fuentes adicionales, incluyendo Comic Sans MS (usa alternativa cursiva si no está instalada en el dispositivo).
- El selector conserva la fuente actual.
- Opción «Editor de estilo» sin icono de paleta.
\n\n## Mishu 2.1\n- Selección de texto: ocultar, negrita, cursiva, subrayar y resaltar en seis colores. Las marcas se almacenan como rangos, sin insertar {{ }} en el contenido.\n- Modo estudio: contenido de solo lectura y respuestas ocultas pulsables.\n- Vista PDF: botón Volver y exportar desde la vista.\n- Limitación: marcas que se solapan con texto editado se eliminan para evitar desplazamientos incorrectos.\n

## Mishu 2.2 — Biblioteca y fuentes
- Las colecciones y carpetas empiezan contraídas al abrir la biblioteca. Se expanden al pulsar su nombre o flecha y se contraen al volver a pulsarlo. El estado de expansión es temporal y no modifica ni elimina notas.
- El selector de fuentes utiliza una lista propia con cada nombre representado en su tipografía. En Android algunas fuentes no están instaladas; se usa una fuente alternativa del sistema.
- No se han compilado APK/EXE ni ejecutado pruebas en dispositivos físicos.


## Mishu 2.3 — PDF con estilos predeterminados
La vista PDF se abre con la configuración visual actual. Dentro de la vista, «Sin estilo» permite alternar a un documento blanco y negro sin modificar la nota ni sus preferencias. La exportación utiliza impresión del sistema.


## Mishu 2.4: orden y navegación secuencial
- Título de la nota destacado sobre los temas.
- Anterior/Siguiente dentro de la misma carpeta, usando el orden visible de la biblioteca.
- Al pulsar Siguiente en la última nota, se crea una nota nueva inmediatamente después de ella.
- Botones ↑/↓ para reordenar colecciones, carpetas y notas dentro de su grupo.
- El orden se conserva en el estado local y las exportaciones JSON.


## Mishu 4.6 — División y movimiento de selecciones
La barra de selección permite dividir en subtema o tema paralelo, elegir título/contenido y mover fragmentos entre ambos campos. Incluye traducciones al inglés.


## Mishu 4.7 — Sincronización manual de nube
Botón ☁ y menú de sincronización, GitHub (repositorio privado) y Dropbox mediante token OAuth. Carpetas como partidas independientes. Comparación de fecha `updated`, confirmación antes de sobrescribir, copia de recuperación local y mensajes español/inglés. El token no se guarda. Requiere conexión a Internet, credenciales válidas y acceso CORS del proveedor. No combina cambios simultáneos; relojes incorrectos pueden producir decisiones erróneas.


## Mishu 4.8 — Google Drive
Se agregó Google Drive al selector de sincronización manual. Requiere un token OAuth de acceso temporal con permisos Drive; no incluye inicio de sesión de Google integrado en Windows o Android. La API crea `Mishu/<carpeta>/mishu-backup.json` en Mi unidad. La autenticación, los permisos de la API y el acceso CORS deben configurarse y comprobarse en cada plataforma. No se almacenan tokens en las copias. La sincronización no fusiona cambios concurrentes.

## Google sign-in (4.9)
Create OAuth clients in Google Cloud, enable Google Drive API and configure the consent screen. For GitHub Pages register your exact origin (e.g. https://campat.github.io) as an authorized JavaScript origin for a Web application OAuth client. For Windows use a separate Desktop OAuth client ID and the system browser. Google login on Android requires a native SDK implementation and is not available in this build.

### Google Drive (preparación 4.10)
Consulta `CONFIGURAR_GOOGLE_DRIVE.md` antes de compilar. El inicio de sesión Android es experimental y requiere credenciales y validación en dispositivo. No se ha probado una sincronización real entre plataformas.


## Mishu 4.12 — Barra de selección
Se restauraron los botones Dividir y Mover a tema / mover a texto en la barra de selección para web, Android y Windows, con traducciones ya incluidas.


## Mishu 4.15 — Device-to-device sync
Removed Google Drive, Dropbox and GitHub sync UI. English is the default for new libraries; existing language settings remain unchanged. Four-digit pairing requires deploying the included temporary relay server (see SYNC_SETUP.md). Sender approval, receiving-side comparison, full replacement confirmation, and local recovery backup are included. Not tested with live cross-device deployment.


### 4.16 Sync configuration
Users no longer enter a server URL. The publisher must deploy the Cloudflare relay and set the GitHub Actions `MISHU_SYNC_URL` variable. See `SYNC_SETUP.md`.


## Mishu 4.17 — Direct device linking
GitHub Pages hosts the interface; WebRTC connects two open devices directly. Manual invitation/response exchange is required; a four-digit code cannot replace signaling without a server. The link is session-only, and Unlink closes the connection. No remote backup. Confirm full replacement before receiving; local recovery is preserved. English and Spanish UI. WebRTC connectivity depends on the network and platform support.
