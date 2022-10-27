package com.example.whatsapp.helper;

import android.util.Base64;

import java.nio.charset.StandardCharsets;

public class Base64decod {

    public static String encodBase64(String tx){

        return Base64
                .encodeToString(tx.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT)
                .replaceAll("\\n|\\r","");
    }

    public static String decodBase64(String tx){

        return new String( Base64.decode( tx, Base64.DEFAULT ) );
    }


}
