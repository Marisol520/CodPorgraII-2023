package com.example.calcolatrice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText editTextNumber1, editTextNumber2;
    TextView textViewResult;
    Button btnSum, btnSubtract, btnMultiply, btnDivide, btnFactorial, btnExponential, btnPercentage, btnSqrt, btnCbrt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextNumber1 = findViewById(R.id.editTextNumber1);
        editTextNumber2 = findViewById(R.id.editTextNumber2);
        textViewResult = findViewById(R.id.textViewResult);

        btnSum = findViewById(R.id.btnSum);
        btnSubtract = findViewById(R.id.btnSubtract);
        btnMultiply = findViewById(R.id.btnMultiply);
        btnDivide = findViewById(R.id.btnDivide);
        btnFactorial = findViewById(R.id.btnFactorial);
        btnExponential = findViewById(R.id.btnExponential);
        btnPercentage = findViewById(R.id.btnPercentage);
        btnSqrt = findViewById(R.id.btnSqrt);
        btnCbrt = findViewById(R.id.btnCbrt);

        btnSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateResult("+");
            }
        });

        btnSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateResult("-");
            }
        });

        btnMultiply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateResult("*");
            }
        });

        btnDivide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateResult("/");
            }
        });

        btnFactorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double num = Double.parseDouble(editTextNumber1.getText().toString());
                double result = factorial(num);
                textViewResult.setText(String.valueOf(result));
            }
        });

        btnExponential.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double num1 = Double.parseDouble(editTextNumber1.getText().toString());
                double num2 = Double.parseDouble(editTextNumber2.getText().toString());
                double result = Math.pow(num1, num2);
                textViewResult.setText(String.valueOf(result));
            }
        });

        btnPercentage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double num1 = Double.parseDouble(editTextNumber1.getText().toString());
                double num2 = Double.parseDouble(editTextNumber2.getText().toString());
                double result = (num1 * num2) / 100;
                textViewResult.setText(String.valueOf(result));
            }
        });

        btnSqrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double num = Double.parseDouble(editTextNumber1.getText().toString());
                double result = Math.sqrt(num);
                textViewResult.setText(String.valueOf(result));
            }
        });

        btnCbrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double num = Double.parseDouble(editTextNumber1.getText().toString());
                double result = Math.cbrt(num);
                textViewResult.setText(String.valueOf(result));
            }
        });
    }

    private void calculateResult(String operator) {
        double num1 = Double.parseDouble(editTextNumber1.getText().toString());
        double num2 = Double.parseDouble(editTextNumber2.getText().toString());
        double result = 0;

        switch (operator) {
            case "+":
                result = num1 + num2;
                break;
            case "-":
                result = num1 - num2;
                break;
            case "*":
                result = num1 * num2;
                break;
            case "/":
                if (num2 != 0) {
                    result = num1 / num2;
                } else {
                    textViewResult.setText("Cannot divide by zero");
                }
                break;
        }

        textViewResult.setText(String.valueOf(result));
    }

    private double factorial(double num) {
        if (num == 0)
            return 1;
        else
            return num * factorial(num - 1);
    }
}