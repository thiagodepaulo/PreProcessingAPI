package com.itera.structures;

import java.io.Serializable;

public class InputPattern implements Serializable {

    int id;
    String texto;
    String classe;

    public InputPattern(int id, String texto, String classe) {
        this.id = id;
        this.texto = texto;
        this.classe = classe;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public String toString() {
        return "(doc: " + id + ", class: " + this.classe + ", texto: " + this.texto + ")";
    }
}
