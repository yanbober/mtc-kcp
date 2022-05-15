# mtc-kcp
一个基于 Kotlin Compiler Plugin 实现的迷你方法耗时无侵入编译插件。也是一个用来学习 KCP 编写的好案例。

默认 kotlin 中对一段代码或方法的耗时计算提供了内置 inline 函数，我们使用方式如下（需要修改代码逻辑）：
```kotlin
val timeCost = measureTimeMillis {
    //logic
}
print("time cost is: $timeCost")
```
此系列方法官方的实现源码如下：
```kotlin
public inline fun measureTimeMillis(block: () -> Unit): Long {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val start = System.currentTimeMillis()
    block()
    return System.currentTimeMillis() - start
}

public inline fun measureNanoTime(block: () -> Unit): Long {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val start = System.nanoTime()
    block()
    return System.nanoTime() - start
}
```

使用此插件后我们可以通过 ASM 无侵入实现如下逻辑（以便在 release 版本中直接 disable）：
```kotlin
fun beforeInjected() {
    //logic
}

fun afterInjected() {
    val start = System.currentTimeMillis()
    //logic
    val end = System.currentTimeMillis()
    val time = end - start
    println("[MTC] cost time:$time")
}
```

### 研究此项目使用方式

本地调试模式下运行可以执行以下命令：
```shell
./gradlew :test-demo:run
```
你可以看到无侵入的方法耗时打印。

### 其他项目中使用方式

1. 修改`method-time-cost-plugin/gradle/maven-publish.gradle`脚本中 repositories 为自己本地或远程仓库。
2. 对 method-time-cost-plugin 中的两个 module 执行 maven-publish plugin 的 publish 发布命令进行发布。
3. 在你使用的项目 root build.gradle 中添加如下脚本片段：
```groovy
buildscript {
    repositories {
        maven { url new File(rootProject.projectDir, "/method-time-cost-plugin/repo/").toURI() } //替换为自己maven
    }

    dependencies {
        // 主插件，编译项目时自己会从 project 的 repositories 中下载依赖的 subplugin
        classpath "com.github.yanbober.mtc-kcp:mtc-gradle-plugin:main-SNAPSHOT" 
    }
}

allprojects {
    repositories {
        maven { url new File(rootProject.projectDir, "/method-time-cost-plugin/repo/").toURI() } //替换为自己maven
    }
}
```
4. 在你的 build.gradle 中添加如下脚本片段：
```groovy
plugins {
    id 'cn.yan.gradle.mtc'
}

mtc {
    enable = true //是否启用此插件
    annotation = "cn.yan.anno.MTC"  //你用来打印方法耗时的注解类名
}
```
5. 在你的代码中使用：
```kotlin
@MTC fun prime(): Int {
    return (0..10_000_000).asSequence().take(10_000_000).last()
}
```
6. 编译运行结果如下：
```text
> Task :test-demo:run
[MTC] cn/yan/demo/UserRunKt#prime --> cost time: (164ms)
```
