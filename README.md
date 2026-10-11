# Sistema de Gestión de Biblioteca Escolar

## Autor del proyecto
- **Nombre completo:** Pablo Ibacache
- **Carrera:** Analista Programador
- **Sede:** Campus Virtual

---

## Descripción general del sistema
Aplicación de escritorio en Java Swing para controlar la circulación de libros de una biblioteca escolar. Permite iniciar sesión con rol de bibliotecario o de estudiante, gestionar libros, categorías y estudiantes, registrar préstamos y devoluciones con control de vencimiento y atraso, y generar reportes. Los datos se guardan en una base de datos MySQL mediante JDBC.

El proyecto aplica los patrones MVC, DAO y Singleton, y usa hilos y sincronización en el proceso de préstamo para mantener consistente el stock.

---

## Estructura general del proyecto

```plaintext
BibliotecaEscolar/
├── dist/
│   ├── BibliotecaEscolar.jar        # Ejecutable (Java 8 o superior)
│   ├── ejecutar.bat                 # Abre el sistema con doble clic
│   └── lib/
│       └── mysql-connector-j-8.4.0.jar
├── lib/
│   └── mysql-connector-j-8.4.0.jar  # Conector JDBC de MySQL
├── sql/
│   ├── Script_crea_tablas_biblioteca.sql      # Crea la base y las tablas
│   └── Script_poblado_tablas_biblioteca.sql   # Inserta los datos de ejemplo
├── src/
│   ├── modelo/
│   │   ├── Persona.java             # Clase abstracta con los datos comunes
│   │   ├── Usuario.java             # Subclase de Persona, con contraseña y rol
│   │   ├── Estudiante.java          # Subclase de Persona, con curso
│   │   ├── Rol.java                 # Enum: BIBLIOTECARIO, ESTUDIANTE
│   │   ├── Categoria.java
│   │   ├── Libro.java
│   │   ├── Prestamo.java            # Calcula atraso y estado del préstamo
│   │   └── LibroMasPrestado.java    # Fila del reporte de libros más prestados
│   ├── dao/
│   │   ├── DatabaseConnection.java  # Singleton con la conexión JDBC
│   │   ├── CrudDAO.java             # Interfaz con create, readAll, update y delete
│   │   ├── UsuarioDAO.java
│   │   ├── EstudianteDAO.java
│   │   ├── CategoriaDAO.java
│   │   ├── LibroDAO.java            # Incluye descontarStock y aumentarStock (synchronized)
│   │   └── PrestamoDAO.java
│   ├── controlador/
│   │   ├── ControladorAutenticacion.java
│   │   ├── ControladorLibros.java
│   │   ├── ControladorEstudiantes.java
│   │   ├── ControladorPrestamos.java    # Hilos en segundo plano y métodos synchronized
│   │   └── ResultadoOperacion.java      # Callback cuando termina un hilo
│   ├── vista/
│   │   ├── VentanaLogin.java
│   │   ├── VentanaPrincipal.java        # Menú de navegación según el rol
│   │   ├── VentanaLibros.java
│   │   ├── VentanaCategorias.java
│   │   ├── VentanaEstudiantes.java
│   │   ├── VentanaPrestamos.java
│   │   ├── VentanaReportes.java
│   │   ├── Mensajes.java                # Mensajes de error, advertencia y confirmación
│   │   └── ModeloTablaSoloLectura.java
│   └── main/
│       └── Main.java                # Punto de entrada, abre VentanaLogin
└── README.md
```

---

## Instrucciones para clonar y ejecutar el proyecto

1. Clona el repositorio desde GitHub:

```bash
git clone https://github.com/App-blo/semana-9
```

2. Crea la base de datos ejecutando en MySQL, en este orden:
   - `sql/Script_crea_tablas_biblioteca.sql`
   - `sql/Script_poblado_tablas_biblioteca.sql`
3. En `src/dao/DatabaseConnection.java`, revisa que `USER` y `PASSWORD` correspondan a tu MySQL (por defecto `root` y contraseña vacía).
4. Ejecuta el sistema de una de estas formas:
   - **Desde IntelliJ IDEA:** abre el proyecto, marca `src/` como **Sources Root**, agrega `lib/mysql-connector-j-8.4.0.jar` en **File > Project Structure > Libraries** y ejecuta la clase `Main` (`src/main/Main.java`).
   - **Con el ejecutable:** con MySQL encendido, haz doble clic en `dist/ejecutar.bat` o ejecuta `java -jar dist/BibliotecaEscolar.jar`.

### Usuarios de prueba

Todos usan la contraseña `clave123`.

| Correo | Rol |
|---|---|
| antonia@correo.cl | Bibliotecario (gestión total) |
| carlos@correo.cl | Estudiante (consulta de libros, préstamos y devoluciones) |

---

## Funcionalidades por rol

- **Bibliotecario:** gestión de libros, categorías y estudiantes (CRUD completo), préstamos y devoluciones de cualquier estudiante, y reportes.
- **Estudiante:** consulta de libros (solo lectura), y registro de préstamos y devoluciones propios, viendo solo su historial.

Al registrar un estudiante se crea también su usuario de acceso, vinculado por RUT. Un libro se presta por 7 días: la fecha de vencimiento se calcula automáticamente y los préstamos no devueltos después de esa fecha se muestran como atrasados con los días de atraso.

Reportes disponibles: libros más prestados, historial por estudiante y libros actualmente en préstamo.

---

## Patrones y buenas prácticas aplicados

| Elemento | Dónde |
|---|---|
| **MVC** | Paquetes `modelo`, `vista` y `controlador` separados |
| **DAO** | Un DAO por entidad en `dao`, que implementa la interfaz `CrudDAO` y solo habla con la base de datos |
| **Singleton** | `DatabaseConnection.getInstance()` entrega la única instancia y la única conexión |
| **Herencia y polimorfismo** | `Usuario` y `Estudiante` heredan de la clase abstracta `Persona` e implementan `getDescripcion()` |
| **Hilos** | El préstamo y la devolución se ejecutan en un hilo aparte (`registrarPrestamoEnSegundoPlano`), la interfaz no se congela |
| **Sincronización** | `registrarPrestamo`, `registrarDevolucion`, `descontarStock` y `aumentarStock` son `synchronized`, y la consulta SQL descuenta solo si `stock > 0` |

---

**Repositorio GitHub:** https://github.com/App-blo/semana-9
**Fecha de entrega:** 11/10/2026

---

Duoc UC | Escuela de Informática y Telecomunicaciones | Evaluación Final Transversal
