# Pruebas CRUD de pagatu-orden-ms por PowerShell (sin Postman)
# Uso:  .\scripts\test-crud.ps1            -> http://localhost:8082 (S2)
#       .\scripts\test-crud.ps1 -Puerto 8081

param([int]$Puerto = 8082)

$base = "http://localhost:$Puerto"
$ErrorActionPreference = "Stop"

function Titulo($texto) {
    Write-Host ""
    Write-Host "=== $texto ===" -ForegroundColor Cyan
}

function CodigoDeError($err) {
    return $err.Exception.Response.StatusCode.value__
}

# En PowerShell 5.1 el cuerpo del error no siempre llega en ErrorDetails:
# hay que leerlo del stream de la respuesta.
function CuerpoDeError($err) {
    if ($err.ErrorDetails -and $err.ErrorDetails.Message) {
        return $err.ErrorDetails.Message
    }
    $stream = $err.Exception.Response.GetResponseStream()
    $stream.Position = 0
    $lector = New-Object System.IO.StreamReader($stream)
    return $lector.ReadToEnd()
}

function MostrarError($err, $esperado) {
    $codigo = CodigoDeError $err
    $color = if ($codigo -eq $esperado) { "Yellow" } else { "Red" }
    Write-Host "HTTP $codigo (esperado $esperado)" -ForegroundColor $color
    Write-Host (CuerpoDeError $err)
}

Titulo "0. Saludo y health en $base"
Invoke-RestMethod -Method Get -Uri "$base/saludo"
(Invoke-RestMethod -Method Get -Uri "$base/actuator/health").status

Titulo "1. POST /api/ordenes (crear orden)"
$codigo = "ORD-" + (Get-Date -Format "yyyyMMddHHmmss")
$orden = Invoke-RestMethod -Method Post -Uri "$base/api/ordenes" `
    -ContentType "application/json" `
    -Body (@{ codigo = $codigo; cliente = "Juan Perez"; estado = "CREADA" } | ConvertTo-Json)
$orden | ConvertTo-Json

Titulo "2. POST /api/orden-detalles (agregar linea y recalcular total)"
$detalle = Invoke-RestMethod -Method Post -Uri "$base/api/orden-detalles" `
    -ContentType "application/json" `
    -Body (@{ ordenId = $orden.id; idProducto = 1; descripcion = "Matricula del ciclo"; cantidad = 2; precioUnitario = 350.00 } | ConvertTo-Json)
$detalle | ConvertTo-Json

Titulo "3. GET /api/ordenes/$($orden.id) (total recalculado = 700.00)"
Invoke-RestMethod -Method Get -Uri "$base/api/ordenes/$($orden.id)" | ConvertTo-Json

Titulo "4. GET /api/ordenes (listar)"
Invoke-RestMethod -Method Get -Uri "$base/api/ordenes" | ConvertTo-Json

Titulo "5. PUT /api/ordenes/$($orden.id) (actualizar estado a PAGADA)"
Invoke-RestMethod -Method Put -Uri "$base/api/ordenes/$($orden.id)" `
    -ContentType "application/json" `
    -Body (@{ codigo = $codigo; cliente = "Juan Perez Quispe"; estado = "PAGADA" } | ConvertTo-Json) | ConvertTo-Json

Titulo "6. GET /api/orden-detalles?ordenId=$($orden.id) (filtro por orden)"
Invoke-RestMethod -Method Get -Uri "$base/api/orden-detalles?ordenId=$($orden.id)" | ConvertTo-Json

Titulo "7. Validacion HTTP 400 (orden sin cliente y estado invalido)"
try {
    Invoke-RestMethod -Method Post -Uri "$base/api/ordenes" -ContentType "application/json" `
        -Body '{"codigo":"ORD-BAD","estado":"XXX"}' | Out-Null
    Write-Host "FALLO: se esperaba HTTP 400" -ForegroundColor Red
} catch {
    MostrarError $_ 400
}

Titulo "8. Validacion HTTP 400 (detalle con cantidad 0)"
try {
    Invoke-RestMethod -Method Post -Uri "$base/api/orden-detalles" -ContentType "application/json" `
        -Body (@{ ordenId = $orden.id; idProducto = 1; cantidad = 0; precioUnitario = 10 } | ConvertTo-Json) | Out-Null
    Write-Host "FALLO: se esperaba HTTP 400" -ForegroundColor Red
} catch {
    MostrarError $_ 400
}

Titulo "9. Recurso inexistente HTTP 404"
try {
    Invoke-RestMethod -Method Get -Uri "$base/api/ordenes/999999" | Out-Null
    Write-Host "FALLO: se esperaba HTTP 404" -ForegroundColor Red
} catch {
    MostrarError $_ 404
}

Titulo "10. Codigo duplicado HTTP 409"
try {
    Invoke-RestMethod -Method Post -Uri "$base/api/ordenes" -ContentType "application/json" `
        -Body (@{ codigo = $codigo; cliente = "Otro cliente"; estado = "CREADA" } | ConvertTo-Json) | Out-Null
    Write-Host "FALLO: se esperaba HTTP 409" -ForegroundColor Red
} catch {
    MostrarError $_ 409
}

Titulo "11. DELETE /api/orden-detalles/$($detalle.id) (total vuelve a 0.00)"
Invoke-RestMethod -Method Delete -Uri "$base/api/orden-detalles/$($detalle.id)"
Invoke-RestMethod -Method Get -Uri "$base/api/ordenes/$($orden.id)" | ConvertTo-Json

Titulo "12. DELETE /api/ordenes/$($orden.id)"
Invoke-RestMethod -Method Delete -Uri "$base/api/ordenes/$($orden.id)"
Write-Host "Orden eliminada. Ordenes restantes:" (Invoke-RestMethod -Method Get -Uri "$base/api/ordenes").Count

Write-Host ""
Write-Host "CRUD verificado correctamente en $base" -ForegroundColor Green
