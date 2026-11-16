# Mishu 3.3

- Importación/exportación desde la interfaz únicamente mediante archivos `.mishu`.
- Etiquetas múltiples por nota, entrada `#etiqueta`, reutilización de etiquetas existentes.
- Búsqueda global con filtro plegado por etiquetas (combinación de texto y etiquetas).
- Vista previa PDF: se sustituye `iframe.document.write` por `iframe.srcdoc` para evitar páginas blancas en Android WebView; se elimina CSS de edición incrustado en la vista previa.

Pruebas pendientes: comprobar la vista PDF y la impresión en dispositivos Android reales. Antes de actualizar, exportar copia `.mishu`.
