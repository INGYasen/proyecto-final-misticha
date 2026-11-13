$Host.UI.RawUI.WindowTitle = "JWT Raymi"
Write-Host "JWT - Gateway"
Write-Host ""

Write-Host "Sin token"
try {
  $null = Invoke-WebRequest "http://localhost:7091/api/v1/clientes/me" -UseBasicParsing -TimeoutSec 8
  Write-Host "200"
} catch {
  $code = $null
  try { $code = [int]$_.Exception.Response.StatusCode } catch {}
  if ($code) {
    Write-Host $code
  } else {
    Write-Host "gateway no responde"
    Write-Host $_.Exception.Message
    exit 1
  }
}

Write-Host ""
Write-Host "Token Keycloak (cliente1)"
try {
  $tok = Invoke-RestMethod -Method Post `
    -Uri "http://localhost:9091/realms/raymisupreme-dev/protocol/openid-connect/token" `
    -ContentType "application/x-www-form-urlencoded" `
    -Body "grant_type=password&client_id=raymisupreme-app&username=cliente1&password=cliente1" `
    -TimeoutSec 15
} catch {
  Write-Host "keycloak no dio token"
  Write-Host $_.Exception.Message
  exit 1
}

Write-Host ""
Write-Host "Con token"
try {
  $r = Invoke-WebRequest "http://localhost:7091/api/v1/clientes/me" `
    -Headers @{ Authorization = "Bearer $($tok.access_token)" } `
    -UseBasicParsing -TimeoutSec 10
  Write-Host $r.StatusCode
  Write-Host $r.Content
} catch {
  $code = $null
  try { $code = [int]$_.Exception.Response.StatusCode } catch {}
  if ($code) {
    Write-Host $code
  } else {
    Write-Host "gateway no responde"
    Write-Host $_.Exception.Message
  }
}
