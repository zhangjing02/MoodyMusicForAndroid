# Java 版本配置指南

## 当前状态

✅ **已配置**: Java 17
✅ **构建状态**: 成功
✅ **兼容性**: 所有依赖库都支持 Java 17

---

## Java 版本对比

### Java 17 (LTS) - 当前使用
- **发布**: 2021年9月
- **支持至**: 2029年
- **稳定性**: ⭐⭐⭐⭐⭐
- **兼容性**: 最广泛支持
- **推荐**: 生产环境首选

### Java 21 (LTS) - 最新版本
- **发布**: 2023年9月
- **支持至**: 2031年
- **稳定性**: ⭐⭐⭐⭐⭐
- **兼容性**: 新项目友好
- **推荐**: 新项目首选

---

## 如何升级到 Java 21

### 步骤1：安装 JDK 21

#### 选项A：Oracle JDK 21
1. 访问：https://www.oracle.com/java/technologies/downloads/#java21
2. 下载 Windows x64 Installer
3. 运行安装程序

#### 选项B：Eclipse Temurin (OpenJDK) - 推荐
1. 访问：https://adoptium.net/temurin/releases/?version=21
2. 下载 JDK 21 for Windows x64
3. 解压或安装

#### 选项C：使用包管理器
```bash
# 使用 winget
winget install EclipseAdoptium.Temurin.21.JDK

# 使用 Chocolatey
choco install temurin21
```

### 步骤2：配置环境变量

#### Windows
1. 打开"系统环境变量"设置
2. 新建系统变量：
   - 变量名：`JAVA_HOME`
   - 变量值：`C:\Program Files\Eclipse Adoptium\jdk-21.x.x-hotspot`（安装路径）
3. 编辑 PATH 变量，添加：
   - `%JAVA_HOME%\bin`

#### 验证安装
```bash
java -version
# 应该显示：
# openjdk version "21.x.x" 2023-xx-xx
# OpenJDK Runtime Environment ...
```

### 步骤3：更新项目配置

#### 方式1：简单配置
在 `commonbase/build.gradle.kts` 和 `app/build.gradle.kts` 中：
```kotlin
compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlinOptions {
    jvmTarget = "21"
}
```

#### 方式2：使用 Toolchain（推荐）
在项目根目录的 `build.gradle.kts` 中添加：
```kotlin
allprojects {
    plugins.withType<JavaPlugin>().configureEach {
        java {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "21"
        }
    }
}
```

或者在模块的 `build.gradle.kts` 中：
```kotlin
kotlin {
    jvmToolchain(21)
}
```

### 步骤4：验证构建
```bash
gradlew clean build
```

---

## Java 17 vs Java 21 对比

### 性能对比
| 指标 | Java 17 | Java 21 |
|------|---------|---------|
| 启动速度 | 基准 | 快 5-10% |
| 内存占用 | 基准 | 低 5-15% |
| 吞吐量 | 基准 | 高 3-8% |

### 新特性对比

#### Java 17 新特性
- ✅ Sealed Classes
- ✅ Pattern Matching for instanceof
- ✅ Text Blocks
- ✅ Records
- ✅ Switch Expressions

#### Java 21 新增特性
- ✅ **Virtual Threads** (虚拟线程) - 重大改进
- ✅ Record Patterns
- ✅ Pattern Matching for switch
- ✅ String Templates (预览)
- ✅ Sequenced Collections
- ✅ Structured Concurrency (预览)

### 对 Kotlin 的影响

即使你使用 Kotlin，Java 版本也很重要：

| 方面 | Java 17 | Java 21 |
|------|---------|---------|
| Kotlin 编译 | 完全支持 | 完全支持 |
| JVM 字节码 | 优秀的性能 | 更优的性能 |
| 协程支持 | 很好 | 更好（虚拟线程） |

---

## 推荐

### 使用 Java 17 的理由
- ✅ 当前配置，构建成功
- ✅ 最广泛的支持
- ✅ 稳定可靠
- ✅ 所有依赖库测试过
- ✅ 生产环境首选

### 使用 Java 21 的理由
- ✅ 更长的支持期（至 2031 年）
- ✅ 虚拟线程（重大性能提升）
- ✅ 更新更好的特性
- ✅ 面向未来

---

## 兼容性矩阵

| 依赖库 | Java 17 | Java 21 |
|--------|---------|---------|
| AGP 8.10.1 | ✅ | ✅ |
| Kotlin 2.1.0 | ✅ | ✅ |
| Retrofit 2.11.0 | ✅ | ✅ |
| OkHttp 4.12.0 | ✅ | ✅ |
| Gson 2.11.0 | ✅ | ✅ |
| AndroidX 库 | ✅ | ✅ |
| Glide 4.16.0 | ✅ | ✅ |
| EventBus 3.3.1 | ✅ | ✅ |
| Room 2.6.1 | ✅ | ✅ |

---

## 常见问题

### Q1: 我应该升级到 Java 21 吗？
**A**:
- 如果是**生产项目**：建议保持 Java 17（稳定）
- 如果是**新项目**：推荐 Java 21（面向未来）
- 如果需要**虚拟线程**：必须 Java 21

### Q2: Java 17 会过时吗？
**A**: 不会，Java 17 是 LTS 版本，支持到 2029 年

### Q3: 升级会影响我的代码吗？
**A**: 不会，所有现有代码都能正常运行

### Q4: 如何检查当前 JDK 版本？
**A**:
```bash
java -version
# 在项目中
gradlew -version
```

### Q5: 可以混用 Java 17 和 Java 21 吗？
**A**: 不推荐，整个项目应该使用相同的 Java 版本

---

## 总结

### 当前配置（推荐用于生产）
- ✅ Java 17
- ✅ 稳定可靠
- ✅ 构建成功

### 升级到 Java 21（可选）
- 📦 安装 JDK 21
- 🔧 更新环境变量
- 📝 修改 build.gradle.kts
- ✅ 验证构建

---

## 快速升级命令

如果你想立即升级到 Java 21：

1. 安装 JDK 21
```bash
winget install EclipseAdoptium.Temurin.21.JDK
```

2. 验证安装
```bash
java -version
```

3. 修改配置文件（已在上面说明）

4. 构建
```bash
gradlew clean build
```

---

## 参考链接

- [Java 17 发布说明](https://openjdk.org/projects/jdk/17/)
- [Java 21 发布说明](https://openjdk.org/projects/jdk/21/)
- [Android Java 版本要求](https://developer.android.com/build/jdks)
- [Kotlin JVM 目标版本](https://kotlinlang.org/docs/gradle-configure-project.html#jvm-target-version)
