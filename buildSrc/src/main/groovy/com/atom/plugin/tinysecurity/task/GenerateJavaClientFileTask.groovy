package com.atom.plugin.tinysecurity.task

import com.atom.plugin.tinysecurity.extension.KeyExt
import com.atom.plugin.tinysecurity.utils.StringUtils
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

import javax.lang.model.element.Modifier

class GenerateJavaClientFileTask extends DefaultTask {

    @OutputDirectory
    File outputDir

    @Input
    List<KeyExt> keyExts

    @TaskAction
    void generate() {

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder("TinySecurityClient")
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .addMethod(
                MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .addException(IllegalAccessException.class)
                        .addStatement("throw new IllegalAccessException()")
                        .build())

        keyExts.each {
            classBuilder.addMethod(
                    MethodSpec.methodBuilder("${it.name}")
                            .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                            .returns(String.class)
                            .addStatement('return TinySecurityCore.get("$L")', StringUtils.md5(it.name))
                            .build()
            )
        }

        JavaFile.builder("com.atom.plugin.tinysecurity", classBuilder.build()).build().writeTo(outputDir)

    }
}