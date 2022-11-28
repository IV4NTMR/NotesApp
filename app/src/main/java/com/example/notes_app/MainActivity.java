package com.example.notes_app;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements PopupMenu.OnMenuItemClickListener{


    //Lista en la cual se almacenan las notas.
    List<NoteElement> notesStorage = new ArrayList<>();
    NoteElement blankNoteTemplate;
    NotesListAdapter listAdapter;
    RecyclerView recyclerView;
    RelativeLayout relativeLayout;
    ImageButton visibilityMenu;
    TextView feedTitle;


    //Valores estáticos de alarma y notifiaciones
    private  String channelID = "idCanalNotificaciones";
    private  Notification.Builder builder;
    private  int notificationId = 1;

    //Variable que nos sirve para identificar cual de las opciones de visibilidad está seleccionada
    int visivilityChecker = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();


        //Recibimos una nota si es que existe una para ser recibida
        NoteElement newNote = (NoteElement) getIntent().getSerializableExtra("NoteItem");
        if (newNote != null){
            //En esta parte verificamos si la nota que estamos recibiendo ya existía en la DB, en cuyo caso solo se sobreescribe.
            NoteDBHelper noteDBHelper = new NoteDBHelper(MainActivity.this);
            if(newNote.getId()==-1){
                boolean success = noteDBHelper.addNote(newNote);
            }else{
                //Si encontramos que la nota ya existía entonces modificamos la nota
                boolean success = noteDBHelper.updateNote(newNote);
                Toast.makeText(this, "Nota actualizada exitosamente: " + success, Toast.LENGTH_SHORT).show();
            }
        }

        init();

        visibilityMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, findViewById(R.id.note_visivility));
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.pop_up_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(MainActivity.this);
                popup.show();
            }
        });


    }



    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.showHidden:
                visivilityChecker = 1;
                feedTitle.setText("Notas Ocultas");
                refresh();
                return true;
            case R.id.showVisible:
                visivilityChecker = 2;
                feedTitle.setText("Notas");
                refresh();
                return true;
            case R.id.showAll:
                visivilityChecker = 3;
                feedTitle.setText("Todas las Notas");
                refresh();
                return true;
            default:
                return false;
        }
    }




    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch(item.getItemId()){
            case R.id.create_note:
                //Aquí va el código para cambiar de vista por medio del menu
                goToNoteEditor(blankNoteTemplate);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem   item) {

        NoteElement noteItem;
        NoteDBHelper noteDBHelper;
        int position;
        try {
            position = listAdapter.getPosition();
            noteItem = listAdapter.getNoteElement(position);
        } catch (Exception e) {return super.onContextItemSelected(item);}

        switch (item.getItemId()) {
            case 1:
                goToNoteEditor(noteItem);
                return true;
            case 2:
                getNoteProperties(noteItem);
                return true;
            case 3:
                noteItem.setHidden(true);
                noteDBHelper = new NoteDBHelper(MainActivity.this);
                noteDBHelper.updateNote(noteItem);
                noteDBHelper.close();
                refresh();
                listAdapter.notifyDataSetChanged();
                return true;
            case 4:
                noteDBHelper = new NoteDBHelper(MainActivity.this);
                new MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.alert_title)
                        .setMessage(R.string.alert_message)
                        .setPositiveButton(R.string.alert_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(MainActivity.this, "Nota Eliminada: " +
                                        noteDBHelper.deleteNote(noteItem), Toast.LENGTH_SHORT).show();
                                noteDBHelper.close();
                                refresh();
                                listAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(R.string.alert_cancel, null).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void init(){
        //Añadimos un template para una nota en blanco
        blankNoteTemplate = new NoteElement("000", "", "Título", obtenerFechaConFormato("yyyy-MM-dd"),"general", "");
        //Agregamos dos notas por default para ejemplificar
        //Método con el cual obtengo las notas de la BD y los almaceno en una lista para mostrarlos en el recyclerview
        visibilityMenu = findViewById(R.id.note_visivility);
        feedTitle = findViewById(R.id.recycler_title);
        refresh();
        registerForContextMenu(recyclerView);
    }

    public void goToNoteEditor(NoteElement item){
        Intent intent = new Intent(this, NoteEditorActivity.class);
        intent.putExtra("NoteItem", item);
        startActivity(intent);
    }

    public void getNoteProperties(NoteElement item){
        Toast.makeText(this,
                "ID:" + item.getId() +
                        "\nTipo de Nota:" + item.getNoteType() +
                        "\nTítulo:" + item.getNoteTitle() +
                        "\nEtiqueta:" + item.getTag() +
                        "\nFecha:" + item.getDate() +
                        "\nOculto:" + item.isHidden() +
                        "\nAlarma:" + item.getAlarm(),
                Toast.LENGTH_SHORT).show();
    }

    public String obtenerFechaConFormato(String formato) {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(formato);
        return sdf.format(date);
    }

    public void reloadMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void refresh(){
        NoteDBHelper dataBaseHelper = new NoteDBHelper(MainActivity.this);

        if(visivilityChecker == 1){notesStorage = dataBaseHelper.getHiddenNotes();}
        else if (visivilityChecker == 2){notesStorage = dataBaseHelper.getVisibleNotes();}
        else if (visivilityChecker == 3){notesStorage = dataBaseHelper.getAllNotes();}
        listAdapter = new NotesListAdapter(notesStorage, this, new NotesListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NoteElement item) {
                goToNoteEditor(item);
            }
            @Override
            public void onLongItemClick(NoteElement item){
            }
        });
        recyclerView = findViewById(R.id.notes_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "notesNotifChannel";
            String description = "Canal de notificaiones para notas";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private static void setAlarm(int i, Long timestamp, Context ctx){
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(ctx, AlarmReciver.class);
        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getBroadcast(ctx, i, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        alarmIntent.setData((Uri.parse("custom://" + System.currentTimeMillis())));
        alarmManager.set(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent);
    }
}