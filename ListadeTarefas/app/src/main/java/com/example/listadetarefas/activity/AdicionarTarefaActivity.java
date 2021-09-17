package com.example.listadetarefas.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.listadetarefas.Helper.TarefaDAO;
import com.example.listadetarefas.R;
import com.example.listadetarefas.model.Tarefas;
import com.google.android.material.textfield.TextInputEditText;

public class AdicionarTarefaActivity extends AppCompatActivity {

    private TextInputEditText editTarefa;
    private Tarefas tarefaAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_tarefa);
        editTarefa = findViewById(R.id.textTarefa);

        //recuperar tarefa editada

        tarefaAtual = (Tarefas) getIntent().getSerializableExtra("tarefaSelecionada");
        //configurar texto

        if(tarefaAtual != null){
            editTarefa.setText( tarefaAtual.getNomeTarefa());

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_adicionar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.itemSalvar :

                TarefaDAO tarefaDAO = new TarefaDAO(getApplicationContext());

                if(tarefaAtual != null){//editar
                    String nomeTarefa = editTarefa.getText().toString();
                    if(!nomeTarefa.isEmpty()) {

                        Tarefas tarefa = new Tarefas();
                        tarefa.setNomeTarefa( nomeTarefa );
                        tarefa.setId(tarefaAtual.getId());


                        //atualizar tabela

                        if(tarefaDAO.atualizar(tarefa)){
                            finish();

                            Toast.makeText(getApplicationContext(), "Sucesso ao atualizar tarefa!", Toast.LENGTH_SHORT).show();
                        }else {


                            Toast.makeText(getApplicationContext(), "Erro ao atualizar tarefa!", Toast.LENGTH_SHORT).show();

                        }
                    }



                } else {//salvar

                    String nomeTarefa = editTarefa.getText().toString();
                    if(!nomeTarefa.isEmpty()){
                        Tarefas tarefa = new Tarefas();
                        tarefa.setNomeTarefa(nomeTarefa);
                        if (tarefaDAO.salvar(tarefa)){
                            finish();

                            Toast.makeText(getApplicationContext(), "Sucesso ao salvar tarefa!", Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(getApplicationContext(), "Erro ao salvar tarefa!", Toast.LENGTH_SHORT).show();

                        }


                    }
                }



                break;


        }
        return super.onOptionsItemSelected(item);

    }
}