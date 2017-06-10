package com.example.dawid.wtistemple;

/**
 * Created by Dawid on 09.06.2017.
 */

public class SzczegolyDokumentow {
    public int id;
    public String nazwa;
    public String timestamp;
    public String autor;
    public String linkPobierz;
    public SzczegolyDokumentow(int id, String nazwa, String timestamp, String autor, String linkPobierz)
    {
        this.id = id;
        this.nazwa  = nazwa;
        this.timestamp = timestamp;
        this.autor = autor;
        this.linkPobierz = linkPobierz;
    }

    public int getID() {
        return id;
    }
    public String getNazwa(){
        return  nazwa;
    }
    public String getTimestamp(){
        return  timestamp;
    }
    public  String getAutor(){
        return autor;
    }
    public String getLinkPobierz(){
        return linkPobierz;
    }
}
