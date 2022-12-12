package com.example.compat;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;


import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

@AutoService(Processor.class)
public class BindProcessor extends AbstractProcessor {
    private static final String TARGET = "target";
    private Types typeUtils;
    private Filer mFiler;
    private Elements mElementUtils; //元素相关工具类：用于获取java类文件
    private Messager mMessager;//用于打印日志

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
        typeUtils = processingEnv.getTypeUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        //返回该注解处理器能够处理哪些注解
        Set<String> types = new LinkedHashSet<>();
        types.add(PluginDepended.class.getName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(PluginDepended.class)) {
            String className = element.getSimpleName().toString();
            String pkg = mElementUtils.getPackageOf(element).getQualifiedName().toString();
            note(element,"class name:%s.%s",pkg,className);
            ClassName target = ClassName.get(pkg, className);
            TypeSpec.Builder builder = TypeSpec.classBuilder(className+"Compat")
                    .addModifiers(Modifier.PUBLIC)
                    .addField(refTypeField(target))
                    .addMethod(constructor(target));
            JavaFile javaFile = JavaFile.builder(pkg, builder.build())
                    .addFileComment("Generated code from Compat compiler. Do not modify!")
                    .build();
            try {
                javaFile.writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 构造函数
     */
    public static MethodSpec constructor(ClassName target) {
        CodeBlock codeBlock = CodeBlock.builder()
                .addStatement("this.target = target;")
                .build();
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(target, TARGET)
                .addCode(codeBlock)
                .build();
    }

    /**
     * 需要导入 import 的类型
     */
    public static FieldSpec refTypeField(ClassName className) {
        // private File mRef;
        return FieldSpec.builder(className, TARGET, Modifier.PRIVATE).build();
    }

    private void error(Element element, String message, Object... args) {
        printMessage(Kind.ERROR, element, message, args);
    }

    private void note(Element element, String message, Object... args) {
        printMessage(Kind.NOTE, element, message, args);
    }

    private void printMessage(Kind kind, Element element, String message, Object[] args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }

        processingEnv.getMessager().printMessage(kind, message, element);
    }
}
