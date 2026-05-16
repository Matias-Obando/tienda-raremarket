# Guía de Testing - Backend Raremarket

## Overview
Se han creado tests unitarios completos para los principales servicios del backend usando:
- **JUnit 5** - Framework de testing
- **Mockito** - Mock framework para aislar dependencias
- **Spring Boot Test** - Testing utilities de Spring

## Tests Creados

### 1. PasswordResetServiceTest
Ubicación: `backend/src/test/java/com/raremarket/backend/service/PasswordResetServiceTest.java`

**Método testeado**: PasswordResetService

**Casos de prueba**:
- ✅ Solicitar reset de contraseña con email válido
- ✅ Manejar usuario no existente
- ✅ Normalizar email a minúsculas
- ✅ Manejar email nulo/en blanco
- ✅ Reset de contraseña con token válido
- ✅ Validar token nulo/en blanco
- ✅ Validar contraseña nula/en blanco
- ✅ Manejar token inválido/expirado
- ✅ Manejar usuario no encontrado durante reset
- ✅ Trim whitespace en email y token

**Total de tests**: 14

### 2. UserServiceTest
Ubicación: `backend/src/test/java/com/raremarket/backend/service/UserServiceTest.java`

**Métodos testeados**: 
- register()
- authenticate()
- findById()
- updateProfile()
- getAllUsers()

**Casos de prueba**:
- ✅ Registro exitoso de usuario
- ✅ Validación de usuario nulo
- ✅ Validación de email nulo/en blanco
- ✅ Validación de contraseña nula/en blanco
- ✅ Prevenir duplicados de username/email
- ✅ Normalización de email
- ✅ Generación automática de UUID
- ✅ Autenticación con username
- ✅ Autenticación con email
- ✅ Validaciones de autenticación
- ✅ Actualizar perfil exitosamente
- ✅ Validar duplicados al actualizar
- ✅ Permitir que usuario actualice su propio email
- ✅ Limpiar avatar

**Total de tests**: 25

### 3. ChatServiceTest
Ubicación: `backend/src/test/java/com/raremarket/backend/service/ChatServiceTest.java`

**Métodos testeados**:
- createOrGetConversation()
- listConversations()
- sendMessage()
- markConversationAsRead()
- deleteConversation()

**Casos de prueba**:
- ✅ Crear nueva conversación
- ✅ Obtener conversación existente
- ✅ Validar IDs de usuarios diferentes
- ✅ Validar itemId no vacío
- ✅ Validar existencia de usuarios
- ✅ Listar conversaciones del usuario
- ✅ Enviar mensaje exitosamente
- ✅ Trim y validación de contenido
- ✅ Marcar conversación como leída
- ✅ Borrar conversación con autenticación

**Total de tests**: 21

## Ejecución de Tests

### Opción 1: Maven (Recomendado)
```bash
# Ejecutar todos los tests
mvn test

# Ejecutar un archivo de test específico
mvn test -Dtest=PasswordResetServiceTest

# Ejecutar un método de test específico
mvn test -Dtest=PasswordResetServiceTest#testRequestPasswordReset_ValidEmail

# Ejecutar tests con cobertura
mvn test jacoco:report

# Ver reporte de cobertura
mvn jacoco:report  # Genera reporte en target/site/jacoco/index.html
```

### Opción 2: IDE (VS Code/IntelliJ)
1. Instalar extensión de "Testing" para Java
2. Hacer clic en "Run" o "Debug" sobre el método de test
3. Ver resultados en la ventana de output

### Opción 3: Terminal (Windows)
```powershell
cd backend

# Compilar y ejecutar tests
mvn clean test

# Con output detallado
mvn test -X
```

## Tests intencionales que fallan

Hay una serie de pruebas añadidas intencionalmente que fallan por defecto para comprobar que el runner detecta fallos. Estas pruebas están destinadas a ejecutarse sólo cuando se quiere validar el comportamiento del framework de testing.

- Ubicaciones:
   - `backend/src/test/java/com/raremarket/backend/service/IntentionalFailuresTest.java`
   - `backend/src/test/java/com/raremarket/backend/service/PasswordResetServiceTest.java` (test `intentionalFail_PasswordReset`)
   - `backend/src/test/java/com/raremarket/backend/service/MailServiceTest.java` (test `intentionalFail_MailService`)
   - `backend/src/test/java/com/raremarket/backend/service/ItemServiceTest.java` (test `intentionalFail_ItemService`)

