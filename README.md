# mtc-kcp
一个基于 Kotlin Compiler Plugin 实现的迷你方法耗时无侵入编译插件。也是一个用来学习 KCP 编写的好案例。

ASM 修改前后对比：
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

1. 在你的 root build.gradle 中添加如下脚本片段：
```groovy
buildscript {
    repositories {
        maven { url 'https://jitpack.io' }
    }

    dependencies {
        classpath "com.github.yanbober.mtc-kcp:mtc-gradle-plugin:main-SNAPSHOT"
    }
}
```
2. 在你的 build.gradle 中添加如下脚本片段：
```groovy
plugins {
    id 'cn.yan.gradle.mtc'
}

mtc {
    enable = true //是否启用此插件
    annotation = "cn.yan.anno.MTC"  //你用来打印方法耗时的注解类名
}
```
3. 在你的代码中使用：
```kotlin
@MTC fun prime(): Int {
    return (0..10_000_000).asSequence().take(10_000_000).last()
}
```
4. 编译运行结果如下：
```text
> Task :test-demo:run
[MTC] cn/yan/demo/UserRunKt#prime --> cost time: (164ms)
```