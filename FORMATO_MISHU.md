# Formato Mishu (.mishu) — versión 1

- Única extensión admitida por los selectores de importación: `.mishu`.
- Las notas, carpetas y colecciones usan el sobre `format: "mishu-item", version: 1`.
- Las copias de seguridad `.mishu` contienen el estado validado de la biblioteca (`schema: 1`).
- Las etiquetas se guardan como una lista opcional `tags: ["#anatomia", "#repaso"]` en cada nota.
- Las versiones futuras deberán leer los archivos `.mishu` de versión 1; se podrán introducir versiones nuevas con migraciones explícitas.
- La base de datos local no se borra ni se convierte al cambiar la extensión de intercambio.
