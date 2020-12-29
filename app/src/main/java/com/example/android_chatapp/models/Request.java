package com.example.android_chatapp.models;




public class Request {
    String name, status, image, thumbnail,device_token;

    public Request() { }
    public Request(String name, String status, String image, String thumbnail,String device_token) {
        this.name = name;
        this.status = status;
        this.image = image;
        this.thumbnail = thumbnail;
        this.device_token = device_token;
    }
    public String getDevice_token() { return device_token; }
    public void setDevice_token(String device_token) { this.device_token = device_token; }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getThumbnail() {
        return thumbnail;
    }
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
