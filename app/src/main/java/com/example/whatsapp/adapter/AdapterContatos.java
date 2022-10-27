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

public class AdapterContatos extends RecyclerView.Adapter<AdapterContatos.MyViewHolder> {

    private List<User> listUsersContatosa;
    private Context context;
    private View view;

    public AdapterContatos(List<User> contatos, Context c){
        this.context = c;
        this.listUsersContatosa = contatos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_contatos, parent , false);

        return new MyViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = listUsersContatosa.get( position );

        holder.nome.setText( user.getNome() );
        holder.email.setText( user.getEmail() );

        if ( user.getFoto() != null ){
            Uri uri = Uri.parse( user.getFoto() );
            Glide.with( context ).load( uri ).into( holder.imgPerfil );
        }

    }

    @Override
    public int getItemCount() {
        return listUsersContatosa.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView nome;
        private TextView email;
        private CircleImageView imgPerfil;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textNome);
            email = itemView.findViewById(R.id.textEmail);
            imgPerfil = itemView.findViewById(R.id.imgPerfilContatos);


        }
    }
}