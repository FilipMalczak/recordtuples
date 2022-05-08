package com.filipmalczak.recordtuples

import groovy.transform.Canonical

class FsLayout {
    File projectRoot
    String basePackage

    File generatedSourcesDir
    private File packageDir

    FsLayout(File projectRoot, String basePackage) {
        this.projectRoot = projectRoot
        this.basePackage = basePackage
        generatedSourcesDir = projectRoot
            .toPath()
            .resolve("src")
            .resolve("main")
            .resolve("generatedJava")
            .toFile()
        packageDir = generatedSourcesDir
            .toPath()
            .resolve(basePackage.replaceAll("[.]", File.separator))
            .toFile()
        packageDir.mkdirs()
    }

    File sourceFile(String name){
        new File(packageDir, name+".java")
    }
}
