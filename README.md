# FeriaFind - Grupo 13

Aplicación móvil para localización y gestión de ferias libres, desarrollada con Kotlin (Jetpack Compose) y Microservicios Spring Boot.

## 👥 Integrantes
- [Jonathan Ferrer]
- [Jaime Hernandez]

## 🚀 Funcionalidades
1. **Autenticación:** Registro y Login seguro.
2. **Gestión de Perfil:** Edición y eliminación de cuenta (CRUD Usuario).
3. **Geolocalización:** Mapa interactivo (OpenStreetMap).
4. **Catálogo:** Búsqueda y filtrado de productos y vendedores.
5. **API Externa:** Integración con Open-Meteo para clima en tiempo real.

## 🔗 Endpoints & Arquitectura
- **App Móvil:** MVVM, Retrofit, Room, DataStore.
- **Backend:** Spring Boot, JPA, HATEOAS, Oracle Cloud.
- **Microservicios:**
  - Usuario: `https://microuser.onrender.com/api/v1/usuarios`
  - Productos: `https://microprod.onrender.com/api/v1/productos`
  - Vendedores: `https://microvend.onrender.com/api/v1/vendedores`

## ⚙️ Ejecución
1. Clonar repositorio.
2. Abrir en Android Studio Ladybug/Koala.
3. Sincronizar Gradle.
4. Ejecutar en Emulador (API 26+).
