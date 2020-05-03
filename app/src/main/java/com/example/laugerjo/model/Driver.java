package com.example.laugerjo.model;

import java.util.Date;

public class Driver extends Users {

    String placa;
    String marca;
    String modelo;
    int año;
    int puertas;
    Date fechaNa;
    String categoriaD;
    Date vigenciaDriver;
    Date vigenciaTarjetap;
    Date vigenciaSoat;
    String antecedentes;
    Date vigenciaTecno;

    public Driver(String id, String email, String lastname, String name, String password, String number, String identi, String placa, String marca, String modelo,int año, int puertas, Date fechaNa, String categoriaD, Date vigenciaDriver, Date vigenciaTarjetap, Date vigenciaSoat, String antecedentes, Date vigenciaTecno) {
        super(id, email, lastname, name, password, number, identi);
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.año = año;
        this.puertas = puertas;
        this.fechaNa = fechaNa;
        this .categoriaD = categoriaD;
        this.vigenciaDriver = vigenciaDriver;
        this.vigenciaTarjetap = vigenciaTarjetap;
        this.vigenciaSoat = vigenciaSoat;
        this.antecedentes = antecedentes;
        this.vigenciaTecno = vigenciaTecno;
    }




    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAño() {
        return año;
    }

    public void setAño(int año) {
        this.año = año;
    }

    public int getPuertas() {
        return puertas;
    }

    public void setPuertas(int puertas) {
        this.puertas = puertas;
    }

    public Date getFechaNa() {
        return fechaNa;
    }

    public void setFechaNa(Date fechaNa) {
        this.fechaNa = fechaNa;
    }

    public String getCategoriaD() {
        return categoriaD;
    }

    public void setCategoriaD(String categoriaD) {
        this.categoriaD = categoriaD;
    }

    public Date getVigenciaDriver() {
        return vigenciaDriver;
    }

    public void setVigenciaDriver(Date vigenciaDriver) {
        this.vigenciaDriver = vigenciaDriver;
    }

    public Date getVigenciaTarjetap() {
        return vigenciaTarjetap;
    }

    public void setVigenciaTarjetap(Date vigenciaTarjetap) {
        this.vigenciaTarjetap = vigenciaTarjetap;
    }

    public Date getVigenciaSoat() {
        return vigenciaSoat;
    }

    public void setVigenciaSoat(Date vigenciaSoat) {
        this.vigenciaSoat = vigenciaSoat;
    }

    public String getAntecedentes() {
        return antecedentes;
    }

    public void setAntecedentes(String antecedentes) {
        this.antecedentes = antecedentes;
    }

    public Date getVigenciaTecno() {
        return vigenciaTecno;
    }

    public void setVigenciaTecno(Date vigenciaTecno) {
        this.vigenciaTecno = vigenciaTecno;
    }
}
