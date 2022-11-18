package com.example.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.model.Conversas;
import com.example.whatsapp.model.Grupo;
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

        // lista de contatos
        if( listUsersContatos != null ){
            User user = listUsersContatos.get( position );

            holder.primaryText.setText( user.getNome() );
            holder.secondaryText.setText( user.getEmail() );

            if ( user.getFoto() != null ){
                Uri uri = Uri.parse( user.getFoto() );
                Glide.with( context ).load( uri ).into( holder.img );
            }

            // se email == vazio define item de NOVO GRUPO
            if( user.getEmail().isEmpty() ){
                holder.secondaryText.setVisibility(View.GONE);
                holder.divisorLayout.setVisibility(View.GONE);
                holder.img.setImageResource(R.drawable.icone_grupo);
            }

        }

        // lista de conversas
        if( listConversas != null ){

            Conversas conversas = listConversas.get( position );
            holder.secondaryText.setText( conversas.getUltimaMenssagen() );

            // verifica se é uma conversa de grupo
            if( conversas.getIsGroup().equals("true")  ){

                Grupo grupo = conversas.getGrupo();
                holder.primaryText.setText( grupo.getNome() );

                // define foto do grupo
                if ( grupo.getFoto() != null ){
                    Uri uri = Uri.parse( grupo.getFoto() );
                    Glide.with( context ).load( uri ).into( holder.img );
                }else{
                    holder.img.setImageResource(R.drawable.padrao);
                }

            }else{

                User user = conversas.getUserExibicao();
                holder.primaryText.setText( user.getNome() );

                if ( user.getFoto() != null){
                    Uri uri = Uri.parse( user.getFoto() );
                    Glide.with( context ).load( uri ).into( holder.img );
                }else{
                    holder.img.setImageResource(R.drawable.padrao);
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
        LinearLayout divisorLayout;

        public MyViewHolder(View itemView){
            super(itemView);

            primaryText = itemView.findViewById(R.id.textPrimary);
            secondaryText = itemView.findViewById(R.id.textSecond);
            img = itemView.findViewById(R.id.img_layout);

            divisorLayout = itemView.findViewById(R.id.divisoLayout);


        }

    }
}
