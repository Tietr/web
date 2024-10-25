# 使用 Maven 作为基础镜像
FROM maven:3.8.5-openjdk-17 AS build

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 和源代码
COPY pom.xml .
COPY src ./src

# 构建项目
RUN mvn clean package -DskipTests

# 使用 OpenJDK 作为运行时基础镜像
FROM openjdk:24-ea-17-jdk-slim-bullseye

# 复制构建好的 jar 文件
COPY --from=build /app/target/*.jar app.jar

# 设置容器启动时的命令
ENTRYPOINT ["java", "-jar", "/app.jar"]
