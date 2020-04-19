package com.pat0007.spendingmanagementsql;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {

    BigDecimal balance;
    Button enterButton, searchButton;
    Context applicationContext;
    DatabaseHelper myDB;
    EditText date, amount, purpose, dateSearchField, typeSearchField;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    TableLayout tableLayout;
    TextView header;

    private View.OnClickListener listen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.enterButton:
                    enter_btn_clicked();
                    break;
                case R.id.searchButton:
                    search();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDB = new DatabaseHelper(this);
        header = findViewById(R.id.header);
        date = findViewById(R.id.date);
        amount = findViewById(R.id.amount);
        purpose = findViewById(R.id.purpose);
        enterButton = findViewById(R.id.enterButton);
        searchButton = findViewById(R.id.searchButton);
        dateSearchField = findViewById(R.id.dateSearchField);
        typeSearchField = findViewById(R.id.typeSearchField);
        tableLayout = findViewById(R.id.transactionsTable);
        applicationContext = this;

        sharedPref = getSharedPreferences(
                "com.pat0007.spendingmanagementsql.PREFERENCE_FILE_KEY", MODE_PRIVATE);

        enterButton.setOnClickListener(listen);
        searchButton.setOnClickListener(listen);

        balance = new BigDecimal(sharedPref.getString("BALANCE", "0"));
        header.setText(sharedPref.getString("HEADER", "Current Balance: $0"));
        populateTable();
    }

    @Override
    protected void onPause() {
        super.onPause();

        sharedPref = getSharedPreferences(
                "com.pat0007.spendingmanagementsql.PREFERENCE_FILE_KEY", MODE_PRIVATE);
        editor = sharedPref.edit();

        editor.putString("HEADER", header.getText().toString());
        String balance = header.getText().toString().substring(18);
        editor.putString("BALANCE", balance);
        editor.apply();
    }

    private void enter_btn_clicked() {
        String dateText = date.getText().toString();
        String purposeText = purpose.getText().toString();
        int packedInt;

        String amountText = amount.getText().toString();
        BigDecimal amount = new BigDecimal(amountText);

        packedInt = amount.scaleByPowerOfTen(2).intValue();

        BigDecimal newBalance = new BigDecimal(amountText);

        balance = balance.add(newBalance);
        header.setText("Current Balance: $" + balance);

        myDB.insertData(dateText, packedInt, purposeText);

        addRow(dateText, amountText, purposeText);
    }

    private void addRow(String date, String amount, String category) {
        LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(tableRowParams);

        TextView dateText = new TextView(this);
        dateText.setText(date);
        TextView amountText = new TextView(this);
        amountText.setText(amount);
        TextView categoryText = new TextView(this);
        categoryText.setText(category);

        TableRow.LayoutParams cellParams = new TableRow.LayoutParams(0,
                TableRow.LayoutParams.MATCH_PARENT);
        cellParams.weight = 3;
        dateText.setLayoutParams(cellParams);
        amountText.setLayoutParams(cellParams);
        categoryText.setLayoutParams(cellParams);

        tableRow.addView(dateText);
        tableRow.addView(amountText);
        tableRow.addView(categoryText);

        tableLayout.addView(tableRow);
    }

    private void populateTable() {
        Cursor result = myDB.getAllData();
        if (result.getCount() == 0) {
            return;
        }
        while (result.moveToNext()) {
            String date = result.getString(1);
            String category = result.getString(3);

            Integer packedInt = result.getInt(2);
            BigDecimal amount = new BigDecimal(packedInt);
            amount = amount.scaleByPowerOfTen(-2);
            String amountText = amount.toString();
            System.out.println(amountText);

            addRow(date, amountText, category);
        }
    }

    private void search() {
        String dateQuery = dateSearchField.getText().toString();
        String typeQuery = typeSearchField.getText().toString();
        Cursor result;

        if (dateQuery != null) {
            if (dateQuery.startsWith("before")) {
                String query = "TRANSACTION_DATE < '" + dateQuery.substring(7) + "'";
                result = myDB.getSelectData(query);
                if (result.getCount() == 0) {
                    System.out.println("No results found!");
                    return;
                }
                while (result.moveToNext()) {
                    System.out.print(result.getString(1));
                    System.out.print(" ");
                    System.out.print(result.getString(2));
                    System.out.print(" ");
                    System.out.println(result.getString(3));
                }
            }
            else if (dateQuery.startsWith("on")) {
                String query = "TRANSACTION_DATE = '" + dateQuery.substring(3) + "'";
                result = myDB.getSelectData(query);
                if (result.getCount() == 0) {
                    System.out.println("This bitch empty");
                    return;
                }
                while (result.moveToNext()) {
                    System.out.print(result.getString(1));
                    System.out.print(" ");
                    System.out.print(result.getString(2));
                    System.out.print(" ");
                    System.out.println(result.getString(3));
                }
            }
            else if (dateQuery.startsWith("after")) {
                String query = "TRANSACTION_DATE > '" + dateQuery.substring(6) + "'";
                result = myDB.getSelectData(query);
                if (result.getCount() == 0) {
                    System.out.println("No results found!");
                    return;
                }
                while (result.moveToNext()) {
                    System.out.print(result.getString(1));
                    System.out.print(" ");
                    System.out.print(result.getString(2));
                    System.out.print(" ");
                    System.out.println(result.getString(3));
                }
            }
            else if (dateQuery.startsWith("between")) {
                String query = "TRANSACTION_DATE BETWEEN '" + dateQuery.substring(8, 18) + "'" +
                        " AND '" + dateQuery.substring(23, 33) + "'";
                result = myDB.getSelectData(query);
                if (result.getCount() == 0) {
                    System.out.println("No results found!");
                    return;
                }
                while (result.moveToNext()) {
                    System.out.print(result.getString(1));
                    System.out.print(" ");
                    System.out.print(result.getString(2));
                    System.out.print(" ");
                    System.out.println(result.getString(3));
                }
            }
            else {
                System.out.println("Error! Please enter your search format correctly.");
            }
        }
        if (typeQuery != null) {
            //do something
            System.out.println(typeQuery);
        }
    }
}