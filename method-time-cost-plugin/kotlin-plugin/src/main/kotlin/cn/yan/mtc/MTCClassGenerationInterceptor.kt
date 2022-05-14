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

import org.jetbrains.kotlin.codegen.ClassBuilder
import org.jetbrains.kotlin.codegen.ClassBuilderFactory
import org.jetbrains.kotlin.codegen.DelegatingClassBuilder
import org.jetbrains.kotlin.codegen.extensions.ClassBuilderInterceptorExtension
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.Opcodes

class MTCClassGenerationInterceptor(
    val annotation: String
) : ClassBuilderInterceptorExtension {
    override fun interceptClassBuilderFactory(
        interceptedFactory: ClassBuilderFactory,
        bindingContext: BindingContext,
        diagnostics: DiagnosticSink
    ): ClassBuilderFactory = object: ClassBuilderFactory by interceptedFactory {
        override fun newClassBuilder(origin: JvmDeclarationOrigin): ClassBuilder {
            return MTCClassBuilder(
                annotation = annotation,
                delegateBuilder = interceptedFactory.newClassBuilder(origin)
            )
        }
    }
}

internal class MTCClassBuilder(
    private val annotation: String,
    private val delegateBuilder: ClassBuilder
) : DelegatingClassBuilder() {
    override fun newMethod(
        origin: JvmDeclarationOrigin,
        /* not used: */ access: Int, name: String, desc: String, signature: String?, exceptions: Array<out String>?
    ): MethodVisitor {
        val original = super.newMethod(origin, access, name, desc, signature, exceptions)

        val function = origin.descriptor as? FunctionDescriptor ?: return original
        if (!function.annotations.hasAnnotation(FqName(annotation))) {
            // none annotation were on this function, so return the original behavior.
            return original
        }
        return MethodTimeCostAdapter(Opcodes.ASM7, access, name, delegateBuilder.thisName, desc, original)
    }

    override fun getDelegate(): ClassBuilder {
        return this.delegateBuilder
    }
}
