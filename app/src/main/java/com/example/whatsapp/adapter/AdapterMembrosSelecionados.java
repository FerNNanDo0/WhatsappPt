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
import com.example.whatsapp.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterMembrosSelecionados extends RecyclerView.Adapter<AdapterMembrosSelecionados.MyViewHolder> {

    private List<User> listContatosSelecionados;
    private Context context;
    private View view;

    public AdapterMembrosSelecionados(List<User> listContatos, Context cont){
        this.listContatosSelecionados = listContatos;
        this.context = cont;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        view = LayoutInflater.from( parent.getContext() )
                .inflate(R.layout.adapter_grupo_selecionado, parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        User user = listContatosSelecionados.get( position );

        holder.nome.setText( user.getNome() );

        // se houver uma foto define para o usuario
        if ( user.getFoto() != null ){
            Uri uri = Uri.parse( user.getFoto() );
            Glide.with( context ).load( uri ).into( holder.foto );
        }

    }

    @Override
    public int getItemCount() {
        return listContatosSelecionados.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView foto;
        TextView nome;

        public MyViewHolder(View itemView){
            super(itemView);

            foto = itemView.findViewById(R.id.img_MembroSelecionado);
            nome = itemView.findViewById(R.id.textMembroSelecionado);
        }
    }
}
