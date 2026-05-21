# 🦷 DentalPlus Front-end Repository

> Aplicación Android para la gestión integral de clínicas dentales, construida con Kotlin y Jetpack Compose.

[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose) 
<!-- [![Release](https://img.shields.io/badge/Release-v0.1.0-blue?style=for-the-badge)](https://github.com/Eskibal/DentalPlus_Frontend/releases) -->

---

## 📋 Descripción
La aplicación satisface la necesidad de disponer de una herramienta informática que permita simular de forma realista la gestión clínica y administrativa de una clínica odontológica.

La aplicación facilitará la gestión completa de la información de los pacientes, incluyendo el registro de datos personales, administrativos y clínicos, la gestión de la primera visita, el registro del motivo de la consulta, la exploración clínica mediante odontogramas normalizados, la recopilación del historial médico y el almacenamiento y visualización de imágenes radiográficas asociadas a cada paciente. 

Asimismo, permitirá la planificación y organización de las visitas odontológicas en función de la agenda de los especialistas y del tipo de tratamiento a realizar.

> **Nota:** Este repositorio contiene únicamente el frontend Android. El backend se encuentra en un repositorio separado.

---

## ✨ Funcionalidades

<!-- TODO: Ajusta esta lista según las features reales de tu app -->

- 📅 **Gestión de citas** — Reserva, modificación y cancelación de citas dentales
- 👤 **Perfil de paciente** — Historial clínico y datos personales
- 🔔 **Notificaciones** — Recordatorios de citas y avisos del dentista
- 🔐 **Autenticación segura** — Login y registro de usuarios
- 🏥 **Panel del dentista** — Gestión de agenda y pacientes (rol profesional)

---

## 🛠️ Tecnologías
| Herramienta | Uso |
| --- | --- |
| **Kotlin** | Lenguaje principal |
| **Jetpack Compose** | UI declarativa |
| **Gradle (KTS)** | Sistema de build |
| **Android SDK** | Plataforma base |
<!-- TODO: Añade aquí librerías que uses: Retrofit, Hilt, Room, Coil, etc. -->

---

## 🚀 Instalación y configuración
 
### Requisitos previos
 
- Android Studio Hedgehog o superior
- JDK 17+
- Android SDK API 26+
### Pasos
 
1. **Clona el repositorio**
   ```bash
   git clone https://github.com/Eskibal/DentalPlus_Frontend.git
   cd DentalPlus_Frontend
   ```
 
2. **Abre el proyecto en Android Studio**
   ```
   File → Open → selecciona la carpeta del proyecto
   ```
 
3. **Configura la URL del backend**
   <!-- TODO: Indica dónde se configura la base URL (p.ej. local.properties o un archivo de config) -->
   ```
   # En local.properties o en el archivo de configuración correspondiente:
   BASE_URL=https://dentalplus-backend.onrender.com/
   ```
 
4. **Sincroniza Gradle y ejecuta**
   ```
   Build → Make Project  (o Ctrl+F9)
   Run → Run 'app'       (o Shift+F10)
   ```
 
---
 
## 📁 Estructura del proyecto
 
```
DentalPlus_Frontend/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/dentalplus_frontend       # Código fuente Kotlin
│   │   │   ├── res/                                       # Recursos (layouts, drawables, strings)
│   │   │   └── AndroidManifest.xml
│   │   └── test/                                          # Tests unitarios
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
└── settings.gradle.kts
```
 
---
 
## 📱 Releases
 
| Versión | Fecha | Notas |
|---|---|---|
| [v0.1.0](https://github.com/Eskibal/DentalPlus_Frontend/releases/tag/v.0.1.0) | Mayo 2026 | Primera versión pública |
<!-- TODO: Actualiza la tabla con cada nueva release -->
 
---
 
## 🤝 Contribuir
 
Las contribuciones son bienvenidas. Por favor, sigue estos pasos:
 
1. Haz un fork del repositorio
2. Crea una rama para tu feature: `git checkout -b feature/nueva-funcionalidad`
3. Realiza tus cambios y haz commit: `git commit -m 'feat: añadir nueva funcionalidad'`
4. Sube tu rama: `git push origin feature/nueva-funcionalidad`
5. Abre un Pull Request
Para bugs o sugerencias, abre un [Issue](https://github.com/Eskibal/DentalPlus_Frontend/issues).
 
---
 
## 👥 Equipo

**Jean Patrick Esquibal Surop & Marc López Molina** - Desarrolladores Front-end & Diseñadores de la aplicación

**Jiahao Liu & Adam Serroukh** - Desarrolladores Back-end & Gestores de la Base de datos

Desarrollado con ❤️ por el equipo de **DentalPlus**.
 
[![GitHub](https://img.shields.io/badge/GitHub-Eskibal-181717?style=flat&logo=github)](https://github.com/Eskibal)
[![GitHub](https://img.shields.io/badge/GitHub-MarcLopezMolina-181717?style=flat&logo=github)](https://github.com/MarcLopezMolina)
[![GitHub](https://img.shields.io/badge/GitHub-LiuUexe-181717?style=flat&logo=github)](https://github.com/LiuUexe)
[![GitHub](https://img.shields.io/badge/GitHub-askm6-181717?style=flat&logo=github)](https://github.com/askm6)
