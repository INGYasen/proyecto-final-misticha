import json
import urllib.parse
import urllib.request
from pathlib import Path

from PIL import Image

SHEET = Path(r"C:\Users\yasen\.cursor\projects\c-Users-yasen-Downloads-plusdistribuidas\assets\c__Users_yasen_AppData_Roaming_Cursor_User_workspaceStorage_bf817e90de4654d961e8ecc45b68e623_images_4fb1b825-5f7e-4025-860e-ac9574235cb6-799e134e-0fff-48c3-8798-517f5f756282.jpg")
OUT = Path(r"c:\Users\yasen\Downloads\plusdistribuidas\fronted\src\assets\catalogo")
GW = "http://localhost:7091"
KC = "http://localhost:9091"

BOUNDS = [0, 85, 164, 245, 324, 399, 473, 553, 628, 708, 791, 864, 942, 1024]
# Photo area only: leave out the caption under each garment.
BANDS = [(26, 118), (145, 240), (292, 386), (413, 500), (551, 632), (683, 782)]

ROWS = [
    ("MUJER", [
        "Chalina clásica", "Poncho andino", "Casaca tejida", "Chompa andina", "Cardigan",
        "Blusa bordada", "Blusa cuello V", "Top bordado", "Crop top", "Vestido andino",
        "Traje formal", "Falda corta", "Falda larga",
    ]),
    ("MUJER", [
        "Pantalón palazzo", "Pantalón recto", "Pantalón cargo", "Short andino",
        "Conjunto top y falda", "Pantalón lino", "Traje de baño", "Kimono andino",
        "Sombrero", "Gorro andino", "Bolso tejido", "Zapatillas andinas", "Sandalias andinas",
    ]),
    ("VARON", [
        "Chompa", "Casaca", "Poncho", "Chalina", "Polo camiseta", "Camisa andina",
        "Camisa lino", "Polera", "Polerón", "Conjunto", "Traje formal", "Bermuda", "Pantalón cargo",
    ]),
    ("VARON", [
        "Pantalón recto", "Pantalón palazzo", "Pantalón drill", "Short andino", "Chaleco",
        "Sombrero", "Gorro andino", "Zapatillas andinas", "Sandalias", "Bolso cruzado",
        "Mochila andina", "Gafas", "Cinturón andino",
    ]),
    ("NINOS", [
        "Chompa", "Casaca", "Poncho", "Polera", "Camisa andina", "Conjunto polo y short",
        "Vestido", "Falda", "Pantalón", "Short", "Gorro", "Sombrero", "Zapatillas",
    ]),
    ("UNISEX", [
        "Chalina", "Poncho", "Chompa oversize", "Casaca bomber", "Polerón hoodie",
        "Conjunto deportivo", "Pantalón jogger", "Pantalón recto", "Short", "Sombrero",
        "Gorro", "Zapatillas andinas", "Bolso tote",
    ]),
]

AUDIENCE = {"MUJER": "Mujer", "VARON": "Varón", "NINOS": "Niños", "UNISEX": "Unisex"}
TALLA = {"MUJER": "M", "VARON": "L", "NINOS": "XS", "UNISEX": "M"}
PRECIO = {
    "Chalinas": 89, "Ponchos": 179, "Casacas": 199, "Chompas": 159, "Blusas": 79,
    "Vestidos": 189, "Faldas": 99, "Pantalones": 129, "Shorts": 79, "Conjuntos": 249,
    "Trajes de baño": 95, "Sombreros": 69, "Gorros": 59, "Calzado": 169, "Bolsos": 99,
    "Accesorios": 49, "Camisas": 89, "Chalecos": 119,
}


def categoria(label: str) -> str:
    n = label.lower()
    rules = [
        ("baño", "Trajes de baño"),
        ("zapat", "Calzado"),
        ("sandalia", "Calzado"),
        ("bolso", "Bolsos"),
        ("mochila", "Bolsos"),
        ("gafa", "Accesorios"),
        ("cintur", "Accesorios"),
        ("sombrero", "Sombreros"),
        ("gorro", "Gorros"),
        ("kimono", "Chalinas"),
        ("chalina", "Chalinas"),
        ("poncho", "Ponchos"),
        ("casaca", "Casacas"),
        ("bomber", "Casacas"),
        ("chaleco", "Chalecos"),
        ("vestido", "Vestidos"),
        ("falda", "Faldas"),
        ("short", "Shorts"),
        ("bermuda", "Shorts"),
        ("pantal", "Pantalones"),
        ("jogger", "Pantalones"),
        ("conjunto", "Conjuntos"),
        ("traje", "Conjuntos"),
        ("blusa", "Blusas"),
        ("crop", "Blusas"),
        ("top", "Blusas"),
        ("camisa", "Camisas"),
        ("polo", "Camisas"),
        ("cardigan", "Chompas"),
        ("poler", "Chompas"),
        ("hoodie", "Chompas"),
        ("chompa", "Chompas"),
        ("oversize", "Chompas"),
    ]
    for key, cat in rules:
        if key in n:
            return cat
    return "Accesorios"


