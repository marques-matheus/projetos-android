package com.example.organizze.activity.Helper;

import java.text.SimpleDateFormat;

public class DataUtilCustom {
    public static String dataAtual(){
       long data = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/YYYY");
        String dataString = simpleDateFormat.format( data );

        return dataString;
    }

    public static String mesAnoDataEscolhida (String data){
     String retornoData[] = data.split("/");
     String dia = retornoData[0];
     String mes = retornoData[1];
     String ano = retornoData[2];


     String mesAno = mes + ano;
     return mesAno;

    }
}
