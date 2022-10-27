package com.example.whatsapp.activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.config.Firebase;
import com.example.whatsapp.config.UserFirebase;
import com.example.whatsapp.helper.Permissions;
import com.example.whatsapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressLint("QueryPermissionsNeeded")
public class ConfigUserActivity extends AppCompatActivity {

    private ImageButton btnImgCamera, btnImgGaleria;
    private CircleImageView img_Perfil_U;
    private EditText nomeUser;
    private ImageView atulizarNome;

    private static final int REQUEST_CODE_CAMERA = 100;
    private static final int REQUEST_CODE_GALLERY = 200;

    private String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private String idUser;
    private FirebaseUser user;
    private User userLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_user);

        nomeUser = findViewById(R.id.config_nome_user);
        img_Perfil_U = findViewById(R.id.img_user_perfil);
        btnImgCamera = findViewById(R.id.imageButtonCamera);
        btnImgGaleria = findViewById(R.id.imageButtonAddFotos);
        atulizarNome = findViewById(R.id.imageAtualizarNome);

        // validate permissions
        Permissions.validatePermissions(permissions, this, 1);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        idUser = UserFirebase.getIdUser();
        user = UserFirebase.getUser();
        Uri url = user.getPhotoUrl();

        userLogado = UserFirebase.getDadosUserLogado();

        if ( url != null ){
            Glide.with(this).load( url ).into( img_Perfil_U );
        }else{
            img_Perfil_U.setImageResource( R.drawable.padrao );
        }

        nomeUser.setText( user.getDisplayName() );

        // button open camera
        btnImgCamera.setOnClickListener(view -> {

            Intent cameraOpen = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraOpen, REQUEST_CODE_CAMERA);

            if (cameraOpen.resolveActivity(getPackageManager()) != null) {

            }
        });

        // button open galeria
        btnImgGaleria.setOnClickListener(view -> {

            Intent galeriaOpen = new Intent(
                    Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galeriaOpen, REQUEST_CODE_GALLERY);

            if (galeriaOpen.resolveActivity(getPackageManager()) != null) {

            }
        });

        atulizarNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nomeAtualizado = nomeUser.getText().toString();

                boolean retorno = UserFirebase.atulizarNomeUser( nomeAtualizado );
                if ( retorno ){

                    userLogado.setNome( nomeAtualizado );
                    userLogado.atualizar();

                    Toast.makeText(
                            ConfigUserActivity.this,
                            "Nome alterado com sucesso.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissionResult : grantResults) {
            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                alertValidatePermission();
            }
        }
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

                    case REQUEST_CODE_GALLERY:
                        assert data != null;

                        Uri img_selecionada = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), img_selecionada);
                        break;
                }

                if (image != null) {
                    img_Perfil_U.setImageBitmap(image);
                    salvarImgStorage(image);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void salvarImgStorage(Bitmap image) {

        // Recuperar dados de imagem para o firebase
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] dadosImg = baos.toByteArray();

        // salvar no storage Firebase
        final StorageReference imgRef = Firebase.getStorageRef()
                .child("imagens")
                .child("perfil")
                .child(idUser + ".jpeg");

        UploadTask uploadTask = imgRef.putBytes(dadosImg);

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

                imgRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        Uri url = task.getResult();
                        atulizarFotoPerfil(url);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        });
    }

    private void atulizarFotoPerfil(Uri url) {
        boolean retorno = UserFirebase.atulizarFotoUser(url);

        if (retorno ){
            userLogado.setFoto( url.toString() );
            userLogado.atualizar();
        }
    }

    // alerta de permissionValidate
    private void alertValidatePermission() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitas as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Comfirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}