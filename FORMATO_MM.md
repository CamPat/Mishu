# Formato .mm de Mishu

Los archivos `.mm` son documentos JSON UTF-8 con extensión propia de Mishu. No son archivos FreeMind ni mapas mentales de otros programas que también usan `.mm`.

- Exportar colección, carpeta o nota crea `.mm` con `format: "mishu-item"`, `version: 1`.
- Exportar copia de seguridad crea `.mm` con el estado completo de la biblioteca.
- Los importadores aceptan tanto `.mm` como los antiguos `.json`.
- Importar elementos añade contenido sin sustituir la biblioteca; importar una copia de seguridad la reemplaza tras confirmación.
- El almacenamiento interno y autoguardado no cambian.
