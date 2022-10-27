package com.example.whatsapp.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.adapter.AdapterMenssagens;
import com.example.whatsapp.config.Firebase;
import com.example.whatsapp.config.UserFirebase;
import com.example.whatsapp.helper.Base64decod;
import com.example.whatsapp.model.Messages;
import com.example.whatsapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView nome;
    private CircleImageView fotoView;
    private EditText EditTextMessages;
    private ImageButton btnCamera;

    private static final int REQUEST_CODE_CAMERA = 100;

    RecyclerView recyclerView_Msgs;
    AdapterMenssagens adapterMensagens;
    List<Messages> listMsgs = new ArrayList<>();

    User userDestinatario;


    String idUserRemetente;
    String idDestinatario;

    DatabaseReference databaseRef;
    DatabaseReference mensagensRef;
    StorageReference storageRef;

    ChildEventListener childEventListenerMsg;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        nome = findViewById(R.id.textChatNomeUser);
        fotoView = findViewById(R.id.imgChatUser);
        EditTextMessages = findViewById(R.id.editTextMessage);
        btnCamera = findViewById(R.id.Btn_camera_chat);
        recyclerView_Msgs = findViewById(R.id.recycler_msgs);

        // config da toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar( toolbar );

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // config dados do usuario
        Bundle extra = getIntent().getExtras();
        if ( extra != null ){
            userDestinatario = (User) extra.getSerializable("userAtual");

            String urlFotoUser = userDestinatario.getFoto();
            String nomeUser = userDestinatario.getNome();

            //define nome do user atual
            nome.setText( nomeUser );

            if (urlFotoUser != null){
                Glide.with(this).load( urlFotoUser ).into(fotoView);
            }

        }

        // ids de users
        idUserRemetente = UserFirebase.getIdUser();
        idDestinatario = Base64decod.encodBase64( userDestinatario.getEmail() );

        // referencia do dataBase e storage
        databaseRef = Firebase.getDatabaseRef();
        storageRef = Firebase.getStorageRef();

        // configs adapter
        adapterMensagens = new AdapterMenssagens( listMsgs, this );

        // configs RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView_Msgs.setLayoutManager( layoutManager );
        recyclerView_Msgs.setHasFixedSize( true );
        recyclerView_Msgs.setAdapter(adapterMensagens);

    }

    public void btnCameraChat(View view){

        Intent cameraOpen = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraOpen, REQUEST_CODE_CAMERA);

    }

    // salvar imgs do chat no storage se a imagem != null
    public void savarImagesStorage(Bitmap image){

        // Recuperar dados de imagem para o firebase
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] dadosImg = baos.toByteArray();

        String idImage = UUID.randomUUID().toString();

        // salvar no storage Firebase
        final StorageReference imageRef = storageRef.child("images")
                .child("fotos")
                .child( idUserRemetente )
                .child( idImage + ".jpeg");

        // upLoad da foto para storage
        UploadTask uploadTask = imageRef.putBytes(dadosImg);
        uploadTask.addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Erro ao fazer download da imagem",
                        Toast.LENGTH_SHORT).show();
            }
        })
        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                Toast.makeText(getApplicationContext(), "Sucesso ao fazer download da imagem ",
                        Toast.LENGTH_LONG).show();


            }
        });


    }



    // method de enviar msgs
    public void sendMessage(View v){

        String txt_msg = EditTextMessages.getText().toString();
        Messages msg;

        if ( !txt_msg.isEmpty()){

            msg = new Messages();
            msg.setIdUsuarioAtual( idUserRemetente );
            msg.setMessage( txt_msg );

            // salvar MSGs para o remetente
            salvarMsgDatabase(idUserRemetente, idDestinatario, msg );

            // salvar para o destinatario
            salvarMsgDatabase(idDestinatario, idUserRemetente,msg );

            EditTextMessages.setText("");
        }


    }

    private void salvarMsgDatabase(String idUserRemetente, String idDestinatario, Messages msg){

        // refencia de mensagens
        DatabaseReference mensagensRef = databaseRef.child("mensagens");

        mensagensRef.child( idUserRemetente )
                .child( idDestinatario )
                .push()
                .setValue( msg );
    }

    @SuppressLint("NotifyDataSetChanged")
    private void recuperarMensagens(){

        mensagensRef = databaseRef.child("mensagens")
                .child( idUserRemetente )
                .child( idDestinatario  );

        childEventListenerMsg = mensagensRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Messages messages = snapshot.getValue( Messages.class );
                listMsgs.add( messages );
                adapterMensagens.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarMensagens();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagensRef.removeEventListener( childEventListenerMsg );
    }

    // click de menus
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

//        switch (item.getItemId()){
//            case android.R.id.home:
//                finish();
//        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap image = null;

            try {
                switch (requestCode) {
                    case REQUEST_CODE_CAMERA:
                        assert data != null;

                        image = (Bitmap) data.getExtras().get("data");
                        break;
                }

                if (image != null) {
                    savarImagesStorage(image);
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

}