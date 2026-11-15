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
