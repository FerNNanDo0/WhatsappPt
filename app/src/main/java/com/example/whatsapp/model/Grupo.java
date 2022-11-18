package com.example.whatsapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.whatsapp.config.Firebase;
import com.example.whatsapp.helper.Base64decod;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Grupo implements Parcelable {

    String id;
    String nome;
    String foto;
    List<User> membros;

    DatabaseReference grupoRef;
    DatabaseReference dataBase;


    public Grupo() {

        dataBase = Firebase.getDatabaseRef();
        grupoRef = dataBase.child("grupos");

        String idGrupoFirebase = grupoRef.push().getKey();
        setId( idGrupoFirebase );

    }

    protected Grupo(Parcel in) {
        id = in.readString();
        nome = in.readString();
        foto = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nome);
        dest.writeString(foto);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Grupo> CREATOR = new Creator<Grupo>() {
        @Override
        public Grupo createFromParcel(Parcel in) {
            return new Grupo(in);
        }

        @Override
        public Grupo[] newArray(int size) {
            return new Grupo[size];
        }
    };

    public void salvar(){

        dataBase = Firebase.getDatabaseRef();
        grupoRef = dataBase.child("grupos");

        grupoRef.child( getId() ).setValue(this);

        for (User membro : getMembros()){

            String idRemetent = Base64decod.encodBase64( membro.getEmail() );
            String idDestinatario = getId();

            Conversas conversa = new Conversas();
            conversa.setIdRemetente( idRemetent );
            conversa.setIdDestinatario( idDestinatario );
            conversa.setUltimaMenssagen("");
            conversa.setIsGroup("true");
            conversa.setGrupo(this);

            conversa.salvar();

        }

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public List<User> getMembros() {
        return membros;
    }

    public void setMembros(List<User> membros) {
        this.membros = membros;
    }
}
