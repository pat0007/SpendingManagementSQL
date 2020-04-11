package com.pat0007.spendingmanagementsql;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
    Button enter_btn;
    Context applicationContext;
    DatabaseHelper myDB;
    EditText date, amount, purpose;
    TableLayout tableLayout;
    TextView header;

    private View.OnClickListener listen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            enter_btn_clicked();
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
        enter_btn = findViewById(R.id.enter_btn);
        tableLayout = findViewById(R.id.transactionsTable);
        applicationContext = this;

        enter_btn.setOnClickListener(listen);
        balance = BigDecimal.ZERO;
    }

    private void enter_btn_clicked() {
        String dateText = date.getText().toString();
        String amountText = amount.getText().toString();
        String purposeText = purpose.getText().toString();

        BigDecimal newBalance = new BigDecimal(amountText);

        balance = balance.add(newBalance);
        header.setText("Current Balance: $" + balance);

        myDB.insertData(dateText, amountText, purposeText);

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
}