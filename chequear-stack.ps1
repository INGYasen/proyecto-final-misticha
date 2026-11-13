$Host.UI.RawUI.WindowTitle = "Chequeo Raymi"
Write-Host "Puertos"
Write-Host ""

$lista = @(
  @{ p=7071; n='config' },
  @{ p=7081; n='eureka' },
  @{ p=7091; n='gateway' },
  @{ p=8086; n='acopio' },
  @{ p=8087; n='inventario' },
  @{ p=8088; n='ventas' },
  @{ p=8089; n='auth' },
  @{ p=8092; n='pagos' },
  @{ p=8093; n='carrito' },
  @{ p=8094; n='catalogo' },
  @{ p=8194; n='catalogo2' },
  @{ p=9091; n='keycloak' },
  @{ p=19090; n='prometheus' },
  @{ p=13000; n='grafana' },
  @{ p=8085; n='kafka-ui' },
  @{ p=4200; n='angular' }
)

foreach ($x in $lista) {
  $ok = $false
  try {
    $c = New-Object Net.Sockets.TcpClient
    $a = $c.BeginConnect('127.0.0.1', [int]$x.p, $null, $null)
    $ok = $a.AsyncWaitHandle.WaitOne(700)
    if ($ok) { $c.EndConnect($a) }
    $c.Close()
  } catch { $ok = $false }
  if ($ok) {
    Write-Host ("{0,-12} :{1}  ok" -f $x.n, $x.p)
  } else {
    Write-Host ("{0,-12} :{1}  caido" -f $x.n, $x.p)
  }
}

Write-Host ""
Write-Host "Eureka"
try {
  $apps = Invoke-RestMethod http://localhost:7081/eureka/apps -Headers @{Accept='application/json'} -TimeoutSec 8
  foreach ($app in $apps.applications.application) {
    $n = @($app.instance).Count
    Write-Host ("- {0}: {1}" -f $app.name, $n)
  }
} catch {
  Write-Host "eureka caido"
}
