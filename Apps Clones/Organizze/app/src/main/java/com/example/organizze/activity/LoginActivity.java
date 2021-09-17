package com.example.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import com.example.organizze.activity.config.ConfiguraçãoFirebase;
import com.example.organizze.activity.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private Button botaoEntrar;
    private  Usuario usuario;
    private FirebaseAuth autenticacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        campoEmail = findViewById(R.id.editEmailLogin);
        campoSenha = findViewById(R.id.editSenhaLogin);
        botaoEntrar = findViewById(R.id.buttonEntrar);

        botaoEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoEmail   = campoEmail.getText().toString();
                String textoSenha   = campoSenha.getText().toString();

                if (!textoEmail.isEmpty()){
                    if (!textoSenha.isEmpty()){

                        usuario = new Usuario();
                        usuario.setEmail( textoEmail );
                        usuario.setSenha(textoSenha);
                        validarLogin();

                    }else {
                        Toast.makeText(LoginActivity.this, "Preencha a Senha", Toast.LENGTH_SHORT).show();
                    }

                }else {






                    Toast.makeText(LoginActivity.this, "Preencha o Email", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void validarLogin(){

        autenticacao = ConfiguraçãoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(),
                usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
               if (task.isSuccessful()){
                   abrirtelaPrincipal();



               }else {

                   String excecao = " ";

                   try {
                       throw task.getException();

                   }catch (FirebaseAuthInvalidUserException e ) {
                       excecao = "Usuário inválido";
                   }catch (FirebaseAuthInvalidCredentialsException e){
                       excecao = "Email e Senha inválidos" + e.getMessage();
                   } catch (Exception e){
                       excecao = "Erro ao cadastras usuário " + e.getMessage();
                       e.printStackTrace();
                   }



                   Toast.makeText(LoginActivity.this,
                           excecao,
                           Toast.LENGTH_SHORT).show();
               }



            }
        });


    }

    public void abrirtelaPrincipal(){
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }
}