package com.sen.calculator;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editTextNumber1;
    private EditText editTextNumber2;
    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind views
        editTextNumber1 = findViewById(R.id.editTextNumber1);
        editTextNumber2 = findViewById(R.id.editTextNumber2);
        textViewResult  = findViewById(R.id.textViewResult);

        Button btnAdd      = findViewById(R.id.btnAdd);
        Button btnSubtract = findViewById(R.id.btnSubtract);
        Button btnMultiply = findViewById(R.id.btnMultiply);
        Button btnDivide   = findViewById(R.id.btnDivide);

        // Button listeners
        btnAdd.setOnClickListener(v -> performOperation(Operation.ADD));
        btnSubtract.setOnClickListener(v -> performOperation(Operation.SUBTRACT));
        btnMultiply.setOnClickListener(v -> performOperation(Operation.MULTIPLY));
        btnDivide.setOnClickListener(v -> performOperation(Operation.DIVIDE));
    }

    // Enum for the four operations
    private enum Operation {
        ADD, SUBTRACT, MULTIPLY, DIVIDE
    }

    /**
     * Validates inputs, then executes the requested arithmetic operation
     * and displays the result (or an error message) in textViewResult.
     */
    private void performOperation(Operation operation) {
        String raw1 = editTextNumber1.getText().toString().trim();
        String raw2 = editTextNumber2.getText().toString().trim();

        // --- Input validation ---
        if (TextUtils.isEmpty(raw1) && TextUtils.isEmpty(raw2)) {
            textViewResult.setText(getString(R.string.error_both_empty));
            return;
        }
        if (TextUtils.isEmpty(raw1)) {
            textViewResult.setText(getString(R.string.error_first_empty));
            return;
        }
        if (TextUtils.isEmpty(raw2)) {
            textViewResult.setText(getString(R.string.error_second_empty));
            return;
        }

        double num1, num2;
        try {
            num1 = Double.parseDouble(raw1);
            num2 = Double.parseDouble(raw2);
        } catch (NumberFormatException e) {
            textViewResult.setText(getString(R.string.error_invalid_number));
            return;
        }

        // --- Division-by-zero guard ---
        if (operation == Operation.DIVIDE && num2 == 0) {
            textViewResult.setText(getString(R.string.error_divide_by_zero));
            return;
        }

        // --- Perform calculation ---
        double result;
        String operatorSymbol;

        switch (operation) {
            case ADD:
                result = num1 + num2;
                operatorSymbol = "+";
                break;
            case SUBTRACT:
                result = num1 - num2;
                operatorSymbol = "−";
                break;
            case MULTIPLY:
                result = num1 * num2;
                operatorSymbol = "×";
                break;
            case DIVIDE:
            default:
                result = num1 / num2;
                operatorSymbol = "÷";
                break;
        }

        // --- Format and display result ---
        // Show as integer when the result has no fractional part
        String formattedResult = (result == Math.floor(result) && !Double.isInfinite(result))
                ? String.valueOf((long) result)
                : String.valueOf(result);

        String output = formatNumber(num1) + " " + operatorSymbol + " "
                + formatNumber(num2) + " = " + formattedResult;

        textViewResult.setText(output);
    }

    /** Returns an integer string when possible, otherwise the decimal string. */
    private String formatNumber(double value) {
        return (value == Math.floor(value) && !Double.isInfinite(value))
                ? String.valueOf((long) value)
                : String.valueOf(value);
    }
}