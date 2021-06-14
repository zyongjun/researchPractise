package com.joe.plugin

import com.android.build.api.transform.Context
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import groovy.io.FileType
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils


class TestTransform extends Transform{

    @Override
    String getName() {
        return "JoeTask"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        inputs.each {TransformInput input ->
            input.directoryInputs.each {
                DirectoryInput directoryInput ->
                    def dest = outputProvider.getContentLocation(directoryInput.name,directoryInput.contentTypes,directoryInput.scopes, Format.DIRECTORY)
                    println('========directoryInput============'+directoryInput.file.getAbsolutePath())
                    def dir = directoryInput.file
                    if(dir){
                        dir.traverse(type: FileType.FILES,nameFilter:~/.*\.class/) {
                            File classFile ->
                                println("@@@@@"+classFile.getAbsolutePath())
                        }
                    }
                    FileUtils.copyDirectory(directoryInput.file,dest)
            }

            input.jarInputs.each {
                JarInput jarInput ->
                    def jarName = jarInput.name
                    println('====jarName=========='+jarInput.file.getAbsolutePath())
                    def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                    if(jarName.endsWith(".jar")){
                        jarName = jarName.substring(0,jarName.length()-4)
                    }
                    File copyJarFile = jarInput.file
                    def dest = outputProvider.getContentLocation(jarName+md5Name,jarInput.contentTypes,jarInput.getScopes(),Format.JAR)
                    FileUtils.copyFile(copyJarFile,dest)
            }

        }
        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental)
    }

    @Override
    boolean isIncremental() {
        return false
    }
}