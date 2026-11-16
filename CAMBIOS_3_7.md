# Mishu 3.7

- Corrige el PDF en blanco: la variable interna `history` (pila de notas) ocultaba `window.history`; `history.pushState` fallaba antes de construir la vista previa. Ahora se usa `window.history` explícitamente.
- Los títulos de temas y subtemas usan textarea multilínea, con ajuste de línea y altura automática.
- Tarjetas más compactas, contenido de texto de altura variable.
- Mantiene formato .mishu y las demás funciones de 3.6.

Pruebas pendientes: compilar y validar en Android WebView real.
