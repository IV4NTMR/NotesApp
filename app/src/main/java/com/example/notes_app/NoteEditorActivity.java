package com.example.notes_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoteEditorActivity extends AppCompatActivity {

    int id_nota;
    EditText title;
    EditText content;
    TextView tag;
    TextView date;
    ImageButton edit;
    Button save;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        NoteElement noteItem = (NoteElement) getIntent().getSerializableExtra("NoteItem");

        edit = findViewById(R.id.editNoteBtn);
        save = findViewById(R.id.saveNoteBtn);

        title = findViewById(R.id.noteTitle);
        content = findViewById(R.id.noteContent);
        tag = findViewById(R.id.noteTag);
        date = findViewById(R.id.noteDate);

        if(noteItem != null){
            id_nota = noteItem.getId();
            title.setText(noteItem.getNoteTitle());
            content.setText(noteItem.getNoteContent());
            tag.setText(noteItem.getTag());
            date.setText(noteItem.getDate());
            //Toast.makeText(this, "Tipo de Nota: " + noteItem.getNoteType(), Toast.LENGTH_SHORT).show();
        }

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteItem.setNoteTitle(title.getText().toString());
                noteItem.setNoteContent(content.getText().toString());
                    goToNoteProperties(noteItem);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Cuando presionamos este botón la nota es guardada, no sin antes
                revisar que todas sus propiedades estén establecidas, de lo contrario
                se hace intent a la vista para editar las propiedades*/
                noteItem.setNoteTitle(title.getText().toString());
                noteItem.setNoteContent(content.getText().toString());
                if (noteBasicPropertiesCheck(noteItem)){
                    //Guardamos la nota y regresamos a la vista principal
                    if(noteItem.getTag()==null){ noteItem.setTag("general"); }
                    if(noteItem.getNoteType()==null){ noteItem.setNoteType("Normal"); }
                    if(noteItem.getColor().equals("")){ noteItem.setColor("#FFF"); }
                    if(noteItem.getDate()==null||noteItem.getDate().equals("")){ noteItem.setDate(obtenerFechaConFormato("yyyy-MM-dd"));};

                    Toast.makeText(NoteEditorActivity.this, "Nota guardada", Toast.LENGTH_SHORT).show();
                    goToMainMenu(noteItem);
                }else{
                    //Hacemos intent a la vista de edición de propiedades
                    Toast.makeText(NoteEditorActivity.this, "No se han definido las propiedades de la nota", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    //método para verificar que se han definido todas las propiedades de la nota antes de guardar
    public boolean noteBasicPropertiesCheck(NoteElement noteItem){
        if(noteItem.getNoteTitle().equals("")) {return false;}
        else return !noteItem.getNoteContent().equals("");
    }

    public void goToNoteProperties(NoteElement noteItem){
        Intent intent = new Intent(this, NoteProperties.class);
        intent.putExtra("NoteItem", noteItem);
        startActivity(intent);
    }

    public void goToMainMenu(NoteElement noteItem){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("NoteItem", noteItem);
        startActivity(intent);
    }

    public static String obtenerFechaConFormato(String formato) {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(formato);
        return sdf.format(date);
    }
}