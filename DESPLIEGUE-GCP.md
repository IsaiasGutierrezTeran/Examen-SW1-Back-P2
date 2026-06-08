# Despliegue en Google Cloud — Backend + Microservicio IA + MongoDB

Los 3 servicios se levantan juntos con el **`docker-compose.yml`** de este repo.
Funciona idéntico en tu PC y en la VM de Google Cloud.

```
<carpeta>/
├── 2dPBACK/                  ← ESTE repo (backend, :8080). El compose se corre desde aquí
│     └── docker-compose.yml
└── 2doPMicroservicio/        ← microservicio IA FastAPI (:8001), repo ExamenSWMS
```

Cableado interno (red `tramites_network` de Docker):
- `backend`  →  `ia.service.url = http://ia-service:8001`
- `backend`  →  `app.mongo.uri  = mongodb://mongodb:27017/tramites_db` (bean `MongoConfig`)
- `ia-service` → `mongodb:27017`

> **Mongo corre SIN autenticación a propósito.** Spring Boot 4.0.6 no aplica
> credenciales al driver e **ignora la config estándar de Mongo** (env/args). Por
> eso el backend trae un bean `MongoConfig` que arma la conexión desde la propiedad
> propia `app.mongo.uri`. Para cambiar de Mongo, edita ese valor en el compose.

---

## 1. Requisitos en la VM (una sola vez)

VM Debian/Ubuntu en Compute Engine. **Recomendado `e2-standard-2` (8 GB)** porque
TensorFlow del microservicio pide RAM (con `e2-medium`/4 GB puede quedar justo).

```bash
sudo apt-get update
sudo apt-get install -y ca-certificates curl git
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker $USER     # re-loguéate después
```

### Firewall (en la consola de GCP → VPC → Firewall)
- **tcp:8080** → backend (Swagger en `/swagger-ui/index.html`)
- **tcp:8001** → microservicio IA (opcional, solo para ver `/docs`)

```bash
gcloud compute firewall-rules create allow-8080 --allow tcp:8080 --source-ranges 0.0.0.0/0
gcloud compute firewall-rules create allow-8001 --allow tcp:8001 --source-ranges 0.0.0.0/0
```

---

## 2. Clonar ambos repos (nombres EXACTOS, como carpetas hermanas)

```bash
mkdir -p ~/examen && cd ~/examen
git clone https://github.com/IsaiasGutierrezTeran/Examen-SW1-Back-P2.git 2dPBACK
git clone https://github.com/IsaiasGutierrezTeran/ExamenSWMS.git        2doPMicroservicio
```

> ⚠️ Deben llamarse exactamente `2dPBACK` y `2doPMicroservicio` — el compose
> referencia `../2doPMicroservicio`.

---

## 3. Levantar todo

```bash
cd ~/examen/2dPBACK
docker compose up -d --build      # primera vez tarda (descarga TensorFlow + deps Gradle)
docker compose ps                 # los 3 en estado "Up"
docker compose logs -f backend    # mira el seeder poblando la BD
```

El backend **siembra la base automáticamente** al arrancar (`--app.seed.reset=true`
→ limpia y re-puebla en cada arranque, 32 colecciones incl. el motor de políticas
de negocio). No hay que correr ningún seeder a mano.

### Verificar
```bash
curl http://localhost:8001/healthz                 # IA: {"status":"ok"}
curl http://localhost:8080/swagger-ui/index.html   # backend
```
Desde fuera: `http://IP_PUBLICA:8080/swagger-ui/index.html` y `http://IP_PUBLICA:8001/docs`.

### Credenciales sembradas
| Email | Password | Rol |
|-------|----------|-----|
| `admin@cre.bo` | `admin12345` | Administrador |
| `funcionario@cre.bo` | `func12345` | Funcionario |
| `cliente@cre.bo` | `cliente12345` | Cliente |

---

## 4. Re-desplegar tras cambios
```bash
cd ~/examen/2dPBACK && git pull          # (o ~/examen/2doPMicroservicio)
docker compose up -d --build backend     # rebuild solo del servicio cambiado
```

## 5. Producción
- Cambia `--app.seed.reset=true` → `false` en el compose cuando NO quieras que
  borre/repueble la BD en cada reinicio.
- Cierra el puerto 8001 en el firewall si no necesitas `/docs`.
- Para HTTPS, pon Nginx/Caddy delante del 8080.
