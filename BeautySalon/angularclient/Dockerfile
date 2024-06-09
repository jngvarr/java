FROM node:latest-alpine as build
WORKDIR /usr/app/front
COPY package.json package-lock.json ./
RUN npm install
COPY . .

RUN npm run build --prod
