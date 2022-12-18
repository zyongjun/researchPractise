package com.example.commsdk.compat;

import com.example.compat.PluginDepended;

import java.util.Base64;

@PluginDepended
public interface IRecPlugin extends IRecParent{
    IRecPlugin test(Object compat, Base64 base64);
    int test(int a,int b);
    void testV(int a);
    String testS();
}
