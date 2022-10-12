package com.rh.videostream;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.webkit.WebView;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private WebSocketServer _server;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _server = new WebSocketServer();
        _server.start();

        WebView webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);

        try {
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open("filename.txt"), StandardCharsets.UTF_8));
             StringBuilder sb = new StringBuilder();
             String line;
             while ((line = bufferedReader.readLine()) != null) {
                 sb.append(line).append("\n");
             }
             String temp = sb.toString();
             temp.replace("{{PORT}}", Integer.toString(_server.getPort()));

             webView.loadData(sb.toString(), "text/html", "UTF-8");
        } catch (IOException ignored) { }
    }
}