package com.example.whatsapp.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.whatsapp.R;
import com.example.whatsapp.config.Firebase;
import com.example.whatsapp.fragments.ContatosFragment;
import com.example.whatsapp.fragments.ConversasFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth Auth = Firebase.getAuthRef();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        //config abas
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
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

    }


    // menu config
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // clicks de menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch ( item.getItemId()){

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