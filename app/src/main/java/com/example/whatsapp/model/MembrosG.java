package com.example.whatsapp.model;

import java.io.Serializable;
import java.util.List;

public class MembrosG implements Serializable {

    static List<User> membros;

    public MembrosG() {
    }


    public static List<User> getMembros() {
        return membros;
    }

    public static void setMembros(List<User> membross) {
        membros = membross;
    }
}
