package com.example.whatsapp.activitys;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView nome;
    private CircleImageView fotoView;
    private EditText EditTextMessages;
    private ImageButton btnCamera;

    RecyclerView recyclerView_Msgs;
    AdapterMenssagens adapterMensagens;
    List<Messages> listMsgs = new ArrayList<>();

    User userDestinatario;

    String idUserRemetente;
    String idDestinatario;

    DatabaseReference database;
    DatabaseReference mensagensRef;

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

        // referencia do dataBase
        database = Firebase.getDatabaseRef();

        // configs adapter
        adapterMensagens = new AdapterMenssagens( listMsgs, this );

        // configs RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView_Msgs.setLayoutManager( layoutManager );
        recyclerView_Msgs.setHasFixedSize( true );
        recyclerView_Msgs.setAdapter(adapterMensagens);
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
        DatabaseReference mensagensRef = database.child("mensagens");

        mensagensRef
                .child( idUserRemetente )
                .child( idDestinatario )
                .push()
                .setValue( msg );
    }

    @SuppressLint("NotifyDataSetChanged")
    private void recuperarMensagens(){

        mensagensRef = database.child("mensagens")
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
}