- Ejecutarlas (por ejemplo):

```bash
cd backend
mvn test -Dtest=IntentionalFailuresTest
# o ejecutar los tests específicos por clase
mvn test -Dtest=PasswordResetServiceTest
```

- Excluir/ignorar estas pruebas en la ejecución normal:
   - Marcar los tests con `@Disabled` en el código (si se quiere mantener en el repo pero no ejecutarlos).
   - Ejecutar sólo las clases de test que te interesen (evita las intencionales) usando `-Dtest=...`.

Se recomienda dejar estas pruebas en el repositorio sólo si el equipo entiende su propósito; alternativamente pueden moverse a un perfil Maven separado o documentarse claramente como se hace aquí.


## Estructura de los Tests

### Patrón AAA (Arrange-Act-Assert)

Cada test sigue este patrón:

```java
@Test
@DisplayName("Descripción clara del test")
void testMethodName() {
    // Arrange - Preparar datos y mocks
    User user = new User();
    when(userRepository.findById(id)).thenReturn(Optional.of(user));
    
    // Act - Ejecutar la función a testear
    Optional<User> result = userService.findById(id);
    
    // Assert - Verificar que el resultado es correcto
    assertTrue(result.isPresent());
    assertEquals(user.getId(), result.get().getId());
}
```

## Dependencias de Testing

El proyecto incluye automáticamente (via Spring Boot):
- **JUnit 5** - @Test, @DisplayName, assertions
- **Mockito** - @Mock, @ExtendWith(MockitoExtension.class)
- **Spring Test** - Testing utilities para Spring Boot
- **AssertJ** - Assertions mejorados (opcional)

### Agregación manual (si es necesaria)
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

## Convenciones de Naming

- Nombre del test: `Test{NombreClase}.java`
- Método de test: `test{NombreMetodo}_{Escenario}`
  - Ejemplo: `testRegister_Success`, `testRegister_NullUser`
- @DisplayName: Descripción clara en lenguaje natural

## Cobertura de Tests

Cobertura aproximada:
- **PasswordResetService**: ~90% cobertura
- **UserService**: ~85% cobertura
- **ChatService**: ~80% cobertura

Para mejorar la cobertura:
```bash
mvn clean test jacoco:report
# Abre target/site/jacoco/index.html para ver detalles
```

## Best Practices Utilizados

✅ **Isolation**: Cada test es independiente
✅ **Mocking**: Se mockean todas las dependencias externas
✅ **Nombres claros**: Los tests describen qué están probando
✅ **Casos edge**: Se incluyen validaciones de null, blank, etc.
✅ **Assertions específicos**: Se verifica comportamiento exacto
✅ **No integración**: Son tests unitarios, no de integración

## Troubleshooting

### Test falla con "No beans found"
- Asegúrate de usar @ExtendWith(MockitoExtension.class)
- No inyectes con @Autowired en tests unitarios, usa @Mock

### Mock no funciona como esperado
- Verifica que `when(...).thenReturn(...)` esté antes del `act`
- Usa `ArgumentCaptor` para capturar argumentos pasados a mocks

### Test lento
- No hagas I/O real, todo debe ser mockeado
- Evita sleeps o delays en tests

## Próximos Pasos

1. **Ampliar tests** a otros servicios:
   - ItemService
   - OrderService
   - MailService
   - AuthTokenService

2. **Tests de integración**:
   - @SpringBootTest para tests con contexto
   - @DataJpaTest para tests de repositorio
   - TestRestTemplate para tests de API

3. **Coverage goals**:
   - Meta: 80% cobertura general
   - Usar `mvn clean test jacoco:report`

## Comandos Útiles

```bash
# Compilar sin ejecutar tests
mvn clean compile -DskipTests

# Ejecutar solo tests que fallan
mvn test -Dtest=*Test --fail-at-end

# Ejecutar tests en paralelo
mvn test -T 1C

# Listar todos los tests disponibles
mvn test -DdryRun

# Ejecutar test específico por nombre exacto
mvn test -Dtest=PasswordResetServiceTest#testRequestPasswordReset_ValidEmail
```

## Recursos

- [JUnit 5 Docs](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
