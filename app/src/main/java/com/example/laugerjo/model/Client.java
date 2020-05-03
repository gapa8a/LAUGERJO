package com.example.laugerjo.model;

public class Client extends Users{
    String id;
    String email;
    String lastname;
    String name;
    String password;
    String number;
    String identi;



    public Client(String id, String email, String lastname, String name, String password, String number,String identi) {
        this.id = id;
        this.email = email;
        this.lastname = lastname;
        this.name = name;
        this.password = password;
        this.number = number;
        this.identi = identi;
    }

    public Client() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
    public String getIdenti() {
        return identi;
    }

    public void setIdenti(String identi) {
        this.identi = identi;
    }
}
