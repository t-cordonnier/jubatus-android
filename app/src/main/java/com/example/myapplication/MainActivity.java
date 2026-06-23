package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Récupérer les vues
        textView = findViewById(R.id.textView);
        Button btnFetch = findViewById(R.id.btnFetch);

        // Configurer le listener pour le bouton
        btnFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchRandomMessage();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchRandomMessage() {
        String url = "http://silvestris-lab.org:8080/jubatus-server/api/random-message";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // En cas d'erreur réseau
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText("Erreur réseau: " + e.getMessage());
                        textView.setTextColor(0xFFFF0000); // Rouge
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result;
                final int textColor;

                if (response.isSuccessful()) {
                    // Succès: parse le JSON
                    String json = response.body().string();
                    // Extraire le message (format: {"message": "..."})
                    int start = json.indexOf("\"message\":\"") + 11;
                    int end = json.indexOf("\"", start);
                    result = json.substring(start, end);
                    textColor = 0xFF000000; // Noir
                    Log.d("API_RESPONSE", "JSON complet reçu: " + json);
                    Log.d("API_RESPONSE", "Message extrait: " + result);
                } else {
                    // Erreur HTTP
                    result = "Erreur HTTP: " + response.code() + " - " + response.message();
                    textColor = 0xFFFF0000; // Rouge
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(result);
                        textView.setTextColor(textColor);
                    }
                });
            }
        });
    }
}
