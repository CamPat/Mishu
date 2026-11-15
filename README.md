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
