# Multi-Stage-Build: Schritt 1 baut den Vite-Output, Schritt 2 serviert via nginx.
# SPA-Fallback ist in nginx.conf konfiguriert (try_files ... /index.html), damit
# direkte URL-Aufrufe (T075 AC) auf React Router treffen statt 404 zu liefern.

# ── build stage ──────────────────────────────────────────────────────────────
FROM node:20-alpine AS build
WORKDIR /app

COPY package.json package-lock.json* ./
RUN npm ci

COPY . .
RUN npm run build

# ── serve stage ──────────────────────────────────────────────────────────────
FROM nginx:1.27-alpine AS serve

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

# Liveness: nginx muss auf / antworten. wget ist in nginx:alpine vorinstalliert.
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget -q --spider http://localhost/ || exit 1

CMD ["nginx", "-g", "daemon off;"]
