package com.example.whatsapp.activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.example.whatsapp.helper.Permissions;
import com.example.whatsapp.model.Conversas;
import com.example.whatsapp.model.Grupo;
import com.example.whatsapp.model.MembrosG;
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

    User userSelecionado;
    Grupo grupo;

    Messages msg;

    String idUserRemetente;
    String idDestinatario;

    DatabaseReference conversasRef;
    DatabaseReference databaseRef;
    DatabaseReference mensagensRef;
    StorageReference storageRef;

    ChildEventListener childEventListenerMsg;


    // permissions
    private String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

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

            if( extra.containsKey( "chatG" )){

                grupo = (Grupo) extra.getParcelable("chatG");
                idDestinatario = grupo.getId();

                String urlFotoGrupo = grupo.getFoto();

                //define nome do user atual
                nome.setText( grupo.getNome() );

                if (urlFotoGrupo != null){
                    Glide.with(this).load( urlFotoGrupo ).into(fotoView);
                }

            }else{
                userSelecionado = (User) extra.getParcelable("chatContatos");

                //define nome do user atual
                nome.setText( userSelecionado.getNome() );

                if ( userSelecionado.getFoto() != null ){
                    Glide.with(this).load( userSelecionado.getFoto() ).into(fotoView);
                }

                idDestinatario = Base64decod.encodBase64( userSelecionado.getEmail() );
            }

        }

        // ids de users
        idUserRemetente = UserFirebase.getIdUser();

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

        // validate permissions
        Permissions.validatePermissions(permissions, this, 1);

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
        final StorageReference imageRef = storageRef.child("imagens")
                .child("fotosChat")
                .child( idUserRemetente )
                .child( idDestinatario )
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

                imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        String downloadUrlImg = task.getResult().toString();

                        Messages message = new Messages();
                        message.setIdUsuarioAtual( idUserRemetente );
                        message.setMessage( "imagem.jpeg" );
                        message.setFoto( downloadUrlImg );

                        // salvar para remetente
                        message.salvarMsgDatabase( idUserRemetente, idDestinatario, message );

                        Toast.makeText(getApplicationContext(), "Sucesso ao enviar imagem ",
                                Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


            }
        });


    }

    public void salvarConversas(String idUserRemetente, String idDestinatario, User userExibicao, Messages msg, boolean isGroup){
        Conversas conversaRemetente = new Conversas();

        // salvar conversas remetente
        conversaRemetente.setIdRemetente( idUserRemetente );
        conversaRemetente.setIdDestinatario( idDestinatario );
        conversaRemetente.setUltimaMenssagen( msg.getMessage() );

        if(isGroup){

            conversaRemetente.setIsGroup("true");
            conversaRemetente.setGrupo( grupo );

        }else{
            conversaRemetente.setUserExibicao(userExibicao);
            conversaRemetente.setIsGroup("false");
        }

        conversaRemetente.salvar();
    }



    // method de enviar msgs
    public void sendMessage(View v){

        String txt_msg = EditTextMessages.getText().toString();

        if ( !txt_msg.isEmpty()){

            msg = new Messages();

            if( userSelecionado != null){

                msg.setIdUsuarioAtual( idUserRemetente );
                msg.setMessage( txt_msg );

                // salvar MSGs para o remetente
                msg.salvarMsgDatabase(idUserRemetente, idDestinatario, msg );

                //   salvar MSGs para o destinatario
                msg.salvarMsgDatabase(idDestinatario, idUserRemetente, msg );

                // salvar conversas para remetente
                salvarConversas( idUserRemetente, idDestinatario,  userSelecionado, msg, false );

                // salvar conversas para destinatario
                User userRemetente = UserFirebase.getDadosUserLogado();
                salvarConversas( idDestinatario, idUserRemetente,  userRemetente, msg, false );

                EditTextMessages.setText("");

            }else{

                String idUserLogadoGrupo = UserFirebase.getIdUser();

                if( MembrosG.getMembros() != null ){

                    //MembrosG.getMembros()
                    for ( User membro : MembrosG.getMembros() ){

                        String idRemetenteGrupo = Base64decod.encodBase64( membro.getEmail() );

                        msg.setIdUsuarioAtual( idUserLogadoGrupo );
                        msg.setMessage( txt_msg );
                        msg.setNome( membro.getNome() );

                        // salvar msg para o membro do grupo
                        msg.salvarMsgDatabase(idRemetenteGrupo, idDestinatario, msg );

                        // salvar conversas para membros do grupo
                        salvarConversas( idRemetenteGrupo, idDestinatario, membro, msg, true);

                        EditTextMessages.setText("");
                    }
                }

            }

        }else{
            Toast.makeText(getApplicationContext(), "Escreva uma menssagem!",
                    Toast.LENGTH_LONG).show();
        }


    }


    @SuppressLint("NotifyDataSetChanged")
    private void recuperarMensagens(){

        listMsgs.clear();

        // referencia menssagens
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

//                Messages messages = snapshot.getValue( Messages.class );
//                listMsgs.add( messages );
                adapterMensagens.notifyDataSetChanged();

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