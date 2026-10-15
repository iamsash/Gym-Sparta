# GYM SPARTA - Documentación Técnica del Proyecto

---

## 1. Descripción y Objetivo

**Gym Sparta** (`com.example.appgymsparta`) es una aplicación móvil Android nativa desarrollada para la gestión integral de un gimnasio. Permite administrar el control de accesos de administradores, registro de clientes, catálogo de membresías, inscripciones a planes, seguimiento de pagos (incluyendo abonos parciales), control de asistencias diarias y asignación de rutinas de entrenamiento personalizadas con ejercicios por día.

**Objetivo del Proyecto**: Proporcionar una solución moderna, reactiva, asíncrona e ininterrumpida para la administración del gimnasio, habiendo completado la migración total desde la persistencia local en Room/SQLite hacia la infraestructura en la nube de **Firebase Authentication** y **Cloud Firestore**.

---

## 2. Tecnologías Utilizadas

- **Plataforma y Lenguaje**: Android Nativo en **Java 11**.
- **Interfaz de Usuario**: **XML Nativo** con componentes Material Design 3.
- **Arquitectura de Software**: **MVVM (Model-View-ViewModel)**.
- **Servicios Cloud / Backend**:
  - **Firebase Authentication**: Gestión y autenticación encriptada de usuarios administradores mediante correo y contraseña.
  - **Cloud Firestore**: Base de datos NoSQL reactiva en la nube para almacenamiento de documentos, subcolecciones y consultas en tiempo real.
- **Manejo de Estado e Hilos**:
  - `androidx.lifecycle.LiveData` y `MutableLiveData` para la observación reactiva de estado en UI.
  - Listeners asíncronos no bloqueantes de Firebase (`OnSuccessListener`, `OnFailureListener`, `addSnapshotListener`).
  - `ListenerRegistration` gestionado en el ciclo de vida (`onActive`/`onInactive`) para evitar fugas de memoria.
- **Entorno de Desarrollo**: Android Studio (Gradle Kotlin Script DSL).

---

## 3. Estructura del Proyecto

```
com.example.appgymsparta/
├── adapter/                      # Adaptadores para RecyclerView
│   ├── AsistenciaAdapter.java
│   ├── ClienteAdapter.java
│   ├── DetalleRutinaAdapter.java
│   ├── InscripcionAdapter.java
│   ├── MembresiaAdapter.java
│   ├── PagoAdapter.java
│   └── RutinaAdapter.java
│
├── data/
│   ├── entity/                   # Modelos POJO compatibles con Firestore (@DocumentId, @Exclude)
│   │   ├── Administrador.java
│   │   ├── Asistencia.java
│   │   ├── Cliente.java
│   │   ├── DetalleRutina.java
│   │   ├── Inscripcion.java
│   │   ├── Membresia.java
│   │   ├── Pago.java
│   │   └── Rutina.java
│   │
│   └── repository/               # Capa de datos y consultas asíncronas hacia Cloud Firestore
│       ├── AdministradorRepository.java
│       ├── AsistenciaRepository.java
│       ├── ClienteRepository.java
│       ├── DetalleRutinaRepository.java
│       ├── InscripcionRepository.java
│       ├── MembresiaRepository.java
│       ├── PagoRepository.java
│       └── RutinaRepository.java
│
├── viewmodel/                    # ViewModels (AndroidViewModel) observados por la UI
│   ├── AdministradorViewModel.java
│   ├── AsistenciaViewModel.java
│   ├── ClienteViewModel.java
│   ├── DashboardViewModel.java
│   ├── DetalleRutinaViewModel.java
│   ├── InscripcionViewModel.java
│   ├── MembresiaViewModel.java
│   ├── PagoViewModel.java
│   └── RutinaViewModel.java
│
└── [Activities - Capa de Presentación UI]
    ├── MainActivity.java                # Pantalla de Login con Firebase Auth
    ├── RegistrarAdministradorActivity.java # Registro seguro de Administrador
    ├── DashboardActivity.java           # Panel Principal con métricas en tiempo real
    ├── ClientesActivity.java            # Listado y filtro de clientes
    ├── RegistrarCliente.java            # Formulario de registro/edición de cliente
    ├── ClienteDetalle.java              # Detalle y cambio de estado de cliente
    ├── MembresiasActivity.java          # Catálogo de membresías
    ├── RegistrarMembresia.java          # Creación/edición de planes
    ├── MembresiaDetalle.java            # Detalle de membresía
    ├── InscripcionesActivity.java        # Listado de suscripciones
    ├── RegistrarInscripcion.java        # Formulario de inscripción
    ├── InscripcionDetalle.java          # Detalle y estado de inscripción
    ├── PagosActivity.java               # Historial de ingresos
    ├── RegistrarPago.java               # Registro de abonos parciales y pases diarios
    ├── PagoDetalle.java                 # Detalle de pago
    ├── AsistenciasActivity.java         # Control de ingresos del día
    ├── RegistrarAsistencias.java        # Registro de asistencia por cliente
    ├── RutinasActivity.java             # Listado de rutinas de entrenamiento
    ├── RegistrarRutinaActivity.java     # Creación/edición de rutinas
    ├── RutinaDetalleActivity.java       # Detalle de rutina y subcolección de ejercicios
    ├── AdministradoresActivity.java     # Perfil del administrador
    ├── EditarAdministradorActivity.java  # Edición de datos del perfil
    ├── CambiarContrasenaActivity.java   # Cambio de contraseña mediante Firebase Auth
    ├── ConfiguracionActivity.java       # Configuración general (SharedPreferences)
    └── AcercaSistemaActivity.java       # Información general del software
```

