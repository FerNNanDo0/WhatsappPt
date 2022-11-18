package com.example.whatsapp.model;

import android.net.Uri;

import com.example.whatsapp.config.Firebase;
import com.example.whatsapp.config.UserFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.Objects;

public class Conversas implements Serializable {

    String idRemetente;
    String idDestinatario;
    User userExibicao;
    String ultimaMenssagen;

    String isGroup;

    Grupo grupo;

    DatabaseReference conversasRef;
    DatabaseReference databaseRef;

    public Conversas(){
        this.setIsGroup("false");
    }



    public String getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(String isGroup) {
        this.isGroup = isGroup;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public String getIdRemetente() {
        return idRemetente;
    }

    public void setIdRemetente(String idRemetente) {
        this.idRemetente = idRemetente;
    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public User getUserExibicao() {
        return userExibicao;
    }

    public void setUserExibicao(User userExibicao) {
        this.userExibicao = userExibicao;
    }

    public String getUltimaMenssagen() {
        return ultimaMenssagen;
    }

    public void setUltimaMenssagen(String ultimaMenssagen) {
        this.ultimaMenssagen = ultimaMenssagen;
    }

    public void salvar(){
        //FirebaseUser userAtual = UserFirebase.getUser();

        databaseRef = Firebase.getDatabaseRef();
        conversasRef = databaseRef.child("conversas");

        // salvar para remetente
        conversasRef.child( this.idRemetente )
                .child( this.idDestinatario )
                .setValue( this );


    }

}
