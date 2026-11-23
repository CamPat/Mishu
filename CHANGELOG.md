# Mishu 4.22 — Search, links, split view

- Global search across collections with tag filtering and highlighted results.
- Bidirectional note references and automatic backlinks.
- Side-by-side read-only note comparison.
- Local-only operation; no synchronization.

## 4.19 — Short-code device linking
- Six-digit pairing UI in English and Spanish; temporary WebRTC SDP signaling Worker.
- No remote library storage. Publisher must deploy signaling and set MISHU_SYNC_URL.
- Not production-ready: rate limiting, sender approval, and end-to-end tests remain pending.

## Mishu 4.14 — Menú Dividir estable

La acción Dividir abre un diálogo independiente de la barra de selección. Se conserva el texto seleccionado incluso con guardado pendiente. Traducciones español/inglés.

## Mishu 4.13 — Menús de selección
- Dividir sustituye la barra principal por las opciones, sin superposición.
- Mover muestra únicamente la acción opuesta al campo seleccionado y la ejecuta directamente.
- Interfaz bilingüe.

## 4.5 — Jerarquía y pegado
- Jerarquizar más: eleva un tema un nivel (izquierda).
- Jerarquizar menos: anida un tema bajo el anterior (derecha).
- Los subtemas se desplazan junto con su tema padre en ambas operaciones.
- Pegar texto: los temas raíz se insertan al mismo nivel, después del seleccionado; los prefijos > conservan subjerarquías.
- Cambios compartidos por web, Android y Windows.

# Historial de cambios — Mishu

Las notas de actualización anteriores se han reunido aquí. Este historial describe cambios de código, no pruebas en dispositivos reales.


---

## Versión 3 3

# Mishu 3.3

- Importación/exportación desde la interfaz únicamente mediante archivos `.mishu`.
- Etiquetas múltiples por nota, entrada `#etiqueta`, reutilización de etiquetas existentes.
- Búsqueda global con filtro plegado por etiquetas (combinación de texto y etiquetas).
- Vista previa PDF: se sustituye `iframe.document.write` por `iframe.srcdoc` para evitar páginas blancas en Android WebView; se elimina CSS de edición incrustado en la vista previa.

Pruebas pendientes: comprobar la vista PDF y la impresión en dispositivos Android reales. Antes de actualizar, exportar copia `.mishu`.


---

## Versión 3 4 TRADUCCIONES

Mishu 3.4 — English UI translations

Expanded English dictionary, translated dynamic dialogs, menus, labels, placeholders and selection toolbar; user-created note content, titles and tags remain unchanged. Spanish remains default.


---

## Versión 3 7

# Mishu 3.7

- Corrige el PDF en blanco: la variable interna `history` (pila de notas) ocultaba `window.history`; `history.pushState` fallaba antes de construir la vista previa. Ahora se usa `window.history` explícitamente.
- Los títulos de temas y subtemas usan textarea multilínea, con ajuste de línea y altura automática.
- Tarjetas más compactas, contenido de texto de altura variable.
- Mantiene formato .mishu y las demás funciones de 3.6.

Pruebas pendientes: compilar y validar en Android WebView real.


---

## Versión 3 8

# Mishu 3.8
- Pegar texto jerárquico desde el menú de tema/subtema, preservando contenido.
- Saludo ¡A organizar! / Let's organize!
- Donar: https://ko-fi.com/campat
- Distribución web estática, con selector de carpeta y guardado `autosave.json` en navegadores compatibles.
- Archivo de inventario de cadenas de interfaz.


---

## Versión 3 9

Mishu 3.9: exportación e importación de notas, carpetas, colecciones y copias de seguridad en .json. Guardado de carpeta web autosave.json. Se mantiene la estructura interna y los datos existentes.


---

## Versión 4 0

# Mishu 4.0 — Inglés corregido

