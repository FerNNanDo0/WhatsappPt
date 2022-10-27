package com.example.whatsapp.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.whatsapp.config.Firebase;
import com.example.whatsapp.R;
import com.example.whatsapp.config.UserFirebase;
import com.example.whatsapp.helper.Base64decod;
import com.example.whatsapp.model.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private Button btnCadastrar;
    private TextInputEditText cad_nome, cad_email, cad_senha;

    private FirebaseAuth Auth = Firebase.getAuthRef();
    //private DatabaseReference Database = Firebase.getDatabaseRef();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        cad_nome = findViewById(R.id.cad_Nome);
        cad_email = findViewById(R.id.cad_Email);
        cad_senha = findViewById(R.id.cad_Senha);

        btnCadastrar = findViewById(R.id.button_Cadastrar);
        btnCadastrar();
    }


    public void btnCadastrar() {
        btnCadastrar.setOnClickListener(view -> {
            String strNome = cad_nome.getText().toString();
            String strEmail = cad_email.getText().toString();
            String strSenha = cad_senha.getText().toString();

            ///validar dados de cad
            if ( !strNome.isEmpty()) {
                if ( !strEmail.isEmpty()) {
                    if ( !strSenha.isEmpty()) {

                        //define os dados do user
                        User user = new User();

                        user.setNome( strNome );
                        user.setEmail( strEmail );
                        user.setSenha( strSenha );
                        user.setId( Base64decod.encodBase64( strEmail ) );

                        // cadastrar user no fireBase
                        criarUserFirebase( user );

                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Informe sua Senha de cadastro",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Informe seu endereço de E-mail para se cadastro",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        "Informe seu Nome para se cadastro",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void criarUserFirebase(User user){
        Auth.createUserWithEmailAndPassword(
                user.getEmail(),
                user.getSenha()

        ).addOnCompleteListener( task -> {
                    if ( task.isSuccessful() ){
                        // salve no dataBase
                        user.salvar();

//                        Intent intentMain = new Intent(getApplicationContext(), MainActivity.class);
//                        startActivity(intentMain);

                        UserFirebase.atulizarNomeUser( user.getNome() );

                        finish();
                    }else{

                        String execao = "";
                        try {
                            throw task.getException();
                        } catch ( FirebaseAuthWeakPasswordException e){
                            execao = "Digite uma senha mais forte!";
                        } catch ( FirebaseAuthInvalidCredentialsException e) {
                            execao = "Por favor, digite um e-mail válido";
                        } catch ( FirebaseAuthUserCollisionException e) {
                            execao = "Esta conta já foi cadastrada";
                        } catch (Exception e) {
                            execao = "Erro ao cadastrar usuário: "+ e.getMessage();
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(),
                                execao, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}