$baseUrl = "http://localhost:8080"

Write-Host "=== Saludo ===" -ForegroundColor Cyan
Invoke-RestMethod -Method Get -Uri "$baseUrl/saludo"

Write-Host "`n=== Health ===" -ForegroundColor Cyan
Invoke-RestMethod -Method Get -Uri "$baseUrl/actuator/health"

Write-Host "`n=== Crear categoria ===" -ForegroundColor Cyan
$categoria = Invoke-RestMethod `
    -Method Post `
    -Uri "$baseUrl/api/categorias" `
    -ContentType "application/json" `
    -Body '{"nombre":"Electrodomesticos","descripcion":"Linea blanca y pequenos electrodomesticos"}'
$categoria | ConvertTo-Json

Write-Host "`n=== Listar categorias ===" -ForegroundColor Cyan
Invoke-RestMethod -Method Get -Uri "$baseUrl/api/categorias" | ConvertTo-Json

Write-Host "`n=== Crear producto ===" -ForegroundColor Cyan
$producto = Invoke-RestMethod `
    -Method Post `
    -Uri "$baseUrl/api/productos" `
    -ContentType "application/json" `
    -Body '{"nombre":"Matricula","descripcion":"Matricula del ciclo","precio":350.00,"activo":true,"categoriaId":1}'
$producto | ConvertTo-Json

Write-Host "`n=== Listar productos ===" -ForegroundColor Cyan
Invoke-RestMethod -Method Get -Uri "$baseUrl/api/productos" | ConvertTo-Json

Write-Host "`nPruebas completadas." -ForegroundColor Green
