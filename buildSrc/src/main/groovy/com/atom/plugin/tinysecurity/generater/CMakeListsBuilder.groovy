package com.atom.plugin.tinysecurity.generater

import com.atom.plugin.tinysecurity.utils.StringUtils

class CMakeListsBuilder {

    private String originCMakePath
    private String cipherCMakePath

    CMakeListsBuilder(String cipherCMakePath) {
        this.cipherCMakePath = cipherCMakePath
    }

    def setOriginCMakePath(String originCMakePath) {
        this.originCMakePath = originCMakePath
        this
    }

    List<String> build() {
        List<String> lines = new ArrayList<>()
        lines.add("# Auto-Generated By tinySecurity\n\n")
        lines.add("cmake_minimum_required(VERSION 3.4.1)\n\n")
        lines.add("add_subdirectory(${StringUtils.convertToUnix(new File(cipherCMakePath).parentFile.canonicalPath)} cipher.out)\n\n")
        if (originCMakePath != null) {
            lines.add("add_subdirectory(${StringUtils.convertToUnix(new File(originCMakePath).parentFile.canonicalPath)} origin.out)\n\n")
        }
        lines
    }

}