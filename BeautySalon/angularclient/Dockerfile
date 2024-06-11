FROM node:latest-alpine as build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm install
COPY . .

RUN npm run build --prod
<<<<<<< HEAD
FROM nginx:alpine
COPY --from=build /app/dist/my-angular-app /usr/share/nginx/html
=======

# Используем образ Nginx для сервировки статических файлов
FROM nginx:latest-alpine

# Копируем собранные файлы из предыдущего этапа в директорию Nginx для статических файлов
COPY --from=build /app/dist/your-angular-app /usr/share/nginx/html

# Открываем порт 80 для Nginx
EXPOSE 80
>>>>>>> 4226a79a7517be55c1bd6fff565d398d79be26ae
