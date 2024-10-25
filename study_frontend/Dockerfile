# 使用 Node.js 作为基础镜像
FROM node:18.20.4 AS build

# 设置工作目录
WORKDIR /app

# 复制 package.json 和 package-lock.json
COPY package*.json ./

# 安装依赖
RUN npm install

# 复制源代码
COPY . .

# 构建项目
RUN npm run build

# 使用 Nginx 作为运行时基础镜像
FROM nginx:mainline-alpine3.20-slim

# 复制构建好的静态文件
COPY --from=build /app/dist /usr/share/nginx/html

# 暴露 Nginx 端口
EXPOSE 80

# 启动 Nginx
CMD ["nginx", "-g", "daemon off;"]
