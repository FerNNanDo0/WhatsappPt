package com.example.whatsapp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.whatsapp.R;
import com.example.whatsapp.activitys.ChatActivity;
import com.example.whatsapp.adapter.Adapter;
import com.example.whatsapp.config.Firebase;
import com.example.whatsapp.config.UserFirebase;
import com.example.whatsapp.helper.Base64decod;
import com.example.whatsapp.helper.RecyclerItemClickListener;
import com.example.whatsapp.model.Conversas;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressLint("NotifyDataSetChanged")
public class ConversasFragment extends Fragment {

    private View view;
    private RecyclerView recyclerViewConversas;
    private Adapter adapterConversas;
    private Context context;

    private String id;

    private FirebaseUser userAtual;
    private DatabaseReference databaseRef, conversasRef;

    private ChildEventListener childEventListener;

    private List<Conversas> listConversas = new ArrayList<>();

    public ConversasFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_coversas, container, false);

        recyclerViewConversas = view.findViewById(R.id.recyclerConversas);

        // referencia do database
        databaseRef = Firebase.getDatabaseRef();

        //config recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        recyclerViewConversas.setLayoutManager( layoutManager );
        recyclerViewConversas.setHasFixedSize(true);

        // config adapter
        adapterConversas = new Adapter( null, listConversas, context );
        recyclerViewConversas.setAdapter( adapterConversas );

        // config e ref de conversas
        id = UserFirebase.getIdUser();

        // config event de click no recyclerView
        recyclerViewConversas.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        context,
                        recyclerViewConversas,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Conversas conversaSelecionada = listConversas.get(position);

                                if ( conversaSelecionada.getIsGroup().equals("true") ){

                                    Intent i = new Intent(requireContext(), ChatActivity.class);
                                    i.putExtra( "chatG", conversaSelecionada.getGrupo() );
                                    startActivity( i );

                                }else{

                                    Intent i = new Intent(requireContext(), ChatActivity.class);
                                    i.putExtra( "chatContatos", conversaSelecionada.getUserExibicao() );
                                    startActivity( i );
                                }

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }
                )
        );

        //recuperarConversas();

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void recuperarConversas(){
        conversasRef = databaseRef.child("conversas").child( id );

        listConversas.clear();

        childEventListener = conversasRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Conversas conver = snapshot.getValue( Conversas.class );
                listConversas.add( conver );

                adapterConversas.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                Conversas conver = snapshot.getValue( Conversas.class );
//                listConversas.add( conver );

                adapterConversas.notifyDataSetChanged();
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

    // pesquizar conersas ou nome do contato
    public  void pesquisarConversas(String text){

        List<Conversas> listBusca = new ArrayList<>();

        for( Conversas conversa : listConversas ){

            String nome = conversa.getUserExibicao().getNome().toLowerCase();
            String ultimaMsg = conversa.getUltimaMenssagen().toLowerCase();

            if( nome.contains(text.toLowerCase()) || ultimaMsg.contains(text.toLowerCase()) ){
                listBusca.add( conversa );
            }
        }
        adapterConversas = new Adapter( null, listBusca,requireActivity() );
        recyclerViewConversas.setAdapter(adapterConversas);
        adapterConversas.notifyDataSetChanged();
    }

    // recarregar todas as conversas
    public void recarregarConversasAdapter(){
        // config adapter
        adapterConversas = new Adapter( null, listConversas, context );
        recyclerViewConversas.setAdapter( adapterConversas );
        adapterConversas.notifyDataSetChanged();
    }


    @Override
    public void onStart() {
        super.onStart();
        recuperarConversas();
    }

    @Override
    public void onStop() {
        super.onStop();
        databaseRef.removeEventListener( childEventListener );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseRef = null;
    }
}