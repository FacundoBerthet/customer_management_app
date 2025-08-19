# =============================================================
# Dockerfile multi-stage para el frontend (Vite + Nginx)
# -------------------------------------------------------------
# Etapa 1: build de la app (Node)
# Etapa 2: runtime liviano (Nginx) sirviendo estáticos + proxy /api
# =============================================================

# -------------------------------
# Etapa 1: Builder (Node)
# -------------------------------
FROM node:20-alpine AS builder
WORKDIR /app

# Instalo dependencias usando package-lock para builds determinísticos
COPY package.json package-lock.json ./
RUN npm ci

# Copio el resto del código y hago el build
COPY . .
# Fuerzo base de API relativa para el build de producción detrás de Nginx
ENV VITE_API_URL=/api
RUN npm run build

# -------------------------------
# Etapa 2: Runtime (Nginx)
# -------------------------------
FROM nginx:alpine

# Configuración de Nginx (reverse proxy /api -> app:8080 y fallback SPA)
COPY nginx/default.conf /etc/nginx/conf.d/default.conf

# Copio el build de Vite a la carpeta pública de Nginx
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

# Nginx ya define CMD por defecto
