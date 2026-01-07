package org.stringtecnologia.string_api.model.dto;

public class UserCreateDTO {

    private String name;
    private String email;

    public UserCreateDTO() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