---

## 4. Módulos y Funcionalidades

1. **Autenticación e Inicio de Sesión**:
   - Acceso seguro mediante correo electrónico o nombre de usuario + contraseña en Firebase Authentication.
   - Validación del estado activo del usuario en Firestore.
2. **Registro de Administradores**:
   - Registro de nuevas cuentas administradoras validadas con Código de Autorización del Gimnasio.
   - Creación del usuario en Firebase Auth y guardado de perfil en Firestore `administradores/{uid}`.
3. **Gestión de Clientes**:
   - Búsqueda en tiempo real por nombre, apellido o DNI.
   - Registro, actualización de datos y desactivación/activación de estado.
4. **Catálogo de Membresías**:
   - Definición de planes con nombre, descripción, duración en días y precio.
5. **Inscripciones**:
   - Suscripción de un cliente a una membresía con cálculo automático de fecha de vencimiento.
   - Denormalización de nombres para agilizar lecturas.
6. **Pagos y Abonos Parciales**:
   - Registro de abonos parciales vinculados a una inscripción con cálculo automático de saldo pendiente.
   - Registro de pagos de pases individuales ("Acceso diario") con `idInscripcion = null`.
7. **Control de Asistencias**:
   - Marcación de ingresos diarios validando que el cliente tenga una membresía activa y vigente.
   - Detección de duplicados para el mismo día.
8. **Rutinas y Ejercicios**:
   - Asignación de planes de entrenamiento por cliente y nivel.
   - Gestión de ejercicios por día de la semana (series, repeticiones, peso y descanso) almacenados en subcolección de Firestore.
9. **Dashboard en Tiempo Real**:
   - Cuatro tarjetas de métricas actualizadas automáticamente mediante `SnapshotListener`: Miembros Activos, Asistencias de Hoy, Ingresos del Mes y Vencimientos Próximos.
10. **Perfil de Administrador**:
    - Consulta/edición de datos personales y actualización de contraseña encriptada vía Firebase Auth.

---

## 5. Integración con Firebase Authentication y Cloud Firestore

- **Autenticación**:
  - Operada mediante `com.google.firebase.auth.FirebaseAuth`.
  - El usuario inicia sesión y obtiene una sesión persistente en el dispositivo.
  - La contraseña solo vive encriptada en Firebase Authentication; nunca en Firestore.
- **Modelos Firestore**:
  - Cada entidad Java POJO utiliza la anotación `@DocumentId private String id;` para capturar el ID de documento de Firestore.
  - Métodos auxiliares marcados con `@Exclude` para evitar que campos calculados se persistan en Firestore.
