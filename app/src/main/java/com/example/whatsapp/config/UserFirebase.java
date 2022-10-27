package com.example.whatsapp.config;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.whatsapp.helper.Base64decod;
import com.example.whatsapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


public class UserFirebase {

    static String email;
    static String id;
    static FirebaseAuth user;

    public static String getIdUser() {

        FirebaseAuth user = Firebase.getAuthRef();
        assert user.getCurrentUser() != null;

        email = user.getCurrentUser().getEmail();
        id = Base64decod.encodBase64(email);

        return id;
    }


    public static FirebaseUser getUser() {

        user = Firebase.getAuthRef();
        return user.getCurrentUser();
    }


    public static boolean atulizarNomeUser(String nome) {

        try {
            FirebaseUser user = getUser();

            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nome)
                    .build();

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (!task.isSuccessful()) {
                                Log.d("PERFIL", "Erro ao atulizar");
                            }

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static User getDadosUserLogado(){

        FirebaseUser firebaseUser = getUser();

        User user = new User();
        user.setEmail( firebaseUser.getEmail() );
        user.setNome( firebaseUser.getDisplayName() );

        if ( firebaseUser.getPhotoUrl() == null ){

            user.setFoto("");

        }else {
            user.setFoto( firebaseUser.getPhotoUrl().toString() );
        }
        return user;
    }

    public static boolean atulizarFotoUser(Uri url) {

        try {
            FirebaseUser user = getUser();

            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(url)
                    .build();

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (!task.isSuccessful()) {
                                Log.d("PERFIL", "Erro ao atulizar");
                            }

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}
