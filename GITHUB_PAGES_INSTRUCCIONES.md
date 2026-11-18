# Publicar Mishu en GitHub Pages

1. Descomprime este ZIP. **Sube el contenido de la carpeta `Mishu` a la raíz de tu repositorio**, de modo que GitHub vea `.github/workflows/pages.yml` y `web/index.html`. No subas el ZIP sin extraer.
2. En GitHub entra a **Settings > Pages > Build and deployment** y selecciona **GitHub Actions** como Source.
3. En **Actions > Publish Mishu Web to GitHub Pages > Run workflow** ejecuta la publicación manualmente, o haz un commit en `main` que modifique `web/`.
4. Abre la URL mostrada en **Settings > Pages**. Normalmente será `https://USUARIO.github.io/REPOSITORIO/`.

## Guardado y privacidad

Mishu web guarda inicialmente en el almacenamiento local del navegador. En Chrome o Edge de escritorio, usa **Seleccionar carpeta Mishu** para autorizar la escritura de `mishu/autosave.json`. El navegador exige permiso; no se puede garantizar acceso automático ni compatibilidad con todos los móviles. Exporta copias JSON regularmente. **GitHub Pages no almacena tus notas en el servidor**.

## Verificación

El workflow publica `web/index.html` como web estática por HTTPS. No se ha desplegado al repositorio del usuario ni probado el sitio publicado desde aquí.