- **Uso de SnapshotListener**:
  - Métodos como `contarActivos()`, `contarPorFecha()` y `sumarIngresosMes()` usan `addSnapshotListener` dentro de un `LiveData` reactivo personalizado que desengancha la suscripción en `onInactive()` evitando fugas de memoria (*Memory Leaks*).

---

## 6. Colecciones y Relaciones en Cloud Firestore

### Esquema Real de Documentos:

1. **Colección `administradores`**:
   - ID de Documento: `{uid}` (UID de Firebase Auth)
   - Campos: `nombres`, `apellidos`, `usuario`, `email`, `telefono`, `estado` (Boolean), `fechaRegistro` ("yyyy-MM-dd").

2. **Colección `clientes`**:
   - ID de Documento: `{clienteId}` (Auto-generado)
   - Campos: `dni`, `nombres`, `apellidos`, `telefono`, `sexo`, `fechaRegistro` ("yyyy-MM-dd"), `estado` (Boolean).

3. **Colección `membresias`**:
   - ID de Documento: `{membresiaId}` (Auto-generado)
   - Campos: `nombre`, `descripcion`, `duracionDias` (Number), `precio` (Double), `estado` (Boolean).

4. **Colección `inscripciones`**:
   - ID de Documento: `{inscripcionId}` (Auto-generado)
   - Campos: `idCliente` (String), `idMembresia` (String), `nombreCliente` (Denormalizado), `dniCliente` (Denormalizado), `nombreMembresia` (Denormalizado), `fechaInicio` ("yyyy-MM-dd"), `fechaVencimiento` ("yyyy-MM-dd"), `precioPagado` (Double), `estado` ("ACTIVA", "VENCIDA", "CANCELADA").

5. **Colección `pagos`**:
   - ID de Documento: `{pagoId}` (Auto-generado)
   - Campos: `idInscripcion` (String/Nullable), `idCliente` (String), `nombreCliente` (Denormalizado), `nombreMembresia` (Denormalizado), `tipoPago` ("Membresía" / "Acceso diario"), `monto` (Double), `fechaPago` ("yyyy-MM-dd'T'HH:mm:ss"), `metodoPago`, `numeroOperacion`, `observacion`.

6. **Colección `asistencias`**:
   - ID de Documento: `{asistenciaId}` (Auto-generado)
   - Campos: `idCliente` (String), `nombreCliente` (Denormalizado), `fecha` ("yyyy-MM-dd"), `horaIngreso` ("HH:mm:ss"), `horaSalida`, `observacion`.

7. **Colección `rutinas` y Subcolección `ejercicios`**:
   - ID de Documento: `rutinas/{rutinaId}`
   - Campos: `idCliente` (String), `nombreCliente` (Denormalizado), `nombre`, `objetivo`, `nivel`, `fechaInicio` ("yyyy-MM-dd"), `estado` (Boolean).
   - **Subcolección `rutinas/{rutinaId}/ejercicios/{ejercicioId}`**:
     - Campos: `idRutina` (String), `ejercicio`, `grupoMuscular`, `dia` ("Lunes", "Martes", etc.), `series` (Number), `repeticiones` (Number), `peso` (Double), `descanso` (Number).

8. **Colección `configuracion`**:
   - Documento `sistema`: `codigoAutorizacionAdmin`, `nombreGimnasio`.

---

## 7. Proceso de Migración desde Room / SQLite

La migración se completó de forma progresiva en 7 fases:

1. **Fase 1**: Adaptación del Módulo Clientes a Firestore y sobrecargas de compatibilidad.
2. **Fase 2**: Migración del Catálogo de Membresías a Firestore.
3. **Fase 3**: Integración de Autenticación de Administradores con Firebase Auth y Firestore (`administradores/{uid}`).
4. **Fase 4**: Migración de Inscripciones y Pagos (soporte de abonos parciales y pases diarios).
5. **Fase 5**: Migración de Asistencias y Rutinas con subcolección de ejercicios.
6. **Fase 6**: Migración de Métricas del Dashboard a consultas asíncronas en tiempo real.
7. **Fase 7 (Limpieza Definitiva de Room)**:
   - Eliminación de los 8 DAOs (`ClienteDao`, `MembresiaDao`, `InscripcionDao`, `PagoDao`, `AsistenciaDao`, `RutinaDao`, `DetalleRutinaDao`, `AdministradorDao`).
   - Eliminación de `AppDatabase.java` y `DateTimeConverter.java`.
   - Limpieza de anotaciones `@Entity`, `@PrimaryKey`, `@ForeignKey` de todas las entidades.
   - Remoción de `room-runtime` y `room-compiler` de `build.gradle.kts`.

