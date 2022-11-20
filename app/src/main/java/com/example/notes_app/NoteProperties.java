package com.example.notes_app;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class NoteProperties extends AppCompatActivity {


    EditText tag;
    Button saveBtn;
    EditText setAlarmDate, setAlarmTime;
    Spinner spinnerTipoNota;
    ArrayAdapter<CharSequence> adapter;
    NoteElement noteItem;
    RadioButton isHiddenRBtn;
    SwitchCompat dateSwitch;
    DatePickerDialog.OnDateSetListener setDateListener;
    TimePickerDialog.OnTimeSetListener setTimeListener;
    Calendar calendar = Calendar.getInstance();

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Calendar utilCalendar = Calendar.getInstance();
    private String channelID = "idCanalNotificaciones";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_properties);

        //utilCalendar nos sirve para setear la alarma

        utilCalendar.set(Calendar.SECOND, 0);
        utilCalendar.set(Calendar.MILLISECOND, 0);

        noteItem = (NoteElement) getIntent().getSerializableExtra("NoteItem");

        //Le pasamos valores predefinidos al spinner de tipo de nota
        adapter = ArrayAdapter.createFromResource(this, R.array.note_types_array, android.R.layout.simple_spinner_item);
        spinnerTipoNota = findViewById(R.id.note_type_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoNota.setAdapter(adapter);


        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);

        tag = findViewById(R.id.note_tag);
        saveBtn = findViewById(R.id.save_properties_buton);
        isHiddenRBtn = findViewById(R.id.hiddenRbtn);
        setAlarmDate = findViewById(R.id.date_picker_actions);
        dateSwitch = findViewById(R.id.date_switch);
        setAlarmTime = findViewById(R.id.time_picker_actions);
        //En este apartado inicializaremos los View Elements del Activity

        isHiddenRBtn.setChecked(noteItem.isHidden());
        tag.setText(noteItem.getTag());

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteItem.setTag(tag.getText().toString());
                noteItem.setNoteType(spinnerTipoNota.getSelectedItem().toString());
                noteItem.setHidden(isHiddenRBtn.isChecked());
                setAlarm();
                goToNoteEditorActivity(noteItem);
            }
        });

        setAlarmDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(NoteProperties.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        String date = day + "/" + month + "/" + year;
                        noteItem.setAlarm(date + " - " + setAlarmTime.getText());
                        setAlarmDate.setText(date);

                    }
                }, year, month, day);
                datePickerDialog.show();

            }
        });

        setAlarmTime.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (dateSwitch.isChecked()) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(NoteProperties.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String time = hourOfDay + ":" + minute;
                            Toast.makeText(NoteProperties.this, time, Toast.LENGTH_SHORT).show();
                            noteItem.setAlarm(noteItem.getDate() + " - " + time);
                            setAlarmTime.setText(time);
                            utilCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            utilCalendar.set(Calendar.MINUTE, minute);
                        }
                    }, hour, minute, true);
                    timePickerDialog.show();
                } else {
                    Toast.makeText(NoteProperties.this, "Antes debes activar \"Crear Recordatorio\" para establecer una hora", Toast.LENGTH_SHORT).show();
                }
            }

        });

        setDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = day + "/" + month + "/" + year;
            }
        };

        setTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = hourOfDay + ":" + minute;
            }
        };

        dateSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dateSwitch.isChecked()){
                    setAlarmDate.setEnabled(true);
                    setAlarmTime.setEnabled(true);
                }else {
                    setAlarmDate.setEnabled(false);
                    setAlarmTime.setEnabled(false);
                }
            }
        });
    }

    public void goToNoteEditorActivity(NoteElement noteItem) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "idCanalNotificaciones")
                    .setSmallIcon(R.drawable.ic_all_notes)
                    .setContentTitle("Recordatorio de Nota")
                    .setContentText("Esta es una prueba")
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(1, builder.build());
            Toast.makeText(this, "alarma!", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(this, NoteEditorActivity.class);
        intent.putExtra("NoteItem", noteItem);
        startActivity(intent);
    }



    public void setAlarm(){
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReciver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, utilCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(this, "La alarma fué creada exitosamente", Toast.LENGTH_SHORT).show();

    }
}