package pe.edu.upeu.gateway;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

	@GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
	public String home() {
		return """
			<!DOCTYPE html>
			<html lang="es">
			<head>
			  <meta charset="utf-8"/>
			  <title>ChaskaWear · Gateway</title>
			  <style>
			    body { font-family: Segoe UI, sans-serif; margin: 0; background: #1a120e; color: #f4ece6; }
			    header { background: #8b3a1a; padding: 22px 28px; }
			    h1 { margin: 0 0 6px; font-size: 28px; }
			    .clock { font-size: 18px; opacity: .95; }
			    main { padding: 24px 28px; max-width: 980px; }
			    .grid { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; }
			    a.card { display: block; background: #2a1c16; border: 1px solid #5c3a2a; border-radius: 10px; padding: 16px; color: #f4ece6; text-decoration: none; }
			    a.card:hover { border-color: #c45c26; }
			  </style>
			</head>
			<body>
			  <header>
			    <h1>ChaskaWear</h1>
			    <div class="clock"><span id="now"></span> · Equipo 12</div>
			  </header>
			  <main>
			    <div class="grid">
			      <a class="card" href="/api/v1/categorias"><b>Catálogo · categorías</b><br/>GET /api/v1/categorias</a>
			      <a class="card" href="/api/v1/productos"><b>Catálogo · productos</b><br/>GET /api/v1/productos</a>
			      <a class="card" href="/api/v1/ordenes"><b>Órdenes</b><br/>GET /api/v1/ordenes</a>
			      <a class="card" href="/api/v1/stocks"><b>Inventario · stock</b><br/>GET /api/v1/stocks</a>
			      <a class="card" href="/api/v1/pagos"><b>Pagos</b><br/>GET /api/v1/pagos</a>
			      <a class="card" href="/api/v1/usuarios"><b>Auth · usuarios</b><br/>GET /api/v1/usuarios</a>
			      <a class="card" href="/api/v1/avisos"><b>Notificaciones</b><br/>GET /api/v1/avisos</a>
			      <a class="card" href="/actuator/health"><b>Health del Gateway</b><br/>/actuator/health</a>
			      <a class="card" href="http://localhost:17761"><b>Eureka</b><br/>instancias registradas</a>
			      <a class="card" href="http://localhost:18090/targets"><b>Prometheus · Targets</b><br/>microservicios activos</a>
			      <a class="card" href="http://localhost:12000"><b>Grafana</b><br/>tableros en vivo</a>
			      <a class="card" href="http://localhost:17888/actuator/health"><b>Config Server</b><br/>puerto 17888</a>
			    </div>
			  </main>
			  <script>
			    const el = document.getElementById('now');
			    const tick = () => el.textContent = new Date().toLocaleString('es-PE');
			    tick();
			    setInterval(tick, 1000);
			  </script>
			</body>
			</html>
			""";
	}
}
