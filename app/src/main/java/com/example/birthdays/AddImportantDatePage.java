package com.example.birthdays;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class AddImportantDatePage extends AppCompatActivity {
    private EditText nameEditText;
    private EditText dateEditText;
    private EditText dateHidden;
    private EditText dateNoYear;
    private CheckBox noyearCheckBox;
    private CheckBox notificationCheckBox;
    private DB db;
    static final String TAG = "AddImportantDatePage";
    private long idEventInfo;
    private static final String IMPORTANT_DATE = EventItems.IMPORTANT_DATE_TYPE;
    private static final int TIME_OF_NOTIFICATION = 8; // В 24 формате
    //private static final int TIME_OF_NOTIFICATION = 1; // В 24 формате
    private static final int COUNT_OF_DAYS_BEFORE_NOTIFICATION = 3; // За сколько дней напомнить о дате

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

        db = new DB(this);
        db.open();
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

    private void insertImportantDate() {
        // Считываем данные из текстовых полей
        String date;
        String name = nameEditText.getText().toString().trim();
        String type = IMPORTANT_DATE;
        long newImportantDateId = 0;
        int notification = 0;

        boolean noyear = ((CheckBox) noyearCheckBox).isChecked();
        if(!noyear){
            date = dateHidden.getText().toString().trim();
        }else{
            date = dateNoYear.getText().toString().trim();
        }

        Log.d(TAG, "date = " + date);

        int noyearInt = noyear ? 1 : 0;
        notification = ((CheckBox) notificationCheckBox).isChecked() ? 1 : 0; // конвертируем boolean  в  int

        db.beginTransaction();
        try {
            newImportantDateId = db.addImportantDate(name, date); //newImportantDateId возвращает -1 если операция не удалась
            Log.d(TAG, "newImportantDateId = " + newImportantDateId);
            db.setTransactionSuccessful();
            idEventInfo = db.addEventInfo(newImportantDateId, type, noyearInt, notification);
            Log.d(TAG, "idEventInfo = " + idEventInfo);
        }
        catch (SQLException e){
            e.printStackTrace();
            Toast.makeText(this, "Ошибка при занесении даты", Toast.LENGTH_SHORT).show();
        }finally {
            db.endTransaction();
            db.close();
            if(notification == 0){
                NotificationCreator nc = new NotificationCreator(newImportantDateId, idEventInfo, type, name, date, TIME_OF_NOTIFICATION, getApplicationContext());
                nc.createNotificationInDay();
                nc.createNotificationEarly(COUNT_OF_DAYS_BEFORE_NOTIFICATION);
            }

            Toast.makeText(this, "Дата успешно занесена" , Toast.LENGTH_SHORT).show();
        }
    }
}
