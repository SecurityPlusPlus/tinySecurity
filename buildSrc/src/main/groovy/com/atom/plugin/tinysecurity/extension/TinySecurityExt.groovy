package com.atom.plugin.tinysecurity.extension

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

class TinySecurityExt {

    Project project

    NamedDomainObjectContainer<KeyExt> keys

    String encryptKey = ""

    String encryptMode = ""

    String encryptIV = ""

    TinySecurityExt(Project project) {
        this.project = project
        keys = project.container(KeyExt)
    }

    def keys(Closure closure) {
        keys.configure closure
    }
}