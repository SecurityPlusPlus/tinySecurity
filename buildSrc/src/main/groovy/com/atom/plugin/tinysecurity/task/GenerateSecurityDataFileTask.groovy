package com.atom.plugin.tinysecurity.task

import com.atom.plugin.tinysecurity.extension.KeyExt
import com.atom.plugin.tinysecurity.generater.SecurityDataFileBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class GenerateSecurityDataFileTask extends DefaultTask {

    private static final String TARGET_FILE_NAME = "extern-keys.h"
    private static final String GROUP_NAME = 'tinySecurity'

    @OutputDirectory
    File outputDir

    @Input
    List<KeyExt> keyExts

    @Input
    String encryptKey

    @Input
    String encryptMode

    @Input
    String encryptIV

    GenerateSecurityDataFileTask() {
        group = GROUP_NAME
    }

    @TaskAction
    void generate() {
        def targetFile = new File(outputDir, TARGET_FILE_NAME)
        def writer = new FileWriter(targetFile)
        new SecurityDataFileBuilder(TARGET_FILE_NAME, keyExts)
                .setEncryptKey(encryptKey)
                .setEncryptMode(encryptMode)
                .setEncryptIV(encryptIV)
                .build()
                .each { writer.append(it) }
        writer.flush()
        writer.close()
    }


}