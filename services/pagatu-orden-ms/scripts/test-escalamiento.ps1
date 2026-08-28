# Verifica que las dos instancias de pagatu-orden-ms responden por separado
# Requiere: instancia 1 en 8080 y instancia 2 en 8081 (--server.port=8081)

Write-Host "=== Instancia 1 (8080) ===" -ForegroundColor Cyan
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/saludo"
(Invoke-RestMethod -Method Get -Uri "http://localhost:8080/actuator/health").status

Write-Host ""
Write-Host "=== Instancia 2 (8081) ===" -ForegroundColor Cyan
Invoke-RestMethod -Method Get -Uri "http://localhost:8081/saludo"
(Invoke-RestMethod -Method Get -Uri "http://localhost:8081/actuator/health").status

Write-Host ""
Write-Host "=== Misma base de datos: dato escrito en 8080 se lee en 8081 ===" -ForegroundColor Cyan
$codigo = "ORD-ESCALA-" + (Get-Date -Format "HHmmss")
$creada = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/ordenes" `
    -ContentType "application/json" `
    -Body (@{ codigo = $codigo; cliente = "Prueba escalamiento"; estado = "CREADA" } | ConvertTo-Json)
Write-Host "Creada en 8080 -> id $($creada.id) / codigo $codigo"

$leida = Invoke-RestMethod -Method Get -Uri "http://localhost:8081/api/ordenes/$($creada.id)"
Write-Host "Leida en 8081 -> id $($leida.id) / codigo $($leida.codigo) / cliente $($leida.cliente)"

Write-Host ""
Write-Host "=== Puertos en escucha ===" -ForegroundColor Cyan
netstat -ano | findstr "LISTENING" | findstr ":808"
