# Formato .mishu (contrato de compatibilidad)

Extensión oficial de intercambio: `.mishu`. Los archivos contienen JSON UTF-8.

## Contrato v1
- Elementos: `format: "mishu-item"`, `version: 1`, `type: "collection" | "folder" | "note"`, `collections`, `folders`, `notes`.
- Backups: objeto con `schema: 1`, `seq`, `collections`, `folders`, `notes`, `trash`, `templates`.
- Lectores futuros DEBEN aceptar estos formatos v1; nuevas versiones deben migrar sin modificar archivos antiguos.
- `.mm` y `.json` legados se siguen aceptando en el selector de importación.
- En Android, el intent ACTION_VIEW recibe el archivo mediante content URI y solicita confirmación antes de importar.
- La asociación de Windows con `.mishu` requiere registro/instalador del sistema operativo; el ZIP no registra esa asociación automáticamente.
