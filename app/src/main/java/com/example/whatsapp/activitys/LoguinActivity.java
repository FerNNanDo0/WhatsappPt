package com.example.whatsapp.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.whatsapp.config.Firebase;
import com.example.whatsapp.R;
import com.example.whatsapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

public class LoguinActivity extends AppCompatActivity {

    private Button btnLoguin;
    private TextInputEditText input_email, input_senha;

    private FirebaseAuth Auth = Firebase.getAuthRef();
    //private DatabaseReference Database = Firebase.getDatabaseRef();


    @Override
    protected void onStart() {
        super.onStart();
        if( Auth.getCurrentUser() != null){
            Intent mainActivity = new Intent(this, MainActivity.class);
            startActivity( mainActivity );
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loguin);

        input_email = findViewById(R.id.log_email);
        input_senha = findViewById(R.id.log_senha);
        btnLoguin = findViewById(R.id.btn_log);
        btnLog();


    }

    // button loguin
    public void btnLog(){
        btnLoguin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEmail = input_email.getText().toString();
                String strSenha = input_senha.getText().toString();

                ///validar dados de log
                if ( !strEmail.isEmpty()){
                    if (!strSenha.isEmpty()){

                        //inicie a activity main se os dados de usuario estiver correto
                        logarUser(strEmail, strSenha);

                    }else{
                        Toast.makeText(getApplicationContext(),
                                "Informe sua senha de acesso",
                                Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(getApplicationContext(),
                            "Informe seu endereço de E-mail",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void logarUser(String strEmail, String strSenha){
        Auth.signInWithEmailAndPassword(
                strEmail,
                strSenha

        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if ( task.isSuccessful() ){

                    Intent intentMain = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity( intentMain );

                    finish();

                }else{
                    String exececao = "";
                    try {
                        throw Objects.requireNonNull(task.getException());
                    }catch ( FirebaseAuthInvalidUserException e){
                        exececao = "Usuário não está cadastrado";
                    }catch ( FirebaseAuthInvalidCredentialsException e){
                        exececao = "E-mail e senha não correspondem a um usuário cadastrado";
                    } catch (Exception e){
                        exececao = "Erro ao cadastrar usuário: "+e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(),
                            exececao, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // ir para tela de cadastro
    public void newActivityCadastro(View v){
        Intent intentCadastro = new Intent(this, CadastroActivity.class);
        startActivity( intentCadastro );
    }
}