Diccionario de interfaz revisado y ampliado; traducciones naturales de términos y mensajes, incluyendo botones con símbolos. Se preservan las notas del usuario. Basado en Mishu 3.9 JSON.


---

## Versión 4 2

Mishu 4.2: Corrige la traducción del botón # Etiquetas / # Tags en la vista de nota. Se actualizan web, Android y archivo JS de pruebas.


---

## Versión 4.3

- Traducciones de «Pegar texto» y «Resaltar» y corrección del guardado de contenido al editar una nota.


## Versión 4.4

- Windows: apertura directa de archivos `.json` desde la línea de comandos y la asociación del sistema (si se configura).
- Consolidación de notas de versiones en un único `CHANGELOG.md`.
- Unificación de la documentación en `GUIA_DESARROLLO.md`.
- HTML compartido entre Android, Windows y web, con las correcciones de la versión 4.3.


## Mishu 4.6 — División y movimiento de selecciones
La barra de selección permite dividir en subtema o tema paralelo, elegir título/contenido y mover fragmentos entre ambos campos. Incluye traducciones al inglés.


## Mishu 4.7 — Sincronización manual de nube
Botón ☁ y menú de sincronización, GitHub (repositorio privado) y Dropbox mediante token OAuth. Carpetas como partidas independientes. Comparación de fecha `updated`, confirmación antes de sobrescribir, copia de recuperación local y mensajes español/inglés. El token no se guarda. Requiere conexión a Internet, credenciales válidas y acceso CORS del proveedor. No combina cambios simultáneos; relojes incorrectos pueden producir decisiones erróneas.


## Mishu 4.8 — Google Drive
Se agregó Google Drive al selector de sincronización manual. Requiere un token OAuth de acceso temporal con permisos Drive; no incluye inicio de sesión de Google integrado en Windows o Android. La API crea `Mishu/<carpeta>/mishu-backup.json` en Mi unidad. La autenticación, los permisos de la API y el acceso CORS deben configurarse y comprobarse en cada plataforma. No se almacenan tokens en las copias. La sincronización no fusiona cambios concurrentes.

## 4.9 — Google OAuth
- Web: Google Identity Services popup, configurable web client ID.
- Windows: system browser with OAuth authorization code + PKCE + localhost callback, configurable Desktop client ID.
- Android: Google login button is not yet connected to a native SDK; manual token fallback is retained only in prior releases. Do not claim Android sign-in works.

## 4.10 — Preparación de autenticación Android (experimental)
- Flujo AppAuth OAuth para Android (autorización mediante navegador externo, PKCE y retorno a la app).
- Integración del token obtenido con la interfaz bilingüe de sincronización.
- Configuración de cliente y esquema Android mediante propiedades Gradle.
- Guía de configuración y plan de pruebas de sincronización cruzada.
- **No validado con Google real; no es lanzamiento listo para producción.**


## Mishu 4.12 — Barra de selección
Se restauraron los botones Dividir y Mover a tema / mover a texto en la barra de selección para web, Android y Windows, con traducciones ya incluidas.


## Mishu 4.15 — Device-to-device sync
Removed Google Drive, Dropbox and GitHub sync UI. English is the default for new libraries; existing language settings remain unchanged. Four-digit pairing requires deploying the included temporary relay server (see SYNC_SETUP.md). Sender approval, receiving-side comparison, full replacement confirmation, and local recovery backup are included. Not tested with live cross-device deployment.


## 4.16 — Managed pairing endpoint
- Removed relay URL field from the end-user interface.
- CI embeds one publisher-managed relay URL in web, Android and Windows builds.
- Added publisher deployment workflow and bilingual setup guide.
- Pairing remains inactive until a real Cloudflare Worker URL is configured and verified.