def color_de(label: str, i: int) -> str:
    n = label.lower()
    if any(k in n for k in ("negro",)):
        return "Negro"
    palette = ["Rojo", "Terracota", "Beige", "Azul", "Blanco", "Ocre", "Negro", "Verde"]
    return palette[i % len(palette)]


def material_de(cat: str) -> str:
    if cat in ("Blusas", "Camisas", "Vestidos"):
        return "Bordado artesanal"
    if cat in ("Calzado", "Bolsos", "Accesorios", "Sombreros"):
        return "Tejido andino"
    if cat in ("Pantalones", "Shorts"):
        return "Algodón"
    return "Lana de alpaca"


def api(method, url, token=None, body=None):
    data = None
    headers = {}
    if token:
        headers["Authorization"] = f"Bearer {token}"
    if body is not None:
        data = json.dumps(body).encode("utf-8")
        headers["Content-Type"] = "application/json"
    req = urllib.request.Request(url, data=data, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req, timeout=40) as res:
            raw = res.read().decode("utf-8")
            return json.loads(raw) if raw else None
    except urllib.error.HTTPError as ex:
        detail = ex.read().decode("utf-8", "replace")
        raise RuntimeError(f"{method} {url} -> {ex.code} {detail[:300]}") from ex


def token():
    form = urllib.parse.urlencode({
        "grant_type": "password",
        "client_id": "raymisupreme-app",
        "client_secret": "vgDLRwz4jzwV26pxGQu1aB11Yoa1VvuA",
        "username": "admin1",
        "password": "admin1",
    }).encode()
    req = urllib.request.Request(
        f"{KC}/realms/raymisupreme-dev/protocol/openid-connect/token",
        data=form,
        headers={"Content-Type": "application/x-www-form-urlencoded"},
    )
    with urllib.request.urlopen(req, timeout=30) as res:
        return json.loads(res.read().decode())["access_token"]


def main():
    OUT.mkdir(parents=True, exist_ok=True)
    im = Image.open(SHEET).convert("RGB")
    products = []
    seen = {}
    n = 0
    for row, (publico, labels) in enumerate(ROWS):
        y0, y1 = BANDS[row]
        for col, label in enumerate(labels):
            n += 1
            base = label
            key = base.lower()
            if key in seen:
                nombre = f"{base} · {AUDIENCE[publico]}"
            else:
                nombre = base
                seen[key] = publico
            cat = categoria(label)
            crop = im.crop((BOUNDS[col] + 2, y0, BOUNDS[col + 1] - 2, y1))
            crop = crop.resize((crop.width * 3, crop.height * 3), Image.Resampling.LANCZOS)
            filename = f"{n:02d}.jpg"
            crop.save(OUT / filename, quality=90)
            products.append({
                "nombre": nombre,
                "publico": publico,
                "categoria": cat,
                "foto": f"assets/catalogo/{filename}",
                "talla": TALLA[publico],
                "color": color_de(label, n),
                "material": material_de(cat),
                "stock": 6 + (n % 10),
                "precio": PRECIO[cat],
                "codigo": f"CAT-{n:03d}",
            })
    print("crops", len(products))

    access = token()
    cats = api("GET", f"{GW}/api/catalogo/public/categorias")
    by_name = {c["nombre"]: c["id"] for c in cats}
    for name in sorted({p["categoria"] for p in products}):
        if name not in by_name:
            created = api("POST", f"{GW}/api/v1/categorias", access, {
                "nombre": name,
                "descripcion": name,
            })
            by_name[name] = created["idCategoria"]
            print("categoria", name, created["idCategoria"])

    items = api("GET", f"{GW}/api/v1/catalogo/items", access) or []
    for item in items:
        api("PUT", f"{GW}/api/v1/catalogo/items/{item['idItem']}", access, {
            "idPrenda": item["idPrenda"],
            "urlFoto": item["urlFoto"],
            "descripcionPublica": "Fuera del catalogo actual",
            "destacado": False,
            "activo": False,
        })
    print("ocultos", len(items))

    for p in products:
        prenda = api("POST", f"{GW}/api/v1/prendas", access, {
            "idLote": 1,
            "idCategoria": by_name[p["categoria"]],
            "publico": p["publico"],
            "codigoUnico": p["codigo"],
            "nombrePrenda": p["nombre"],
            "talla": p["talla"],
            "color": p["color"],
            "material": p["material"],
            "stock": p["stock"],
            "urlFoto": p["foto"],
        })
        api("POST", f"{GW}/api/v1/precios/prendas", access, {
            "prendaId": prenda["idPrenda"],
            "precioBase": p["precio"],
        })
        api("POST", f"{GW}/api/v1/catalogo/items", access, {
            "idPrenda": prenda["idPrenda"],
            "urlFoto": p["foto"],
            "descripcionPublica": f"{p['nombre']} de la línea {AUDIENCE[p['publico']].lower()}.",
            "destacado": True,
            "activo": True,
        })
        print("ok", p["publico"], p["categoria"], p["nombre"])

    publicos = api("GET", f"{GW}/api/catalogo/public/productos")
    print("publicos", len(publicos))


if __name__ == "__main__":
    main()
