package com.example.notes_app;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_properties);

        noteItem = (NoteElement) getIntent().getSerializableExtra("NoteItem");

        //Le pasamos valores predefinidos al spinner de tipo de nota
        adapter = ArrayAdapter.createFromResource(this, R.array.note_types_array, android.R.layout.simple_spinner_item);
        spinnerTipoNota = findViewById(R.id.note_type_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoNota.setAdapter(adapter);

        Calendar calendar = Calendar.getInstance();
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
        Intent intent = new Intent(this, NoteEditorActivity.class);
        intent.putExtra("NoteItem", noteItem);
        startActivity(intent);
    }
}