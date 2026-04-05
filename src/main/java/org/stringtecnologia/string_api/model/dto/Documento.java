
package org.stringtecnologia.string_api.model.dto;



public class Documento{
    private String codigo;
    private String usuario;
    private String data;

    public String getCodigo(){
         return codigo;
    }

    public void setCodigo(String codigo){
        this.codigo = codigo;
    }

    public String getUsuario(){
        return usuario;
    }

    public void setUsuario(String usuario){
       this.usuario = usuario;
    }

    public String getData(){
       return data;
    }

    public void setData(String data){
    this.data = data;
    }
}