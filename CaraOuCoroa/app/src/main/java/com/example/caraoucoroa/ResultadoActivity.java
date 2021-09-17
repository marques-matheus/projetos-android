package com.example.caraoucoroa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ResultadoActivity extends AppCompatActivity {

    private ImageView imagemResultado;
    private ImageView botaoVoltar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);
        botaoVoltar = findViewById(R.id.botaoVoltar);
        imagemResultado = findViewById(R.id.imagemResultado);

        //recuperar dados
        Bundle dados = getIntent().getExtras();
        int numero = dados.getInt("numero");

        if (
                numero == 0
        ){

            imagemResultado.setImageResource(R.drawable.moeda_cara);


        } else {
            imagemResultado.setImageResource(R.drawable.moeda_coroa);
        }
      botaoVoltar.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              finish();
          }
      });

    }
}