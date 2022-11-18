package com.example.whatsapp.model;

import com.example.whatsapp.config.Firebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Messages implements Serializable {

    DatabaseReference mensagensRef;
    DatabaseReference databaseRef;

    private String nome = "";

    private String idUsuarioAtual;
    private String message;
    private String foto;

    public Messages(){

    }

    public String getIdUsuarioAtual() {
        return idUsuarioAtual;
    }

    public void setIdUsuarioAtual(String idUsuarioAtual) {
        this.idUsuarioAtual = idUsuarioAtual;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }



    // salvar msgs
    public void salvarMsgDatabase(String idUserRemetente, String idDestinatario, Messages msg){

        // refencia de mensagens
        databaseRef = Firebase.getDatabaseRef();
        mensagensRef = databaseRef.child("mensagens");

        mensagensRef.child( idUserRemetente )
                .child( idDestinatario )
                .push()
                .setValue( msg );

//        mensagensRef.child( idDestinatario )
//                .child( idUserRemetente )
//                .push()
//                .setValue( msg );
    }
}
