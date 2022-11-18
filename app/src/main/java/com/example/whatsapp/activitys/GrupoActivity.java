package com.example.whatsapp.activitys;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.example.whatsapp.adapter.Adapter;
import com.example.whatsapp.adapter.AdapterMembrosSelecionados;
import com.example.whatsapp.config.Firebase;
import com.example.whatsapp.config.UserFirebase;
import com.example.whatsapp.helper.RecyclerItemClickListener;
import com.example.whatsapp.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("NotifyDataSetChanged")
public class GrupoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerContatos, recyclerMenbroSelect;
    private RecyclerView.LayoutManager layoutManager, layoutManagerHorizontal;

    private Adapter adapterGrup;
    private AdapterMembrosSelecionados adapterMembrosSelecionados;

    private List<User> listaContatos = new ArrayList<>();
    private List<User> listaMenbrosEscolhidos = new ArrayList<>();

    private ValueEventListener valueEventListener;
    private DatabaseReference databaseRef;

    private FirebaseUser userAtual;
    private String emailUserAtual;
    private FloatingActionButton fab;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseRef.removeEventListener( valueEventListener );
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);

        fab = findViewById(R.id.floatingFab);

        toolbar =  findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        if( getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // referencia do database
        databaseRef = Firebase.getDatabaseRef();
        userAtual = UserFirebase.getUser();
        emailUserAtual = userAtual.getEmail();

        // config Adapter contatos
        adapterGrup = new Adapter( listaContatos, null, this);

        // recycler de contatos para add no grupo
        recyclerContatos = findViewById(R.id.recyclerViewContatos);

        layoutManager = new LinearLayoutManager(this );
        recyclerContatos.setLayoutManager( layoutManager );
        recyclerContatos.setHasFixedSize(true);
        recyclerContatos.setAdapter( adapterGrup );
        clickListenerRecyclerContatos();

        // ----------||---------\\
        // config adapter membros selecionados
        adapterMembrosSelecionados = new AdapterMembrosSelecionados(listaMenbrosEscolhidos, this);

        // recycler de membros que foram selecionados
        recyclerMenbroSelect = findViewById(R.id.recyclerViewMenbros);

        layoutManagerHorizontal = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerMenbroSelect.setLayoutManager(layoutManagerHorizontal);
        recyclerMenbroSelect.setHasFixedSize(true);
        recyclerMenbroSelect.setAdapter( adapterMembrosSelecionados );
        clickListenerMenbrosSelect();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ( !listaMenbrosEscolhidos.isEmpty() ){

                    Bundle bundle = new Bundle();
                    bundle.putSerializable( "membros", (Serializable) listaMenbrosEscolhidos);

                    Intent i = new Intent(getApplicationContext(), CadastroGrupoActivity.class);
                    i.putExtras( bundle );
                    startActivity(i);

                    finish();

                }else{
                    Toast.makeText(getApplicationContext(),
                            "Escolha ao menos um membro para o grupo", Toast.LENGTH_SHORT).show();
                }

            }
        });

        recuperarUsers();

    }

    public void atulizarToobar(){

        int totalSelected = listaMenbrosEscolhidos.size();

        int total = listaContatos.size() + totalSelected;
        toolbar.setSubtitle(totalSelected + " de "+ total + "selecionados");
    }


    // recuperar usuarios para o grupo
    public void recuperarUsers(){

        valueEventListener = databaseRef.child("usuarios")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listaContatos.clear();

                        for (DataSnapshot userFirebase : snapshot.getChildren() ){

                            User user = userFirebase.getValue( User.class );
                            if ( !emailUserAtual.equals( user.getEmail() ) ){
                                listaContatos.add( user );
                            }
                        }
                        adapterGrup.notifyDataSetChanged();
                        atulizarToobar();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    public void clickListenerRecyclerContatos(){
        recyclerContatos.addOnItemTouchListener( new RecyclerItemClickListener(
                this, recyclerContatos,
                new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {

                        User usuarioSelecionado = listaContatos.get(position);

                        // remover user selecionado da lista
                        listaContatos.remove( usuarioSelecionado );
                        adapterGrup.notifyDataSetChanged();


                        // adiciona usuario a nova lista de selecionados
                        listaMenbrosEscolhidos.add( usuarioSelecionado );
                        adapterMembrosSelecionados.notifyDataSetChanged();
                        atulizarToobar();


                    }
                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }));
    }

    public void clickListenerMenbrosSelect(){
        recyclerMenbroSelect.addOnItemTouchListener( new RecyclerItemClickListener(
                this, recyclerMenbroSelect,
                new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {

                        User usuarioSelecionado = listaMenbrosEscolhidos.get(position);

                        // remover user selecionado da lista
                        listaMenbrosEscolhidos.remove( usuarioSelecionado );
                        adapterMembrosSelecionados.notifyDataSetChanged();

                        // adiciona usuario a nova lista de contatos
                        listaContatos.add( usuarioSelecionado );
                        adapterGrup.notifyDataSetChanged();
                        atulizarToobar();

                    }
                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }));


    }


}