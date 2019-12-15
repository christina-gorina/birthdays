package com.example.birthdays;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.birthdays.EventsContract.ImportantDates;

public class AddImportantDatePage extends AppCompatActivity {
    private EditText nameEditText;
    private EditText dateEditText;
    private EditText dateHidden;
    private EditText dateNoYear;
    private CheckBox noyearCheckBox;
    private CheckBox notificationCheckBox;
    EventsDbHelper eventsDbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_important_date_page);

        nameEditText = (EditText) findViewById(R.id.nameOfImportantDate);
        dateEditText = (EditText) findViewById(R.id.dateOfImportantDate);
        dateHidden = (EditText) findViewById(R.id.date);
        dateNoYear = (EditText) findViewById(R.id.dateNoYear);
        noyearCheckBox = (CheckBox) findViewById(R.id.noYear);
        notificationCheckBox = (CheckBox) findViewById(R.id.notification);

        eventsDbHelper = new EventsDbHelper(this);
        db = eventsDbHelper.getWritableDatabase();
    }

    public void addDateOfImportantDate(View view) {
        System.out.println("addDateOfImportantDate");
        DialogFragment dialog = new AddImportantDateDialog();
        dialog.show(getSupportFragmentManager(), "DIALOG_LIST");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainactivity_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.addImportantDate:
                if(!(nameEditText.getText().toString().trim().equals("")) && !(dateEditText.getText().toString().trim().equals(""))){
                    insertImportantDate();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(this, "Введите название и дату", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        db.close();
    }

    private void insertImportantDate() {
        // Считываем данные из текстовых полей
        String date;
        String name = nameEditText.getText().toString().trim();

        boolean noyear = ((CheckBox) noyearCheckBox).isChecked();
        if(!noyear){
            date = dateHidden.getText().toString().trim();
        }else{
            date = dateNoYear.getText().toString().trim();
        }

        boolean notification = ((CheckBox) notificationCheckBox).isChecked();

        ContentValues values = new ContentValues();

        values.put(ImportantDates.COLUMN_NAME, name);
        values.put(ImportantDates.COLUMN_DATE, date);
        values.put(ImportantDates.COLUMN_NOYEAR, noyear);
        values.put(ImportantDates.COLUMN_NOTIFICATION, notification);

        // Вставляем новый ряд в базу данных и запоминаем его идентификатор
        long newRowId = db.insert(ImportantDates.TABLE_NAME, null, values);

        // Выводим сообщение в успешном случае или при ошибке
        if (newRowId == -1) {
            // Если ID  -1, значит произошла ошибка
            Toast.makeText(this, "Ошибка при занесении даты", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Дата успешно занесена" , Toast.LENGTH_SHORT).show();
        }
    }
}
