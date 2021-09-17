package com.example.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterContatos extends RecyclerView.Adapter<AdapterContatos.MyViewHolder> {

    private List<Usuario> contatos;
    private Context context;

    public AdapterContatos(List<Usuario> listaContatos, Context c) {

        this.contatos = listaContatos;
        this.context = c;
    }

    @NonNull

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contatos, parent, false);


        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterContatos.MyViewHolder holder, int position) {
        Usuario usuarios = contatos.get(position);
        holder.nome.setText(usuarios.getNome());
        holder.email.setText(usuarios.getEmail());


        if (usuarios.getFoto() != null) {
            Uri uri = Uri.parse(usuarios.getFoto());
            Glide.with(context).load(uri).into(holder.foto);

        } else {
            holder.foto.setImageResource(R.drawable.padrao);
        }
    }


    @Override
    public int getItemCount() {
        return contatos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        CircleImageView foto;
        TextView nome, email;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


            foto = itemView.findViewById(R.id.imageViewFotoContato);
            nome = itemView.findViewById(R.id.textNomeContato);
            email = itemView.findViewById(R.id.textEmailContato);
        }

    }
}
