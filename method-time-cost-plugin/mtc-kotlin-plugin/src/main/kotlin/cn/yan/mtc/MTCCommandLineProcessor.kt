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
package cn.yan.mtc

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

class MTCCommandLineProcessor: CommandLineProcessor {
    override val pluginId: String = "cn.yan.gradle.mtc.kotlin" // same as ID from subPlugin

    override val pluginOptions: Collection<CliOption> = listOf(
        CliOption(
            optionName = "enable",
            valueDescription = "<true|false>",
            description = "whether plugin is enabled.",
            required = false
        ),
        CliOption(
            optionName = "annotation",
            valueDescription = "<fqname>",
            description = "method cost time mark annotation name.",
            required = true
        )
    )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration) = when (option.optionName) {
        "enable" -> configuration.put(KEY_ENABLE, value.toBoolean())
        "annotation" -> configuration.put(KEY_ANNOTATION, value)
        else -> error("Unexpected config option ${option.optionName}")
    }
}

val KEY_ENABLE = CompilerConfigurationKey<Boolean>("is the plugin is enabled")
val KEY_ANNOTATION = CompilerConfigurationKey<String>("the plugin assigned annotation")
