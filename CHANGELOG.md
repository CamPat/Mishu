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
