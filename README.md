# CumpliApp 📱

> **Aplicación de Gestión de Tareas y Productividad**  
> Desarrollado para el Tercer Examen Parcial de Desarrollo de Nuevas Plataformas

## 📋 Descripción

CumpliApp es una aplicación móvil Android nativa desarrollada en Kotlin que permite a los usuarios gestionar sus tareas y actividades de manera eficiente. La aplicación combina funcionalidades modernas de productividad con una interfaz intuitiva construida con Jetpack Compose.

## ✨ Características Principales

### 🎯 Gestión de Tareas

- **Crear y editar actividades** con título, descripción y fecha de entrega
- **Categorización** de tareas por tipo (Trabajo, Personal, Estudio, Salud, Otros)
- **Priorización** de actividades (Alta, Media, Baja)
- **Marcar tareas como completadas** y visualizarlas en una sección separada
- **Sistema de recordatorios** mediante notificaciones push

### ⏱️ Modo Enfoque (Focus Mode)

- Cronómetro integrado para seguimiento del tiempo dedicado a cada tarea
- Servicio en primer plano que permite mantener el seguimiento activo
- Acumulación automática del tiempo trabajado en cada actividad
- Notificaciones persistentes durante el modo enfoque

### 📊 Estadísticas y Análisis

- Visualización de estadísticas de productividad
- Seguimiento de tareas completadas
- Análisis del tiempo invertido en diferentes actividades

### ⚙️ Configuración Personalizable

- **Temas**: Modo claro, modo oscuro o automático según el sistema
- Gestión de preferencias de usuario
- Configuración de notificaciones

## 🛠️ Tecnologías Utilizadas

### Framework y Lenguaje

- **Kotlin**: Lenguaje de programación principal
- **Jetpack Compose**: UI moderna y declarativa
- **Material Design 3**: Sistema de diseño

### Arquitectura

- **Clean Architecture**: Separación en capas (Data, Domain, UI)
- **MVVM Pattern**: Model-View-ViewModel
- **Hilt**: Inyección de dependencias
- **Kotlin Coroutines**: Programación asíncrona y concurrente

### Persistencia de Datos

- **Room Database**: Base de datos local SQLite con ORM
- **DataStore Preferences**: Almacenamiento de preferencias de usuario

### Navegación y UI

- **Jetpack Navigation Compose**: Sistema de navegación
- **Material Icons Extended**: Biblioteca extendida de iconos

### Servicios y Workers

- **WorkManager**: Programación de tareas en segundo plano y recordatorios
- **Foreground Service**: Servicio para el modo enfoque
- **Hilt Worker**: Integración de WorkManager con Hilt

## 📁 Estructura del Proyecto

```
app/src/main/java/com/idnp2025b/cumpliapp/
├── data/                           # Capa de datos
│   ├── local/                      # Fuentes de datos locales
│   │   ├── dao/                    # Data Access Objects (Room)
│   │   ├── database/               # Configuración de base de datos
│   │   ├── entity/                 # Entidades de Room
│   │   └── preferences/            # DataStore preferences
│   ├── model/                      # Modelos de dominio
│   │   ├── Actividad.kt
│   │   ├── Categoria.kt
│   │   ├── Prioridad.kt
│   │   ├── AppTheme.kt
│   │   └── UserPreferences.kt
│   └── repository/                 # Repositorios
├── di/                             # Módulos de inyección de dependencias (Hilt)
├── domain/                         # Lógica de negocio
├── service/                        # Servicios en primer plano
│   └── EnfoqueService.kt
├── ui/                             # Capa de presentación
│   ├── components/                 # Componentes reutilizables
│   ├── navigation/                 # Configuración de navegación
│   ├── screens/                    # Pantallas de la app
│   │   ├── lista/                  # Pantalla principal de tareas
│   │   ├── crear/                  # Crear nueva tarea
│   │   ├── editar/                 # Editar tarea existente
│   │   ├── completadas/            # Tareas completadas
│   │   ├── estadisticas/           # Estadísticas
│   │   └── configuracion/          # Ajustes
│   ├── theme/                      # Tema de la aplicación
│   └── utils/                      # Utilidades de UI
├── worker/                         # Workers de WorkManager
└── util/                           # Utilidades generales
```

