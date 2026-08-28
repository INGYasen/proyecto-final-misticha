# Pagatu S1 - Comandos manuales (PowerShell)
# Ejecutar desde la raiz del monorepo o ajustar rutas

# 1) Java 25 (requerido por Spring Boot 4.0.7)
$env:JAVA_HOME = "C:\Users\yasen\.jdk\jdk-25.0.2"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version

# 2) Ir al microservicio
cd "c:\Users\yasen\Downloads\distribuidas 5to\pagatu\services\pagatu-catalogo-ms"

# 3) Levantar PostgreSQL DEV
docker compose -f compose-dev.yml up -d
docker ps

# 4) Verificar base de datos
docker exec pagatu-postgres-catalogo-dev psql -U pagatu -d pagatu_catalogo_db -c "SELECT current_database();"
docker exec pagatu-postgres-catalogo-dev psql -U pagatu -d pagatu_catalogo_db -c "\dt"

# 5) Compilar y ejecutar
.\mvnw.cmd -DskipTests package
.\mvnw.cmd spring-boot:run

# 6) Pruebas API (en otra terminal)
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/saludo"
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/actuator/health"
.\scripts\test-crud.ps1
