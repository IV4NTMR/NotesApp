package com.example.notes_app;

import java.io.Serializable;

//Esta clase es el modelo de la nota

public class NoteElement implements Serializable {


    public int id;
    public String noteType;
    public String noteTitle;
    public String noteContent;
    public String tag;
    public String date;
    public String color;
    public String alarm;



    public boolean isHidden;


    //Constuctor del modelo nota default
    public NoteElement(String color, String noteType, String noteTitle, String date, String tag, String noteContent) {
        this.id = -1;
        this.color = color;
        this.noteType = noteType;
        this.noteTitle = noteTitle;
        this.date = date;
        this.tag = tag;
        this.noteContent = noteContent;
        this.alarm = "";
        this.isHidden = false;
    }

    //Constructor del modelo nota completo
    public NoteElement(int id, String type, String title, String content, String tag, String date, String color, String alarm, boolean isHideen){
        this.id = id;
        this.noteType = type;
        this.noteTitle = title;
        this.noteContent = content;
        this.tag = tag;
        this.date = date;
        this.color = color;
        this.alarm = alarm;
        this.isHidden = isHideen;
    }

    @Override
    public String toString() {
        return "NoteElement{" +
                "id=" + id +
                ", color='" + color + '\'' +
                ", noteType='" + noteType + '\'' +
                ", noteTitle='" + noteTitle + '\'' +
                ", date='" + date + '\'' +
                ", tag='" + tag + '\'' +
                ", noteContent='" + noteContent + '\'' +
                ", alarm='" + alarm + '\'' +
                ", isHidden='" + isHidden + '\'' +
                '}';
    }

    //Getters y Setters

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getAlarm() {return alarm;}

    public void setAlarm(String alarm) {this.alarm = alarm;}

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNoteType() {
        return noteType;
    }

    public void setNoteType(String noteType) {
        this.noteType = noteType;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

}
