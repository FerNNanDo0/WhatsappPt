package com.example.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.config.UserFirebase;
import com.example.whatsapp.model.Messages;

import java.util.List;

public class AdapterMenssagens extends RecyclerView.Adapter<AdapterMenssagens.MyViewHolder> {

    private View view;
    private List<Messages> listMessages;
    private Context context;

    private static final int TIPO_REMETENTE = 0;
    private static final int TIPO_DESTINATARIO = 1;

    public AdapterMenssagens( List<Messages> listMessages , Context c){
        this.context = c;
        this.listMessages = listMessages;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if ( viewType == TIPO_REMETENTE ){
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.adapter_msgs_remetente, parent, false);

        }else if ( viewType == TIPO_DESTINATARIO ){
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.adapter_msgs_destinatario, parent, false);
        }

        return new MyViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Messages messages = listMessages.get( position );
        String msg = messages.getMessage();
        String img = messages.getFoto();

        if ( img != null  ){
            Uri url = Uri.parse( img );
            Glide.with(context).load( url ).into( holder.img_message );

            holder.textMessage.setVisibility(View.GONE);

        }else{
            holder.textMessage.setText( msg );

            holder.img_message.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return listMessages.size();
    }


    @Override
    public int getItemViewType(int position) {

        Messages messages = listMessages.get( position );

        String idUser = UserFirebase.getIdUser();

        if ( idUser.equals( messages.getIdUsuarioAtual() ) ){
            return TIPO_REMETENTE;
        }

        return TIPO_DESTINATARIO;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textMessage;
        ImageView img_message;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textMessage = itemView.findViewById(R.id.textViewMsg);
            img_message = itemView.findViewById(R.id.imageMsgFoto);

        }
    }
}
