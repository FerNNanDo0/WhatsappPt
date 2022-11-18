package com.example.whatsapp.activitys;

//import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
//import android.view.View;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.example.whatsapp.adapter.AdapterMembrosSelecionados;
import com.example.whatsapp.config.Firebase;
import com.example.whatsapp.config.UserFirebase;
import com.example.whatsapp.model.Grupo;
import com.example.whatsapp.model.MembrosG;
import com.example.whatsapp.model.User;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CadastroGrupoActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_GALLERY = 200;

    Toolbar toolbar;
    List<User> listaMembros = new ArrayList<>();

    FloatingActionButton fab;

    TextView textTotal;
    CircleImageView img_grupo;
    EditText textNome;

    LinearLayoutManager layoutManagerHorizontal;

    RecyclerView  recyclerMenbroSelect;
    AdapterMembrosSelecionados adapterMembrosSelecionados;

    Grupo grupo;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_grupo);

        textTotal = findViewById(R.id.textParticipantes);
        img_grupo = findViewById(R.id.img_grupo);
        textNome = findViewById(R.id.nomeGrupo);
        recyclerMenbroSelect = findViewById(R.id.membrosGrupoRecyclerView);
        toolbar =  findViewById(R.id.toolbar_main);
        fab = findViewById(R.id.floatingActionCheck);

        grupo = new Grupo();

        // config toolbar
        setSupportActionBar(toolbar);
        if( getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setSubtitle("Defina o nome...");
        }

        // recuperar lista de membros
        if ( getIntent().getExtras() != null ){

            List<User> membros = (List<User>) getIntent().getExtras().get("membros");
            listaMembros.addAll( membros );

            textTotal.setText( "Participantes "+ listaMembros.size() );
        }

        // define imagem do novo grupo
        img_grupo.setOnClickListener(view -> {

            Intent galeriaOpen = new Intent(
                    Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galeriaOpen, REQUEST_CODE_GALLERY);

        });

        // config adapter
        adapterMembrosSelecionados = new AdapterMembrosSelecionados( listaMembros, this);

        // configurar Recyclerview
        layoutManagerHorizontal = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerMenbroSelect.setLayoutManager(layoutManagerHorizontal);
        recyclerMenbroSelect.setHasFixedSize(true);
        recyclerMenbroSelect.setAdapter( adapterMembrosSelecionados );

        // button float event click
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listaMembros.add( UserFirebase.getDadosUserLogado() );

                String nomeGrupo = textNome.getText().toString();

                if ( !nomeGrupo.isEmpty() ){

                    grupo.setNome( nomeGrupo );
                    grupo.setMembros( listaMembros );
                    grupo.salvar();

                    // salvar membros em outra classe
                    MembrosG.setMembros( grupo.getMembros() );

                    Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                    i.putExtra( "chatG", grupo );
                    startActivity( i );

                    Toast.makeText(getApplicationContext(),
                            "total de membros "+ MembrosG.getMembros().size(), Toast.LENGTH_SHORT).show();

                    finish();

                }else{
                    Toast.makeText(getApplicationContext(),
                            "Informe um nome para o Grupo", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == RESULT_OK ){
            Bitmap imagem;

            try{

                Uri localImagemSelect = data.getData();
                imagem = MediaStore.Images.Media.getBitmap( getContentResolver(), localImagemSelect );

                if (imagem != null){
                    img_grupo.setImageBitmap( imagem );

                    // Recuperar dados de imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImg = baos.toByteArray();

                    // salvar no storage Firebase
                    final StorageReference imgRef = Firebase.getStorageRef()
                            .child("imagens")
                            .child("grupos")
                            .child( "" + ".jpeg");

                    UploadTask uploadTask = imgRef.putBytes(dadosImg);

                    uploadTask.addOnFailureListener( e -> Toast.makeText(getApplicationContext(), "Erro ao fazer download da imagem",
                            Toast.LENGTH_SHORT).show())
                            .addOnCompleteListener(task -> {

                                Toast.makeText(getApplicationContext(), "Sucesso ao fazer download da imagem ",
                                        Toast.LENGTH_LONG).show();

                                imgRef.getDownloadUrl().addOnCompleteListener(task1 -> {

                                    Uri url = task1.getResult();
                                    grupo.setFoto( url.toString() );

                                }).addOnFailureListener(e -> {

                                });

                            });


                }

            }catch (Exception e){
                e.printStackTrace();
            }


        }




    }
}