package com.example.compat_compile;

import com.example.compat_runtime.BindView;
import com.google.auto.service.AutoService;


import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@AutoService(Processor.class)
public class BindProcessor extends AbstractProcessor {
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
        types.add(BindView.class.getName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return false;
    }
}
