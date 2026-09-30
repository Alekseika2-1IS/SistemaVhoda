package org.example;

public class Polzovatel {
    public int id;
    public String login;
    public String rol;
    public boolean zablokirovan;
    public int oshibokPodryad;

    public Polzovatel(int id, String login, String rol, boolean zablokirovan, int oshibokPodryad) {
        this.id = id;
        this.login = login;
        this.rol = rol;
        this.zablokirovan = zablokirovan;
        this.oshibokPodryad = oshibokPodryad;
    }
}