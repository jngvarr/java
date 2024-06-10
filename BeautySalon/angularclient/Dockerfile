FROM node:latest-alpine as build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm install
COPY . .

RUN npm run build --prod

# Используем образ Nginx для сервировки статических файлов
FROM nginx:latest-alpine

# Копируем собранные файлы из предыдущего этапа в директорию Nginx для статических файлов
COPY --from=build /app/dist/your-angular-app /usr/share/nginx/html

# Открываем порт 80 для Nginx
EXPOSE 80
