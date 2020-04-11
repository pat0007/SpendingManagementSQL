package com.pat0007.spendingmanagementsql;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {

    BigDecimal balance;
    Button add_btn, minus_btn;
    Context applicationContext;
    DatabaseHelper myDB;
    EditText date, amount, purpose;
    String history;
    TextView header;

    private View.OnClickListener listen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            add_btn_clicked();
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
        add_btn = findViewById(R.id.add_btn);
        applicationContext = this;

        add_btn.setOnClickListener(listen);

        balance = BigDecimal.ZERO;
    }

    private void add_btn_clicked() {
        String dateText = date.getText().toString();
        String amountText = amount.getText().toString();
        String purposeText = purpose.getText().toString();

        BigDecimal newBalance = new BigDecimal(amountText);

        balance = balance.add(newBalance);
        header.setText("Current Balance: $" + balance);

        myDB.insertData(dateText, amountText, purposeText);
    }
}