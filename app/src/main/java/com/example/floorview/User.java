package com.example.floorview;

public class User
{
    public String _id;
    public String _mail;
    public String _librarianCode;
    public User(String ID,String mail,String lc)
    {
        _id=ID;
        _mail = mail;
        _librarianCode = lc;
    }
    public User(User user)
    {
        this._librarianCode = user._librarianCode;
        this._id = user._id;
        this._mail = user._mail;
    }
    public User()
    {
        this._librarianCode = "";
        this._id = "00000000";
        this._mail = "null@null";
    }
}