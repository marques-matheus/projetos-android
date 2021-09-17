package com.example.listadetarefas.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.listadetarefas.model.Tarefas;

import java.util.ArrayList;
import java.util.List;

public class TarefaDAO implements ITarefaDAO {

    private SQLiteDatabase escreve;
    private SQLiteDatabase le;

    public TarefaDAO(Context context) {
        DbHelper db = new DbHelper(context);
        escreve = db.getWritableDatabase();
        le = db.getReadableDatabase();

    }

    @Override
    public boolean salvar(Tarefas tarefa) {

        ContentValues cv = new ContentValues();
        cv.put("nome", tarefa.getNomeTarefa() );

        try{
            escreve.insert(DbHelper.TABELA_TAREFAS, null, cv);
        }catch (Exception e){
            return false;

        }



        return true;
    }

    @Override
    public boolean atualizar(Tarefas tarefa) {

        ContentValues cv = new ContentValues();
        cv.put("nome", tarefa.getNomeTarefa());

        try{
            String[] args = {String.valueOf(tarefa.getId())};
            escreve.update(DbHelper.TABELA_TAREFAS, cv, "id=?", args);
        }catch (Exception e){
            return false;

        }



        return true;
    }

    @Override
    public boolean deletar(Tarefas tarefa) {


        try{
            String[] args = {String.valueOf(tarefa.getId())};
            escreve.delete(DbHelper.TABELA_TAREFAS,"id=?", args);
        }catch (Exception e){
            return false;

        }

        return true;
    }

    @Override
    public List<Tarefas> listar() {

        List<Tarefas> tarefas = new ArrayList<>();

        String sql = "SELECT * FROM " + DbHelper.TABELA_TAREFAS + ";";
        Cursor c = le.rawQuery(sql, null);

        while (c.moveToNext()){
            Tarefas tarefa = new Tarefas();
            Long id = c.getLong(c.getColumnIndex("id"));
            String nomeTarefa = c.getString(c.getColumnIndex("nome"));


            tarefa.setId(id);
            tarefa.setNomeTarefa(nomeTarefa);

           tarefas.add( tarefa);


        }

        return  tarefas;

    }
}
