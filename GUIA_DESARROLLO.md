# Guía de Mishu — desarrollo y distribución

## Plataformas
- Android: `app/src/main/assets/index.html`, Gradle y `.github/workflows/android.yml`.
- Windows: el mismo HTML empaquetado por `windows/Mishu.py` con PyInstaller; SQLite en `%LOCALAPPDATA%/Mishu/mishu.sqlite`.
- Web: `web/index.html`, publicado mediante `.github/workflows/pages.yml`.

## Archivos de usuario
El formato actual para importar y exportar es `.json`. La apertura directa en Windows admite rutas `.json`; la asociación por doble clic se configura en Windows. La compatibilidad histórica de formatos previos se documenta en el historial.

## Precauciones
Exporta una copia JSON antes de reemplazar una instalación. El ZIP contiene fuentes, no un EXE compilado. No se han realizado pruebas de interfaz en Windows o Android reales.


---

## Github Pages Instrucciones

# Publicar Mishu en GitHub Pages

1. Descomprime este ZIP. **Sube el contenido de la carpeta `Mishu` a la raíz de tu repositorio**, de modo que GitHub vea `.github/workflows/pages.yml` y `web/index.html`. No subas el ZIP sin extraer.
2. En GitHub entra a **Settings > Pages > Build and deployment** y selecciona **GitHub Actions** como Source.
3. En **Actions > Publish Mishu Web to GitHub Pages > Run workflow** ejecuta la publicación manualmente, o haz un commit en `main` que modifique `web/`.
4. Abre la URL mostrada en **Settings > Pages**. Normalmente será `https://USUARIO.github.io/REPOSITORIO/`.

## Guardado y privacidad

Mishu web guarda inicialmente en el almacenamiento local del navegador. En Chrome o Edge de escritorio, usa **Seleccionar carpeta Mishu** para autorizar la escritura de `mishu/autosave.json`. El navegador exige permiso; no se puede garantizar acceso automático ni compatibilidad con todos los móviles. Exporta copias JSON regularmente. **GitHub Pages no almacena tus notas en el servidor**.

## Verificación

El workflow publica `web/index.html` como web estática por HTTPS. No se ha desplegado al repositorio del usuario ni probado el sitio publicado desde aquí.


---

## Pdf Compuesto

# Mishu 2.8 · PDF compuesto

Desde **Biblioteca**, selecciona **PDF compuesto**. Marca una colección completa o carpetas específicas de distintas colecciones. Las casillas de colección seleccionan todas sus carpetas. El contador indica cuántas notas se incluirán. Pulsa **Vista previa del PDF** para abrir el documento unido, en el orden actual de colecciones, carpetas y notas. Cada nota comienza en una página nueva.

La opción **PDF** dentro de una nota permanece sin cambios y exporta solamente esa nota. Ambas vistas comienzan con el estilo actual; puedes marcar **Sin estilo** antes de imprimir o guardar.

La exportación utiliza la función de impresión del WebView/navegador y requiere comprobación en Android y Windows. Se conserva la biblioteca sin modificarla.


---

## Seleccion Texto

Mishu 2.7: selección azul nativa en el texto; barra contextual automática; eliminado botón Seleccionar / herramientas; rangos ocultos aparecen grisáceos en edición mediante espejo de texto. Sin compilación en dispositivos reales.


---

## Video Fix

Mishu 2.6: reproductor integrado HTML5 para MP4/WebM/Ogg; vista previa embebida de YouTube, Vimeo y Google Drive; ampliación en modal y enlace alternativo externo. Algunos proveedores restringen la reproducción embebida. No se ha probado en dispositivo real.\n