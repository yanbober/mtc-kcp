/**
 * MIT License
 *
 * Copyright (c) 2022 yanbo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package cn.yan.mtc.gradle

import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.*

/**
 * stand kotlin compiler plugin support plugin.
 */
class MTCGradleSubPlugin: KotlinCompilerPluginSupportPlugin {
    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        return project.provider {
            val extension = project.extensions.findByType(MTCGradleExtension::class.java)
                ?: MTCGradleExtension()
            if (extension.enable && extension.annotation.isEmpty()) {
                error("MTC method time cost plugin is enabled, but no annotation class were set!")
            }

            val options = mutableListOf<SubpluginOption>()
            options += SubpluginOption(key = "annotation", value = extension.annotation)
            options += SubpluginOption(key = "enable", value = extension.enable.toString())
            options
        }
    }

    override fun getCompilerPluginId(): String = "cn.yan.gradle.mtc.kotlin"
    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        "com.github.yanbober.mtc-kcp",
        "mtc-kotlin-plugin",
        "main-SNAPSHOT")

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
        return kotlinCompilation.target.project.plugins.hasPlugin(MTCGradlePlugin::class.java)
    }
}
