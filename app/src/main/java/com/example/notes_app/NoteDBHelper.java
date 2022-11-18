package com.example.notes_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NoteDBHelper extends SQLiteOpenHelper {

    public static final String ID = "ID";
    public static final String NOTES_TABLE = "NOTES_TABLE";
    public static final String TYPE = "TYPE";
    public static final String TITLE = "TITLE";
    public static final String CONTENT = "CONTENT";
    public static final String TAG = "TAG";
    public static final String DATE = "DATE";
    public static final String COLOR = "COLOR";
    private static final String ALARM = "ALARM";
    private static final String IS_HIDDEN = "IS_HIDDEN";


    public NoteDBHelper(@Nullable Context context) {
        super(context, "notas.db", null, 1);
    }

    //Este método es llamado la primera vez que accedemos a la base de datos
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + NOTES_TABLE + "(" +
                    ID +  " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TYPE + " TEXT, " +
                    TITLE + " TEXT, " +
                    CONTENT + " TEXT, " +
                    TAG + " TEXT, " +
                    DATE + " TEXT, " +
                    COLOR + " TEXT, " +
                    ALARM + " TEXT, " +
                    IS_HIDDEN + " TEXT" +
                ")";

        db.execSQL(createTableStatement);
    }

    //Este método es llamado si el número de versión de la DB cambia
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addNote(NoteElement noteItem){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TYPE, noteItem.getNoteType());
        cv.put(TITLE, noteItem.getNoteTitle());
        cv.put(CONTENT, noteItem.getNoteContent());
        cv.put(TAG, noteItem.getTag());
        cv.put(DATE, noteItem.getDate());
        cv.put(COLOR, noteItem.getColor());
        cv.put(ALARM, noteItem.getAlarm());
        cv.put(IS_HIDDEN, noteItem.isHidden());

        long insert = db.insert(NOTES_TABLE, null, cv);
        if (insert == -1){
            return false;
        }else{
            return true;
        }
    }

    public List<NoteElement> getVisibleNotes(){

        List<NoteElement> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + NOTES_TABLE + " WHERE " + IS_HIDDEN + " = 0";

        SQLiteDatabase db = this. getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            //Aquí se lee en un ciclo los resultados de la consulta para retornarlos por medio de returnList.
            do{
                int id = cursor.getInt(0);
                String type = cursor.getString(1);
                String title = cursor.getString(2);
                String content = cursor.getString(3);
                String tag = cursor.getString(4);
                String date = cursor.getString(5);
                String color = cursor.getString(6);
                String alarm = cursor.getString(7);
                boolean isHidden = cursor.getInt(8) == 1;

                NoteElement noteSearcher = new NoteElement(id, type, title, content, tag, date, color, alarm, isHidden);
                returnList.add(noteSearcher);

            }while(cursor.moveToNext());
        }
        else{
            //Fallo al intentar leer la lista
        }

        cursor.close();
        db.close();
        return returnList;
    }

    public List<NoteElement> getAllNotes(){

        List<NoteElement> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + NOTES_TABLE;

        SQLiteDatabase db = this. getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            //Aquí se lee en un ciclo los resultados de la consulta para retornarlos por medio de returnList.
            do{
                int id = cursor.getInt(0);
                String type = cursor.getString(1);
                String title = cursor.getString(2);
                String content = cursor.getString(3);
                String tag = cursor.getString(4);
                String date = cursor.getString(5);
                String color = cursor.getString(6);
                String alarm = cursor.getString(7);
                boolean isHidden = cursor.getInt(8) == 1 ?  true: false;

                NoteElement noteSearcher = new NoteElement(id, type, title, content, tag, date, color, alarm, isHidden);
                returnList.add(noteSearcher);

            }while(cursor.moveToNext());
        }
        else{
            //Fallo al intentar leer la lista
        }

        cursor.close();
        db.close();
        return returnList;
    }

    public NoteElement getNote(NoteElement noteItem){

        NoteElement selectedNote;
        String queryString = "SELECT * FROM " + NOTES_TABLE + " WHERE " + "ID = " + noteItem.getId();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);
        if(!cursor.isNull(0)){
            int id = cursor.getInt(0);
            String type = cursor.getString(1);
            String title = cursor.getString(2);
            String content = cursor.getString(3);
            String tag = cursor.getString(4);
            String date = cursor.getString(5);
            String color = cursor.getString(6);
            String alarm = cursor.getString(7);
            boolean isHidden = cursor.getInt(8) == 1;
            selectedNote = new NoteElement(id, type, title, content, tag, date, color, alarm, isHidden);
        }
        else{
            selectedNote = null;
        }
        cursor.close();
        db.close();
        return selectedNote;
    }

    public boolean updateNote(NoteElement noteItem){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TYPE, noteItem.getNoteType());
        cv.put(TITLE, noteItem.getNoteTitle());
        cv.put(CONTENT, noteItem.getNoteContent());
        cv.put(TAG, noteItem.getTag());
        cv.put(DATE, noteItem.getDate());
        cv.put(COLOR, noteItem.getColor());
        cv.put(ALARM, noteItem.getAlarm());
        cv.put(IS_HIDDEN, noteItem.isHidden() ? 1:0);

        long update = db.update(NOTES_TABLE, cv, ID + " = " + noteItem.getId(), null);
        if (update == -1){
            return false;
        }else{
            return true;
        }
    }

    public boolean deleteNote(NoteElement noteItem){
        SQLiteDatabase db = this.getWritableDatabase();
        int delete = db.delete(NOTES_TABLE, " ID = " + noteItem.getId(), null);
        if (delete == -1){
            return false;
        }else{
            return true;
        }
    }

    public List<NoteElement> getHiddenNotes() {
        List<NoteElement> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + NOTES_TABLE + " WHERE " + IS_HIDDEN + " = 1";

        SQLiteDatabase db = this. getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            //Aquí se lee en un ciclo los resultados de la consulta para retornarlos por medio de returnList.
            do{
                int id = cursor.getInt(0);
                String type = cursor.getString(1);
                String title = cursor.getString(2);
                String content = cursor.getString(3);
                String tag = cursor.getString(4);
                String date = cursor.getString(5);
                String color = cursor.getString(6);
                String alarm = cursor.getString(7);
                boolean isHidden = cursor.getInt(8) == 1;

                NoteElement noteSearcher = new NoteElement(id, type, title, content, tag, date, color, alarm, isHidden);
                returnList.add(noteSearcher);

            }while(cursor.moveToNext());
        }
        else{
            //Fallo al intentar leer la lista
        }

        cursor.close();
        db.close();
        return returnList;
    }
}
