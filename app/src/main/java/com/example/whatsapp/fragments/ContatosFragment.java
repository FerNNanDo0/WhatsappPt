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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.whatsapp.R;
import com.example.whatsapp.activitys.ChatActivity;
import com.example.whatsapp.adapter.Adapter;
import com.example.whatsapp.adapter.AdapterContatos;
import com.example.whatsapp.config.Firebase;
import com.example.whatsapp.config.UserFirebase;
import com.example.whatsapp.helper.RecyclerItemClickListener;
import com.example.whatsapp.model.Conversas;
import com.example.whatsapp.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NotifyDataSetChanged")
public class ContatosFragment extends Fragment {

    private View view;
    private RecyclerView recyclerContatos;

    private List<User> listaContatos = new ArrayList<>();

    private Context context;
    private Adapter adapterContatos;

    private DatabaseReference databaseRef;
    private ValueEventListener valueEventListener;
//    private ChildEventListener childEventListener;
    private FirebaseUser userAtual;
    private String emailUserAtual;

    public ContatosFragment() {
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
        view = inflater.inflate(R.layout.fragment_contatos, container, false);

        recyclerContatos = view.findViewById(R.id.recyclerContatos);

        userAtual = UserFirebase.getUser();
        emailUserAtual = userAtual.getEmail();

        // referencia do database
        databaseRef = Firebase.getDatabaseRef();

        // config recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        recyclerContatos.setLayoutManager( layoutManager );
        recyclerContatos.setHasFixedSize(true);

        // config adapterContatos
        adapterContatos = new Adapter( listaContatos, null, context );
        recyclerContatos.setAdapter( adapterContatos );

        // config event de click no recyclerView
        recyclerContatos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        context,
                        recyclerContatos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                User userAtual = listaContatos.get(position);

                                Intent i = new Intent(requireContext(), ChatActivity.class);
                                i.putExtra("userAtual", userAtual);
                                startActivity( i );
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

        recuperarUsers();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        databaseRef.removeEventListener( valueEventListener );
    }

    public void recuperarUsers(){

        valueEventListener = databaseRef.child("usuarios")
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userFirebase : snapshot.getChildren() ){
                    User user = userFirebase.getValue( User.class );
                    if ( !emailUserAtual.equals( user.getEmail() ) ){
                        listaContatos.add( user );
                    }
                }
                adapterContatos.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


}