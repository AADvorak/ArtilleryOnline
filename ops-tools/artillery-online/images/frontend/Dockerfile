# Stage 1: Build
FROM node:24.2-alpine3.22 AS build
WORKDIR /frontend
COPY ../../frontend/package.json ../../frontend/package-lock.json* ./
RUN npm install
COPY ../../frontend/ .
RUN npm run build

# Stage 2: Run
FROM node:24.2-alpine3.22
WORKDIR /frontend
COPY --from=build /frontend/.output ./.output
CMD ["node", ".output/server/index.mjs"]
