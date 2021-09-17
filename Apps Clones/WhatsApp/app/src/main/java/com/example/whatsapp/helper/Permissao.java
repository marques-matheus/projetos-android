package com.example.whatsapp.helper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissao {

    public static boolean validarPermissoes(String[] permissoes, Activity activity, int requesCode){
        if (Build.VERSION.SDK_INT >= 23){

            List<String> listaPermissoes = new ArrayList<>();
            //percorre permissoes passadas
            for (String permissao : permissoes){
              Boolean temPermissao = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;

              if (!temPermissao) listaPermissoes.add(permissao);


            }

            if (listaPermissoes.isEmpty()) return true;
            String[] novaspermissoes = new String[listaPermissoes.size()];
            listaPermissoes.toArray(novaspermissoes);



            //solicita permissao

            ActivityCompat.requestPermissions(activity, novaspermissoes, requesCode);





        }



        return true;
    }
}
