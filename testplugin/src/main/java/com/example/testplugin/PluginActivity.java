package com.example.testplugin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.commsdk.compat.IRecPlugin;
import com.example.compat.PluginDepended;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

@PluginDepended
public class PluginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin);
    }
}