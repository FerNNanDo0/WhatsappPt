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
import com.example.whatsapp.model.Conversas;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterConversas extends RecyclerView.Adapter<AdapterConversas.MyViewHolderConversas> {

    List<Conversas> listConversas;
    Context context;
    View view;

    public AdapterConversas(Context context, List<Conversas> conversas){
        this.listConversas = conversas;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderConversas holder, int position) {

        Conversas conversas = listConversas.get( position );

        String urlFotoRemetente = conversas.getUserExibicao().getFoto();

        holder.nome.setText( conversas.getUserExibicao().getNome() );
        holder.ultimaMsg.setText( conversas.getUltimaMenssagen() );

        if ( urlFotoRemetente != null ){
            Uri uri = Uri.parse( urlFotoRemetente );
            Glide.with( context ).load( uri ).into( holder.imgPerfil );
        }

    }

    @NonNull
    @Override
    public MyViewHolderConversas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        view = LayoutInflater
                .from(parent.getContext())
                .inflate( R.layout.adapter_layout, parent, false);
//        view = View.inflate( parent.getContext(), R.layout.adapter_contatos, parent );

        return new MyViewHolderConversas( view );
    }

    @Override
    public int getItemCount() {
        return listConversas.size();
    }

    public class MyViewHolderConversas extends RecyclerView.ViewHolder{

        private TextView nome;
        private TextView ultimaMsg;
        private CircleImageView imgPerfil;

        public MyViewHolderConversas(@NonNull View itemView ){
            super(itemView);

            nome = itemView.findViewById(R.id.textPrimary);
            ultimaMsg = itemView.findViewById(R.id.textSecond);
            imgPerfil = itemView.findViewById(R.id.img_layout);
        }
    }
}
