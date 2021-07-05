package com.example.myapplication.hook;

import android.media.AudioManager;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Hooker {
    private static final String TAG = "Hooker";

    public static void hookInstance() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        Log.i(TAG, "hookInstance: start");
        AudioFocusManager audioFocusManager = AudioFocusManager.getInstance();
        Class[] classes = audioFocusManager.getClass().getDeclaredClasses();
        Object proxy = Proxy.newProxyInstance(audioFocusManager.getClass().getClassLoader(), new Class[]{audioFocusManager.getClass()}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Log.i(TAG, "invoke: method:" + method.getName());
                return method.invoke(audioFocusManager, args);
            }
        });

        Constructor constructor = audioFocusManager.getClass().getConstructor(new Class[]{});
        constructor.setAccessible(true);
//        Method method = audioFocusManager.getClass().getDeclaredMethod("getInstance",new Class[]{});
        Log.i(TAG, "hookInstance: obj:"+proxy.getClass().getSimpleName());
        for (Class aClass : classes) {
            Log.i(TAG, "hookInstance: aclass:" + aClass.getSimpleName());
            Field field = aClass.getDeclaredField("INSTANCE");
            field.setAccessible(true);
            Log.i(TAG, "hookInstance: name" + field.getName());
            field.set(aClass,proxy);
        }
        AudioFocusManager.getInstance().printB();
    }
}