---

## 8. Dependencias Actuales (`app/build.gradle.kts`)

```kotlin
dependencies {
    // Android UI & Core
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)

    // Firebase (BOM 34.0.0)
    implementation(platform("com.google.firebase:firebase-bom:34.0.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-functions")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}
```

---

## 9. Instrucciones para Ejecutar la Aplicación

1. **Clonar/Abrir el Proyecto**:
   - Abrir el proyecto `AppGymSparta` en Android Studio.
2. **Sincronización Gradle**:
   - Ejecutar `Sync Project with Gradle Files`.
3. **Archivo de Configuración de Firebase**:
   - Verificar que `google-services.json` esté ubicado en la carpeta `app/`.
4. **Reglas de Seguridad en Cloud Firestore**:
   - Asegurarse de tener publicadas las reglas en la consola web de Firebase:
     ```javascript
     rules_version = '2';
     service cloud.firestore {
       match /databases/{database}/documents {
         match /administradores/{uid} {
           allow create: if request.auth != null && request.auth.uid == uid;
           allow read, write: if request.auth != null;
         }
         match /{document=**} {
           allow read, write: if request.auth != null;
         }
       }
     }
     ```
5. **Ejecutar en Emulador/Dispositivo**:
   - Seleccionar un emulador Android (API 26+) o dispositivo físico y presionar `Run 'app'` (`Shift + F10`).
   - Compilación con Gradle: `:app:assembleDebug`.

---

## 10. Pruebas Realizadas y Pruebas Pendientes

### Pruebas Verificadas a Nivel de Código y Compilación:
- [x] **Compilación de Gradle**: Proyecto compila exitosamente (`BUILD SUCCESSFUL`) con 0 errores.
- [x] **Eliminación Total de Room**: 0 referencias activas a SQLite o Room en todo el proyecto.
- [x] **Inicio de Sesión y Registro**: Flujo verificado con Firebase Authentication y Firestore.
- [x] **Dashboard**: Tarjetas de métricas conectadas a snapshot listeners de Firestore.

### Pruebas Manuales Recomendadas para el Usuario en Emulador:
- [ ] Registrar un nuevo cliente en Firestore y buscarlo por DNI.
- [ ] Crear un plan de membresía.
- [ ] Suscribir un cliente a la membresía.
- [ ] Registrar 2 abonos parciales (ej: S/ 50 + S/ 50) y verificar el saldo pendiente.
- [ ] Marcar la asistencia diaria de un cliente activo.
- [ ] Asignar una rutina con ejercicios en la subcolección y editar/eliminar un ejercicio.

---

## 11. Historial Resumido del Desarrollo

- **Etapa 1**: Diseño de interfaz visual XML, navegación con DrawerLayout/NavigationView y base de datos inicial con Room.
- **Etapa 2**: Integración del SDK de Firebase, colocación de `google-services.json` e inicio de migración.
- **Etapa 3**: Migración incremental de módulos (Clientes, Membresías, Administradores, Inscripciones, Pagos, Asistencias, Rutinas y Dashboard) hacia Cloud Firestore.
- **Etapa 4**: Implementación de Pagos Parciales con cálculo automático de Saldo Pendiente.
- **Etapa 5**: Auditoría de dependencias y eliminación completa de la infraestructura de Room y SQLite.

---

## 12. Tareas Pendientes / Próximos Pasos

1. Realizar el recorrido de pruebas manuales finales en el emulador.
2. Afinar las reglas de seguridad de Firestore (`firestore.rules`) para restringir las escrituras en colecciones generales exclusivamente a cuentas que tengan `estado == true` en `administradores/{uid}`.
3. Habilitar la recuperación de contraseñas por correo electrónico mediante `FirebaseAuth.sendPasswordResetEmail()`.
