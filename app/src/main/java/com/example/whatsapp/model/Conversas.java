package com.example.whatsapp.model;

import com.example.whatsapp.config.Firebase;
import com.google.firebase.database.DatabaseReference;

public class Conversas {

    String idRemetente;
    String idDestinatario;
    User userExibicao;
    String ultimaMenssagen;

    DatabaseReference conversasRef;
    DatabaseReference databaseRef;

    public Conversas(){}


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

        databaseRef = Firebase.getDatabaseRef();
        conversasRef = databaseRef.child("conversas");

        conversasRef.child( this.idRemetente )
                .child( this.idDestinatario )
                .setValue( this );

    }

}
