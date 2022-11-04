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
import com.example.whatsapp.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private Context context;
    private List<Conversas> listConversas;
    private List<User> listUsersContatos;

    private View view;

    public Adapter(List<User> listContatos, List<Conversas> listConversas, Context c){
        this.context = c;
        this.listUsersContatos = listContatos;
        this.listConversas = listConversas;
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if( listUsersContatos != null ){
            User user = listUsersContatos.get( position );

            holder.primaryText.setText( user.getNome() );
            holder.secondaryText.setText( user.getEmail() );

            if ( user.getFoto() != null ){
                Uri uri = Uri.parse( user.getFoto() );
                Glide.with( context ).load( uri ).into( holder.img );
            }

        }

        if( listConversas != null ){
            Conversas conversas = listConversas.get( position );

            holder.secondaryText.setText( conversas.getUltimaMenssagen() );

            if ( conversas.getUserExibicao() != null){

                String urlFotoRemetente = conversas.getUserExibicao().getFoto();

                holder.primaryText.setText( conversas.getUserExibicao().getNome() );

                if ( urlFotoRemetente != null ){
                    Uri uri = Uri.parse( urlFotoRemetente );
                    Glide.with( context ).load( uri ).into( holder.img );
                }


            }
        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_layout, parent , false);

        return new MyViewHolder( view );
    }

    @Override
    public int getItemCount() {

        if( listUsersContatos != null ){
            return listUsersContatos.size();
        }
        if ( listConversas != null ){
            return listConversas.size();
        }

        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView primaryText;
        private TextView secondaryText;
        private CircleImageView img;

        public MyViewHolder(View itemView){
            super(itemView);

            primaryText = itemView.findViewById(R.id.textPrimary);
            secondaryText = itemView.findViewById(R.id.textSecond);
            img = itemView.findViewById(R.id.img_layout);


        }

    }
}
