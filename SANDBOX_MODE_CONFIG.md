# 沙箱运行模式配置指南

## 📌 概述

该项目支持两种代码执行模式：
- **本地模式 (native)**: 直接在服务器上执行 Java 代码
- **Docker 模式 (docker)**: 在 Docker 容器中执行 Java 代码（更安全）

## 🔧 配置方法

### 1. 本地开发环境（默认）

**文件**: `application.yml`
```yaml
sandbox:
  mode: native  # 使用本地沙箱
```

**特点**:
- ✅ 无需 Docker
- ✅ 启动快速
- ❌ 安全隔离性差
- ❌ 恶意代码可能危害主机

### 2. Docker 环境（生产推荐）

**文件**: `application-prod.yml`
```yaml
sandbox:
  mode: docker  # 使用 Docker 沙箱
```

**启动方式**:
```bash
# 激活 prod 配置
java -jar app.jar --spring.profiles.active=prod
```

**特点**:
- ✅ 代码完全隔离
- ✅ 安全性高
- ✅ 资源可控
- ❌ 需要 Docker 环境
- ❌ 性能略低

## 🚀 使用方式

### 本地开发

```bash
# 编译
mvn clean compile

# 运行（默认使用 native 模式）
mvn spring-boot:run
```

### Docker 部署

```bash
# 构建镜像
docker build -t code-sandbox .

# 运行容器（自动使用 docker 模式）
docker run -p 8081:8081 code-sandbox
```

## 🔄 切换模式

### 临时切换（命令行）
```bash
# 强制使用 Docker 模式
java -jar app.jar --sandbox.mode=docker

# 强制使用本地模式
java -jar app.jar --sandbox.mode=native
```

### 环境变量切换
```bash
# 设置环境变量
export SANDBOX_MODE=docker
java -jar app.jar
```

## ⚠️ Docker 模式配置要求

### 前置条件
1. 安装 Docker（版本 >= 20.10）
2. 拉取 OpenJDK 镜像：
```bash
docker pull openjdk:8-alpine
```

3. 挂载卷权限（Linux）：
```bash
# 允许当前用户访问 Docker
sudo usermod -aG docker $USER
```

### 常见问题

**Q: Docker 模式报错"Cannot connect to Docker daemon"**
- A: 检查 Docker 是否运行：`docker ps`

**Q: 容器内找不到 Java**
- A: 确保拉取了正确的镜像：`docker pull openjdk:8-alpine`

**Q: 容器一直创建失败**
- A: 检查磁盘空间和内存是否充足

## 📊 性能对比

| 指标 | 本地模式 | Docker 模式 |
|------|--------|-----------|
| 启动速度 | 快 | 较慢 |
| 内存占用 | 低 | 中等 |
| 执行速度 | 快 | 稍慢 |
| 安全隔离 | ❌ 差 | ✅ 优 |
| 资源控制 | ❌ 无 | ✅ 完全 |

## 🎯 推荐方案

- **开发环境**: 使用 `native` 模式，快速迭代
- **测试环境**: 使用 `docker` 模式，验证隔离性
- **生产环境**: 使用 `docker` 模式，确保安全

## 📝 代码实现

配置通过 `@ConfigurationProperties` 实现：

```java
@ConfigurationProperties(prefix = "sandbox")
public static class SandboxProperties {
    private String mode = "native";  // 默认为 native
}
```

根据配置自动选择对应的实现：

```java
@Bean
public CodeSandBox codeSandBox(SandboxProperties properties) {
    if ("docker".equalsIgnoreCase(properties.getMode())) {
        return new JavaDockerCodeSandbox();
    } else {
        return new JavaNativeCodeSandbox();
    }
}
```
