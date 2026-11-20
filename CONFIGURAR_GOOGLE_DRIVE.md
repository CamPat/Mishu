# Google Drive — preparación de lanzamiento (Mishu 4.10)

Esta versión incluye un flujo de autorización Android experimental con AppAuth y los flujos web/Windows previos. **No se ha verificado con credenciales reales ni en dispositivos.** No se considera listo para producción hasta superar las pruebas siguientes.

## Configuración obligatoria (no se pueden inventar estas credenciales)

1. En Google Cloud Console, crea un proyecto, habilita Google Drive API y configura la pantalla de consentimiento OAuth, con usuarios de prueba si la aplicación está en modo Testing.
2. Crea un **cliente OAuth web** y registra el origen exacto de GitHub Pages (por ejemplo `https://campat.github.io`); escribe su Client ID en Mishu web.
3. Crea un **cliente OAuth de escritorio** y escribe su Client ID en Mishu Windows. El programa usa navegador externo y callback loopback con PKCE.
4. Para Android, configura un cliente OAuth y una URI de retorno aceptada por Google para tu tipo de aplicación. El flujo de AppAuth incluido necesita verificar que Google acepte el esquema personalizado. Construye con las propiedades `MISHU_GOOGLE_ANDROID_CLIENT_ID` y `MISHU_GOOGLE_ANDROID_REDIRECT_SCHEME`; ejemplo: `./gradlew assembleDebug -PMISHU_GOOGLE_ANDROID_CLIENT_ID=... -PMISHU_GOOGLE_ANDROID_REDIRECT_SCHEME=com.googleusercontent.apps...`. Si Google rechaza el redirect, **no distribuyas el APK**: reemplaza el flujo por el mecanismo Android recomendado por Google (Google Identity Services / AuthorizationClient).
5. No introduzcas client secrets ni tokens en el repositorio. Los Client IDs no son secretos, pero deben corresponder al tipo de cliente correcto.

## Validación obligatoria antes de declarar sincronización lista

- Autorizar cuenta en web, Windows y Android; confirmar que Android devuelve token válido y que Google Drive API acepta llamadas desde WebView.
- Subir una biblioteca de prueba en la carpeta `principal`, cerrar sesión, descargar en otro dispositivo y verificar todas las notas y moldes.
- Cambiar una nota en el segundo dispositivo, sincronizar, descargar en el primero, comprobar contenido y backups.
- Probar 2 carpetas independientes (`principal`, `medicina`), conflicto de ediciones simultáneas y cancelación de sobrescritura.
- Probar caducidad del token y reconexión en las tres plataformas.
- Probar archivos grandes, interrupción de red y recuperación de un backup.
- Revisar protección contra modificaciones concurrentes: la segunda lectura reduce el riesgo, pero no ofrece escritura condicional atómica en Drive.

## Limitaciones conocidas

- La sincronización es manual y reemplaza la biblioteca completa; no fusiona cambios.
- Las sesiones OAuth son temporales. No se ha implementado almacenamiento seguro de refresh tokens ni reconexión silenciosa.
- El alcance `drive.file` permite acceder a archivos creados/abiertos por la aplicación; no garantiza visibilidad de carpetas arbitrarias preexistentes.
- La app no debe considerarse lista para sincronizar hasta completar la validación de Android y las pruebas de interoperabilidad.
