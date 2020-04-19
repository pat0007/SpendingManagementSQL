package com.pat0007.spendingmanagementsql;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
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
    EditText date, amount, purpose, dateSearchField, amountSearchField;
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
        amountSearchField = findViewById(R.id.amountSearchField);
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

            addRow(date, amountText, category);
        }
    }

    private void search() {
        Cursor result;

        if (!TextUtils.isEmpty(amountSearchField.getText()) &&
                !TextUtils.isEmpty(dateSearchField.getText())) {


            String dateSearchQuery = getDateSearchQuery();
            String amountSearchQuery = getAmountSearchQuery();
            String query = dateSearchQuery + amountSearchQuery;

            result = myDB.getSelectData(query);
                if (result.getCount() == 0) {
                    System.out.println("No results found!");
                    return;
                }
                else {
                    printResult(result);
                }
        }
        else if (!TextUtils.isEmpty(dateSearchField.getText()) &&
                TextUtils.isEmpty(amountSearchField.getText())) {

            String dateSearchQuery = getDateSearchQuery();

            result = myDB.getSelectData(dateSearchQuery);
            if (result.getCount() == 0) {
                System.out.println("No results found!");
                return;
            }
            else {
                printResult(result);
            }
        }
        else if (TextUtils.isEmpty(dateSearchField.getText()) &&
                !TextUtils.isEmpty(amountSearchField.getText())) {

            String amountSearchQuery = getAmountSearchQuery();

            result = myDB.getSelectData(amountSearchQuery);
            if (result.getCount() == 0) {
                System.out.println("No results found!");
                return;
            }
            else {
                printResult(result);
            }
        }
        else {
            System.out.println("No search terms added.");
        }
    }

    private String getDateSearchQuery() {
        if (dateSearchField.getText().toString().startsWith("before")) {
            return "TRANSACTION_DATE < '" + dateSearchField.getText().toString().substring(7) + "'";
        }
        else if (dateSearchField.getText().toString().startsWith("on")) {
            return "TRANSACTION_DATE = '" + dateSearchField.getText().toString().substring(3) + "'";
        }
        else if (dateSearchField.getText().toString().startsWith("after")) {
            return "TRANSACTION_DATE > '" + dateSearchField.getText().toString().substring(6) + "'";
        }
        else if (dateSearchField.getText().toString().startsWith("between")) {
            return "TRANSACTION_DATE BETWEEN '" +
                    dateSearchField.getText().toString().substring(8, 18) + "' AND '" +
                    dateSearchField.getText().toString().substring(23,33) + "'";
        }
        else {
            return "Please enter correct date search term.";
        }
    }

    private String getAmountSearchQuery() {
        int packedInt;
        if (amountSearchField.getText().toString().substring(0,2).equals("< ") ||
                amountSearchField.getText().toString().substring(0,2).equals("> ")) {
            packedInt = Integer.parseInt(amountSearchField.getText().toString().substring(2));
            BigDecimal bd = new BigDecimal(packedInt);
            bd = bd.scaleByPowerOfTen(2);
            String amount = bd.toString();
            return " AND AMOUNT " + amountSearchField.getText().toString().substring(0,2) + amount;
        }
        else if (amountSearchField.getText().toString().contains("<=") ||
                amountSearchField.getText().toString().contains(">=")) {
            packedInt = Integer.parseInt(amountSearchField.getText().toString().substring(3));
            BigDecimal bd = new BigDecimal(packedInt);
            bd = bd.scaleByPowerOfTen(2);
            String amount = bd.toString();
            return " AND AMOUNT " + amountSearchField.getText().toString().substring(0,3) + amount;
        }
        else {
            return "\nPlease enter correct amount search term.";
        }
    }

    private void printResult(Cursor toBePrinted) {
        while (toBePrinted.moveToNext()) {
            System.out.print(toBePrinted.getString(1));
            System.out.print(" ");

            Integer packedInt = toBePrinted.getInt(2);
            BigDecimal amount = new BigDecimal(packedInt);
            amount = amount.scaleByPowerOfTen(-2);
            System.out.print("$" + amount);

            System.out.print(" ");
            System.out.println(toBePrinted.getString(3));
        }
    }
}