## Mishu 4.17 — Direct device linking
GitHub Pages hosts the interface; WebRTC connects two open devices directly. Manual invitation/response exchange is required; a four-digit code cannot replace signaling without a server. The link is session-only, and Unlink closes the connection. No remote backup. Confirm full replacement before receiving; local recovery is preserved. English and Spanish UI. WebRTC connectivity depends on the network and platform support.


Mishu 4.18: Improved direct-link wizard UI with clear steps, prominent copy button, collapsed technical SDP messages, mobile-responsive layout, and English/Spanish labels. No networking architecture changes.


## Mishu 4.20 — Offline only
Sincronización, vinculación entre dispositivos y botón de nube eliminados. Se mantienen las copias locales y la importación/exportación manual de bibliotecas.


Mishu 4.21: Mapa conceptual automático junto al botón PDF en cada nota. Vista offline de jerarquías con conexiones, colores y fuentes del estilo activo, zoom, impresión y traducción inglés/español.

## 4.23
- Traducciones EN/ES para selector de carpeta Mishu y estado de conexion, incluso al cambiar idioma.
\n## 4.24\n- Nueva nota: selección obligatoria de colección y carpeta, con interfaz bilingüe.\n- Mover nota: primero colección y después carpeta.\n- Android: preservar WebView y nota al girar la pantalla.\n- Eliminado modo de comparación de notas y sus controles.\n
## 4.25
- Tablas editables por tema, con encabezados, filas y columnas, guardado local y modo estudio.

## 4.26
- Selección de celdas y rangos (Mayús + clic), combinación y separación, colores de fondo en tablas.

## 4.27
- Multimedia enlazada HTTPS por celda (imagen, audio, video, PDF, enlaces) con vista integrada cuando el proveedor lo permite.
- Ancho de columnas ajustable mediante arrastre desde el encabezado, persistido en la biblioteca local.

## 4.28
- Varias imágenes, videos, audios, PDF y enlaces por celda.
- Gestor de multimedia por celda: añadir, quitar y cambiar orden.
- Compatible con celdas antiguas que contenían un solo recurso.

## 4.29
- Moldes conservan tablas, combinaciones, colores, tamaños y multimedia; regeneración de IDs de tablas al aplicar molde.
- Vista ampliada de tablas con búsqueda por filas.
- Búsqueda global indexa texto de celdas y referencias multimedia.

## 4.31 — Corrección de exportación PDF
- Android: impresión nativa mediante PrintManager y WebView dedicada, compatible con Guardar como PDF.
- Vista previa de notas y diagramas: botón Exportar/Guardar PDF utiliza impresión nativa en Android; en navegador conserva window.print().
- Se aplica A4 vertical a notas y orientación seleccionada a diagramas.

## 4.32 — PDF biblioteca y formatos
- Exportación por biblioteca completa, colección o carpeta, incluidas carpetas vacías y títulos jerárquicos.
- Orientación vertical/horizontal, tamaños A3/A4/A5/Carta/Legal/personalizado y modo página continua.
- Impresión aislada en navegador y tamaño personalizado nativo Android; tablas incluidas en PDF.
- Límite técnico del modo continuo: 5000 mm de alto; documentos más largos pueden paginarse.

## 4.33 — PDF en blanco y delimitador <
- Se elimina la impresión en iframe oculto; se imprime documento visible en nueva ventana y hay alternativa en la misma pantalla.
- Se imprimen los estilos de PDF en vez de heredar todos los estilos de navegación de Mishu.
- Al escribir < en el título de un tema/subtema, todo lo que sigue se traslada al campo de texto del mismo tema.
- El pegado jerárquico también acepta `> Subtema < descripción`.

## 4.36 — Copias JSON Android
- Exportar biblioteca completa, colección, carpeta o nota abre ACTION_CREATE_DOCUMENT en Android.
- Escribe JSON UTF-8 directamente al URI elegido por el usuario y muestra éxito/error.
- Navegador mantiene descarga con Blob y URL temporal.
- Sin permiso de almacenamiento requerido; no altera la biblioteca.
