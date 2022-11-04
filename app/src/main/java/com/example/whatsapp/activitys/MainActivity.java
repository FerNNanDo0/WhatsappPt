package com.example.whatsapp.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.whatsapp.R;
import com.example.whatsapp.config.Firebase;
import com.example.whatsapp.fragments.ContatosFragment;
import com.example.whatsapp.fragments.ConversasFragment;
import com.example.whatsapp.model.Conversas;
import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth Auth = Firebase.getAuthRef();
    private MaterialSearchView searchView;
    private FragmentPagerItemAdapter adapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar =  findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        if( getSupportActionBar() != null){
            getSupportActionBar().setElevation(0);
        }

        //config abas
        adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                .add( "Conversas", ConversasFragment.class )
                .add( "Contatos", ContatosFragment.class)
                .create()
        );

        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter( adapter );

        SmartTabLayout viewPagerTab = findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager( viewPager );

        searchView = findViewById(R.id.search_viewM);

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                ConversasFragment fragmentConv = (ConversasFragment) adapter.getPage(0);
                // recarregar conversas
                fragmentConv.recarregarConversasAdapter();
            }
        });

                // listener caixa de texto
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if ( newText != null & !newText.isEmpty() ){
                    ConversasFragment fragmentConv = (ConversasFragment) adapter.getPage(0);
                    fragmentConv.pesquisarConversas( newText );
                }


                return true;
            }
        });

    }


    // menu config
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // config btn de pesquisa searshView
        MenuItem item = menu.findItem(R.id.app_bar_search);
        searchView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }
    // clicks de menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch ( item.getItemId()){

            //case R.id.app_bar_search:

            case R.id.sair_doApp:
                desLogarUser();
                break;

            case R.id.configUser:
                configUser();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    //abrir log
    public void activityLog(){
        Intent activityLog = new Intent(this, LoguinActivity.class);
        startActivity( activityLog );
    }
    /// deslogarUser
    public void desLogarUser(){
        try{
            Auth.signOut();
            activityLog();
            finish();
        }catch (Exception e){
            e.getMessage();
        }
    }

    public void configUser(){
        Intent activity_config = new Intent(this, ConfigUserActivity.class);
        startActivity( activity_config );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}