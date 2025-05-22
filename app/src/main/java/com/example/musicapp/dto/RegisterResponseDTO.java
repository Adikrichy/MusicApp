package com.example.musicapp.dto;

public class RegisterResponseDTO {
    private String status;
    private String message;

    public RegisterResponseDTO(){

    }


    public String getMessage(){
        return message;
    }
    public void setMessage(String message){
        this.message=message;
    }

    public void setStatus(String status){
        this.status=status;
    }

    public String getStatus(){
        return status;
    }
}
