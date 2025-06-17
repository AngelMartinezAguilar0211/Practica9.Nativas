# Practica9.Nativas 💧⌚📱

Aplicación multiplataforma desarrollada para Wear OS y Android, enfocada en el seguimiento y sincronización en tiempo real de vasos de agua consumidos, usando almacenamiento local (DataStore) y sincronización con Firebase Realtime Database.

## Estructura del Proyecto

    ├── /Practica9NativasWearOS    // App para Wear OS (reloj)
    ├── /Practica9NativasAndroid   // App para Android (teléfono)
    ├── /core                      // Lógica, modelos y repositorios compartidos

## Funcionalidades Principales
- Registro visual de vasos de agua: Añade y resta con botones visuales + y -.
- Sincronización instantánea entre dispositivos: Al sumar/restar en un dispositivo, el cambio aparece en el otro en tiempo real.
- Persistencia local: El último valor queda guardado con DataStore para funcionar sin conexión.
- Interfaz intuitiva y adaptada: Experiencia de usuario óptima en reloj y teléfono.

## Tecnologías Utilizadas
- Kotlin + Jetpack Compose (para ambas interfaces)
- DataStore Preferences (persistencia local)
- Firebase Realtime Database (sincronización en la nube)
- Firebase Cloud Messaging (notificaciones push)
- Arquitectura multi-módulo (core + apps)

## Instalación y ejecución

### Configura Firebase:
1. Crea un proyecto en Firebase Console.
2. Agrega dos apps: una para el teléfono (com.example.practica9nativasandroid) y otra para el reloj (com.example.practica9nativas).
3. Descarga y coloca el archivo google-services.json correspondiente en la carpeta app/ de cada módulo.
4. Habilita Realtime Database y pon reglas abiertas temporalmente para pruebas.
5. Abre el proyecto en Android Studio, sincroniza Gradle y ejecuta en emuladores o dispositivos Wear/Android.

## Uso
- Pulsa + para agregar un vaso, - para restar.
- Todos los cambios se reflejan en ambos dispositivos y se guardan localmente.
- Las notificaciones push pueden ser enviadas desde la consola de Firebase.
