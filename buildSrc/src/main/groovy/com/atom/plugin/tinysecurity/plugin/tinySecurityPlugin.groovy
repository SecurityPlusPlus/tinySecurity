package com.atom.plugin.tinysecurity.plugin

import com.android.build.gradle.AppExtension
import com.atom.plugin.tinysecurity.extension.TinySecurityExt
import com.atom.plugin.tinysecurity.generater.CMakeListsBuilder
import com.atom.plugin.tinysecurity.task.GenerateSecurityDataFileTask
import com.atom.plugin.tinysecurity.task.GenerateJavaClientFileTask
import com.atom.plugin.tinysecurity.utils.IOUtils
import com.atom.plugin.tinysecurity.utils.LogUtils
import com.atom.plugin.tinysecurity.utils.StringUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete

class tinySecurityPlugin implements Plugin<Project> {

    private String originCmakeListPath

    @Override
    void apply(Project project) {
        project.extensions.add("tinySecurity", new TinySecurityExt(project))
        setupProjectNativeSupport(project)
    }


    private def setupProjectNativeSupport(Project project) {
        project.afterEvaluate {
            unzipNativeArchive(project)
            def android = project.extensions.findByType(AppExtension)
            createTasks(project, android)

            originCmakeListPath = android.externalNativeBuild.cmake.path?.canonicalPath
            File targetFile = generateCMakeListsFile(project, originCmakeListPath)
            android.externalNativeBuild {
                cmake {
                    path targetFile.canonicalPath
                }
            }
        }
    }

    /**
     * 解压native project 相关文件
     * @param project
     * @return
     */
    private def unzipNativeArchive(Project project) {
        def archiveFile = getNativeArchiveFile(project)
        LogUtils.Log("archiveFile = ${archiveFile.toString()}")
        project.copy {
            from archiveFile
            include "src/main/cpp/**"
            include "CMakeLists.txt"
            exclude "src/main/cpp/include/extern-keys.h"
            LogUtils.Log("project.buildDir = ${project.buildDir}")
            into new File(project.buildDir, "tinySecurity")
        }
    }

    /**
     * 获取native project 相关文件
     * @param project
     * @return
     */
    private def getNativeArchiveFile(Project project) {
        if (project.rootProject.subprojects.find { it.name == "buildSo" } != null) {
            return project.rootProject.file("buildSo").canonicalPath
        } else {
            def archiveZip = findNativeArchiveFromBuildscript(project)
            if (archiveZip != null) {
                archiveZip = findNativeArchiveFromBuildscript(project.rootProject)
            }
            archiveZip
        }
    }

    /**
     * 使用 Buildscript  查找
     * @param project
     * @return
     */
    private def findNativeArchiveFromBuildscript(Project project) {
        def archiveZip = null
        project.buildscript.configurations.findAll {
            project.gradle.gradleVersion >= '4.0' ? it.isCanBeResolved() : true
        }.each {
            File file = it.files.find {
                it.name.toUpperCase(Locale.default).contains("TINYSECURITY")
            }
            if (file != null) {
                archiveZip = project.zipTree(file)
            }
            archiveZip
        }
    }

    private File generateCMakeListsFile(Project project, String originCMakeListsPath) {
        def outputDir = new File(project.buildDir, "/tinySecurity/cmake")
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
        def targetFile = new File(outputDir, "CMakeLists.txt")
        def writer = new FileWriter(targetFile)
        new CMakeListsBuilder("${project.buildDir.canonicalPath}/tinySecurity/src/main/cpp/CMakeLists.txt")
                .setOriginCMakePath(originCMakeListsPath)
                .build().each {
            writer.append(it)
        }
        writer.flush()
        writer.close()
        targetFile
    }

    /**
     * 创建 执行 task
     *
     * @param project
     * @param android
     */
    private def createTasks(Project project, AppExtension android) {

        //创建CmakeListFile
        def generateCmakeListFileTask = project.tasks.create("generateCmakeListFile") {
            group "tinySecurity"
            doLast {
                generateCMakeListsFile(project, originCmakeListPath)
            }
        }

        //Copy native工程文件
        def archiveFile = getNativeArchiveFile(project)
        def copyNativeArchiveTask = project.tasks.create("copyNativeArchive", Copy) {
            group "tinySecurity"
            from archiveFile
            include "src/main/cpp/**"
            include "CMakeLists.txt"
            exclude "src/main/cpp/include/extern-keys.h"
            into new File(project.buildDir, "tinySecurity")
        }

        copyNativeArchiveTask.dependsOn generateCmakeListFileTask

        //查询application 的 gradle 配置文件
        android.applicationVariants.all { variant ->
            def configs = project.tinySecurity
            def secretKey = configs.encryptKey
            def keys = configs.keys.asList()
            LogUtils.Log("encryptKey = ${secretKey}")
            LogUtils.Log("keys = ${keys.toString()}")
            LogUtils.Log("variant.name = ${variant.name}")
            /**
             *
             */
            def generateCipherSoHeaderTask = project.tasks.create("generate${StringUtils.capitalize(variant.name)}CipherSoHeader", GenerateSecurityDataFileTask)
            generateCipherSoHeaderTask.configure {
                it.keyExts = keys
                it.encryptKey = secretKey
                it.encryptMode = configs.encryptMode
                it.encryptIV = configs.encryptIV
                it.outputDir = IOUtils.getNativeHeaderDir(project)
            }
            /**
             *
             */
            project.getTasksByName("generateJsonModel${StringUtils.capitalize(variant.name)}", false).each {
                LogUtils.Log("generateJsonModel")
                it.dependsOn copyNativeArchiveTask
                it.dependsOn generateCipherSoHeaderTask
            }
            /**
             *
             */
            def outputDir = new File(project.buildDir, "/generated/source/tinySecurity/${variant.name}")
            def generateJavaClientFileTask = project.tasks.create("generate${StringUtils.capitalize(variant.name)}JavaClient", GenerateJavaClientFileTask)
            generateJavaClientFileTask.configure {
                it.outputDir = outputDir
                it.keyExts = keys
            }
            /**
             *
             */
            variant.registerJavaGeneratingTask(generateJavaClientFileTask, outputDir)
            /**
             *
             */
            def copyJavaArchiveTask = project.tasks.create("copyJavaArchive${StringUtils.capitalize(variant.name)}", Copy) {
                LogUtils.Log("copyJavaArchiveTask")
                group "tinySecurity"
                from archiveFile
                LogUtils.Log("archiveFile = ${archiveFile.toString()}")
                include "src/main/java/**"
                eachFile {
                    it.path = it.path.replaceFirst("src/main/java/", "")
                }
                into outputDir
            }

            def deleteJavaFileTask = project.tasks.create("deleteJavaArchive${StringUtils.capitalize(variant.name)}", Delete){
                LogUtils.Log("deleteJavaFileTask")
                group "tinySecurity"
                delete "${outputDir}/src"
            }

            variant.registerJavaGeneratingTask(deleteJavaFileTask, outputDir)
            /**
             *
             */
            generateJavaClientFileTask.dependsOn copyJavaArchiveTask
            deleteJavaFileTask.dependsOn generateJavaClientFileTask
        }

    }
}