FROM node:latest-alpine as build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm install
COPY . .

RUN npm run build --prod
FROM nginx:alpine
COPY --from=build /app/dist/my-angular-app /usr/share/nginx/html
