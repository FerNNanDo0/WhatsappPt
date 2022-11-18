package com.example.whatsapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.whatsapp.config.Firebase;
import com.example.whatsapp.config.UserFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class User implements Parcelable {

    private String nome;
    private String email;
    private String senha;
    private String id;
    private String foto;

    private DatabaseReference databaseReference;

    public User(){

    }

    protected User(Parcel in) {
        nome = in.readString();
        email = in.readString();
        senha = in.readString();
        id = in.readString();
        foto = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public void salvar(){
        databaseReference = Firebase.getDatabaseRef();
        databaseReference
                .child("usuarios")
                .child( getId() )
                .setValue( this );
    }

    public void atualizar(){
        String idUser = UserFirebase.getIdUser();
        DatabaseReference database = Firebase.getDatabaseRef();

        DatabaseReference userRef = database
                .child("usuarios")
                .child( idUser );

        Map<String, Object> valorUser = coverterParaMap();

        userRef.updateChildren( valorUser );

    }

    @Exclude
    public Map<String, Object> coverterParaMap(){
        HashMap<String, Object> usuarioMap = new HashMap<>();

        usuarioMap.put( "email", getEmail() );
        usuarioMap.put( "nome", getNome() );
        usuarioMap.put( "foto", getFoto() );

        return usuarioMap;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Exclude
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nome);
        parcel.writeString(email);
        parcel.writeString(senha);
        parcel.writeString(id);
        parcel.writeString(foto);
    }
}
