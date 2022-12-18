package com.example.compat;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

@AutoService(Processor.class)
public class BindProcessor extends AbstractProcessor {
    private static final String TARGET = "target";
    public static final String M_METHODS = "mMethods";
    public static final String CLZ = "clz";
    private Types typeUtils;
    private Filer mFiler;
    private Elements mElementUtils; //元素相关工具类：用于获取java类文件

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
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
            error(element, "start class name start");
            String className = element.getSimpleName().toString();
            String pkg = mElementUtils.getPackageOf(element).getQualifiedName().toString();
            note(element, "class name:%s.%s", pkg, className);
            ClassName target = ClassName.get(pkg, className);
            ElementKind kind = element.getKind();
            note(element, "annotated kind %s ", kind);
            if (!kind.isInterface() && !kind.isClass()) {
                continue;
            }

            TypeSpec.Builder builder = TypeSpec.classBuilder(className + "Compat")
                    .addModifiers(Modifier.PUBLIC)
                    .addField(refTypeField(target))
                    .addMethod(createConstructor())
                    .addMethod(createInit());
            // Check if an empty public constructor is given
            for (Element enclosed : element.getEnclosedElements()) {
                ElementKind enclosedKind = enclosed.getKind();
                note(element, "enclosed kind %s ", enclosedKind);
                if (enclosed.getKind() == ElementKind.CONSTRUCTOR) {
                    continue;
                }
                ExecutableElement methodElement = (ExecutableElement) enclosed;
                builder.addMethod(createMethod(target, methodElement));
            }

            JavaFile javaFile = JavaFile.builder(pkg, builder.build())
                    .addFileComment("Generated code from Compat compiler. Do not modify!")
                    .build();
            try {
                URI uri = javaFile.toJavaFileObject().toUri();
                note(element, "start class name end path:%s raw:%s ", uri.getPath(), uri.getRawPath());
                javaFile.writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
                error(element, "start class name end path error:", e.getMessage());
            }
        }
        return true;
    }

    /**
     * 构造函数
     */
    public static MethodSpec createConstructor() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build();
    }

    private static MethodSpec createInit() {
        CodeBlock codeBlock = CodeBlock.builder()
                .addStatement(M_METHODS + " = $T.stream(clz.getMethods())\n.map($T::getName).collect($T.toSet())",
                        Arrays.class, Method.class, Collectors.class)
                .build();
        return MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ParameterizedTypeName.get(Class.class), CLZ)
                .addCode(codeBlock)
                .build();
    }

    private static Object bestGuess(TypeName type) {
        if (TypeName.BOOLEAN.equals(type)) {
            return false;
        } else if (TypeName.BYTE.equals(type)) {
            return 0;
        } else if (TypeName.CHAR.equals(type)) {
            return "";
        } else if (TypeName.DOUBLE.equals(type)) {
            return 0d;
        } else if (TypeName.FLOAT.equals(type)) {
            return 0f;
        } else if (TypeName.INT.equals(type)) {
            return 0;
        } else if (TypeName.LONG.equals(type)) {
            return 0L;
        } else if (TypeName.SHORT.equals(type)) {
            return 0;
        }
        return null;
    }

    /**
     * 代理方法
     */
    public static MethodSpec createMethod(ClassName target, ExecutableElement methodElement) {
        String methodName = methodElement.getSimpleName().toString();
        MethodSpec.Builder builder = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ParameterSpec.builder(target, TARGET).build());
        TypeName returnType = TypeName.get(methodElement.getReturnType());
        builder.returns(returnType);
        StringBuilder callBuilder = new StringBuilder();
        if (returnType == TypeName.VOID) {
            callBuilder.append("target.$N(");
        } else {
            callBuilder.append("return target.$N(");
        }
        List<Object> params = new ArrayList<>();
        params.add(methodElement.getSimpleName());
        for (VariableElement parameter : methodElement.getParameters()) {
            ParameterSpec spec = ParameterSpec.get(parameter);
            builder.addParameter(spec);
            callBuilder.append("$N,");
            params.add(spec);
        }
        if (!methodElement.getParameters().isEmpty()) {
            callBuilder.deleteCharAt(callBuilder.lastIndexOf(","));
        }
        callBuilder.append(")");
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder()
                .beginControlFlow("if (mMethods == null)")
                .addStatement("init(target.getClass())")
                .endControlFlow()
                .beginControlFlow("if (mMethods.contains(\"$L\"))", methodName)
                .addStatement(callBuilder.toString(), params.toArray())
                .endControlFlow();
        if (returnType != TypeName.VOID) {
            codeBlockBuilder.addStatement("return $L", bestGuess(returnType));
        }
        builder.addCode(codeBlockBuilder.build());
        return builder.build();
    }

    public static FieldSpec refTypeField(ClassName className) {
        ParameterizedTypeName type = ParameterizedTypeName.get(Set.class, String.class);
        return FieldSpec.builder(type, M_METHODS, Modifier.PRIVATE, Modifier.STATIC).build();
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
