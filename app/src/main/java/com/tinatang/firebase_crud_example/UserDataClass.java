package com.tinatang.firebase_crud_example;

public class UserDataClass {
    String Id, Name, New_Image;

    public UserDataClass(){}

    public UserDataClass(String Id, String Name, String New_Image) {
        this.Id = Id;
        this.Name = Name;
        this.New_Image = New_Image;
    }

    public String getId() {
        return Id;
    }

    public String setId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public String setName() {
        return Name;
    }

    public String getNew_Image() {
        return New_Image;
    }

    public String setNew_Image() {
        return New_Image;
    }
}


