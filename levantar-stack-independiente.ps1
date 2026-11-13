# Levanta Raymi Supreme en ventanas PowerShell aparte (siguen si cierras Cursor).
$ErrorActionPreference = 'Continue'
$Root = $PSScriptRoot
$Back = Join-Path $Root 'raymisupreme-back'

$Mvn = $null
foreach ($c in @(
  (Get-Command mvn.cmd -ErrorAction SilentlyContinue | Select-Object -ExpandProperty Source),
  (Get-Command mvn -ErrorAction SilentlyContinue | Select-Object -ExpandProperty Source),
  (Join-Path $env:USERPROFILE 'tools\apache-maven-3.9.9\bin\mvn.cmd')
)) {
  if ($c -and (Test-Path $c)) { $Mvn = $c; break }
}
if (-not $Mvn) {
  Write-Host 'No se encontro Maven (mvn). Ponlo en el PATH o en %USERPROFILE%\tools\apache-maven-3.9.9\bin'
  exit 1
}

$Java = $env:JAVA_HOME
if (-not $Java -or -not (Test-Path $Java)) {
  $Java = 'C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot'
}
if (-not (Test-Path $Java)) {
  Write-Host 'No se encontro JDK 17. Define JAVA_HOME.'
  exit 1
}

function Start-IndepWindow {
  param(
    [string]$Title,
    [string]$WorkDir,
    [string]$Command
  )
  $ps = @"
`$Host.UI.RawUI.WindowTitle = '$Title'
Write-Host '=== $Title ===' -ForegroundColor Cyan
Set-Location '$WorkDir'
`$env:JAVA_HOME = '$Java'
`$env:Path = '$Java\bin;' + `$env:Path
$Command
"@
  $encoded = [Convert]::ToBase64String([Text.Encoding]::Unicode.GetBytes($ps))
  Start-Process -FilePath 'powershell.exe' -ArgumentList @(
    '-NoExit',
    '-ExecutionPolicy', 'Bypass',
    '-EncodedCommand', $encoded
  ) | Out-Null
  Write-Host "OK ventana: $Title"
}

Write-Host '1) Red Docker ms-net' -ForegroundColor Yellow
docker network create ms-net 2>$null | Out-Null

Write-Host '2) MySQL + Keycloak (Docker)' -ForegroundColor Yellow
$composeDirs = @(
  (Join-Path $Back 'services\ms-acopio'),
  (Join-Path $Back 'services\ms-inventario'),
  (Join-Path $Back 'services\ms-ventas'),
  (Join-Path $Back 'services\ms-catalogo'),
  (Join-Path $Back 'services\ms-pagos'),
  (Join-Path $Back 'services\ms-carrito'),
  (Join-Path $Back 'services\ms-auth')
)
foreach ($d in $composeDirs) {
  $f = Join-Path $d 'docker-compose-dev.yml'
  if (Test-Path $f) {
    Write-Host "  compose $d"
    docker compose -f $f up -d
  }
}

Write-Host '3) Observabilidad + Kafka (Docker)' -ForegroundColor Yellow
docker compose -f (Join-Path $Back 'obs\docker-compose-dev.yml') up -d

Write-Host 'Esperando MySQL/Keycloak ~25s...' -ForegroundColor DarkYellow
Start-Sleep -Seconds 25

Write-Host '4) Microservicios Spring (ventanas independientes)' -ForegroundColor Yellow
$apps = @(
  @{ Title = 'Raymi-config-server'; Dir = "$Back\infra\config-server"; Wait = 18 },
  @{ Title = 'Raymi-eureka'; Dir = "$Back\infra\registry-server"; Wait = 20 },
  @{ Title = 'Raymi-ms-auth'; Dir = "$Back\services\ms-auth"; Wait = 8 },
  @{ Title = 'Raymi-ms-acopio'; Dir = "$Back\services\ms-acopio"; Wait = 6 },
  @{ Title = 'Raymi-ms-inventario'; Dir = "$Back\services\ms-inventario"; Wait = 6 },
  @{ Title = 'Raymi-ms-catalogo-8094'; Dir = "$Back\services\ms-catalogo"; Wait = 6; Extra = '' },
  @{ Title = 'Raymi-ms-catalogo-8194'; Dir = "$Back\services\ms-catalogo"; Wait = 4; Extra = '"-Dspring-boot.run.arguments=--server.port=8194"' },
  @{ Title = 'Raymi-ms-carrito'; Dir = "$Back\services\ms-carrito"; Wait = 6 },
  @{ Title = 'Raymi-ms-ventas'; Dir = "$Back\services\ms-ventas"; Wait = 6 },
  @{ Title = 'Raymi-ms-pagos'; Dir = "$Back\services\ms-pagos"; Wait = 6 },
  @{ Title = 'Raymi-gateway'; Dir = "$Back\infra\gateway"; Wait = 10 }
)

foreach ($app in $apps) {
  $extra = ''
  if ($app.ContainsKey('Extra') -and $app.Extra) { $extra = ' ' + $app.Extra }
  $cmd = "& '$Mvn' spring-boot:run -DskipTests$extra"
  Start-IndepWindow -Title $app.Title -WorkDir $app.Dir -Command $cmd
  Start-Sleep -Seconds $app.Wait
}

Write-Host '5) Frontend Angular (ventana independiente)' -ForegroundColor Yellow
Start-IndepWindow -Title 'Raymi-angular' -WorkDir (Join-Path $Root 'fronted') -Command 'npx ng serve --port 4200 --host localhost'

Write-Host ''
Write-Host 'Listo. Las ventanas PowerShell quedan fuera de Cursor.' -ForegroundColor Green
Write-Host 'Espera 1-2 min y revisa: Eureka :7081 | Grafana :13000 | Tienda :4200'
Write-Host 'Si ms-pagos falla por token MP, en esa ventana define MP_ACCESS_TOKEN y reinicia mvn.'