## 🔧 Requisitos del Sistema

### Desarrollo

- **Android Studio**: Hedgehog | 2023.1.1 o superior
- **JDK**: Java 11
- **Gradle**: 8.0+
- **Kotlin**: 2.0.21

### Dispositivo/Emulador

- **SDK Mínimo**: Android 7.0 (API 24)
- **SDK Objetivo**: Android 14 (API 36)
- **Permisos requeridos**:
  - `POST_NOTIFICATIONS`: Para enviar notificaciones (Android 13+)
  - `FOREGROUND_SERVICE`: Para el modo enfoque
  - `FOREGROUND_SERVICE_SPECIAL_USE`: Para cronómetro en primer plano

## 🚀 Instalación y Ejecución

### 1. Clonar el repositorio

```bash
git clone <repository-url>
cd CumpliApp
```

### 2. Abrir en Android Studio

- Abrir Android Studio
- Seleccionar "Open an Existing Project"
- Navegar a la carpeta del proyecto
- Esperar a que Gradle sincronice las dependencias

### 3. Configurar dispositivo

- Conectar un dispositivo físico con depuración USB habilitada, o
- Configurar un emulador Android (recomendado API 33+)

### 4. Ejecutar la aplicación

```bash
./gradlew installDebug
```

O usar el botón "Run" (▶️) en Android Studio

## 📦 Dependencias Principales

```kotlin
// Jetpack Compose
implementation(platform(libs.androidx.compose.bom))
implementation(libs.androidx.compose.material3)

// Room Database
implementation("androidx.room:room-runtime:2.8.4")
implementation("androidx.room:room-ktx:2.8.4")

// Hilt - Dependency Injection
implementation("com.google.dagger:hilt-android:2.57.1")
implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

// Navigation
implementation("androidx.navigation:navigation-compose:2.9.6")

// DataStore Preferences
implementation("androidx.datastore:datastore-preferences:1.0.0")

// WorkManager
implementation("androidx.work:work-runtime-ktx:2.9.0")
implementation("androidx.hilt:hilt-work:1.2.0")

// Material Icons Extended
implementation("androidx.compose.material:material-icons-extended")
```

## 🎨 Capturas de Pantalla

> _[Agregar capturas de pantalla de la aplicación]_

## 👥 Equipo de Desarrollo

Este proyecto fue desarrollado como parte del curso de Desarrollo de Nuevas Plataformas.

**Package**: `com.idnp2025b.cumpliapp`

## 📝 Notas de Desarrollo

### Arquitectura Implementada

- **Separación de responsabilidades**: Cada capa tiene una responsabilidad única
- **Inversión de dependencias**: Las capas superiores no dependen de las inferiores
- **Inyección de dependencias**: Uso de Hilt para gestionar dependencias
- **Single Source of Truth**: Room como única fuente de verdad para los datos

### Características Técnicas Destacadas

1. **Persistencia offline completa**: Toda la información se almacena localmente
2. **UI reactiva**: Uso de Flows para observar cambios en tiempo real
3. **Servicio en primer plano**: Permite mantener el cronómetro activo incluso con la app en segundo plano
4. **Gestión de permisos moderna**: Solicitud dinámica de permisos según la versión de Android
5. **Theming dinámico**: Soporte para tema claro/oscuro con persistencia de preferencias

## 🐛 Problemas Conocidos

- Los recordatorios requieren que el permiso de notificaciones esté concedido en Android 13+
- El modo enfoque puede verse afectado por optimizaciones agresivas de batería en algunos dispositivos

## 📄 Licencia

Este proyecto es un trabajo académico desarrollado para fines educativos.

## 🔗 Enlaces Útiles

- [Documentación de Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Guía de Room Database](https://developer.android.com/training/data-storage/room)
- [Hilt para Android](https://developer.android.com/training/dependency-injection/hilt-android)
- [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)

---

**Desarrollado con ❤️ para el curso de Desarrollo de Nuevas Plataformas**
