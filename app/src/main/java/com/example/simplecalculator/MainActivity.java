package com.example.simplecalculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvResult;

    private String currentInput = "0";
    private Double firstOperand = null;
    private String operator = null;
    private boolean isNewOperation = true;
    private String currentExpression = "";

    // Переменная для памяти
    private double memoryValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);

        updateDisplay();

        // Навешиваем обработчики на все кнопки
        int[] btnIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btnAdd, R.id.btnSub, R.id.btnMult, R.id.btnDiv,
                R.id.btnEqual, R.id.btnComma, R.id.btnClear, R.id.btnPercent,
                R.id.btnMC, R.id.btnMPlus, R.id.btnMMinus, R.id.btnMR, R.id.btnBackspace
        };

        for (int id : btnIds) {
            findViewById(id).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        // Цифровые кнопки
        if (id == R.id.btn0) {
            actionNumber("0");
        } else if (id == R.id.btn1) {
            actionNumber("1");
        } else if (id == R.id.btn2) {
            actionNumber("2");
        } else if (id == R.id.btn3) {
            actionNumber("3");
        } else if (id == R.id.btn4) {
            actionNumber("4");
        } else if (id == R.id.btn5) {
            actionNumber("5");
        } else if (id == R.id.btn6) {
            actionNumber("6");
        } else if (id == R.id.btn7) {
            actionNumber("7");
        } else if (id == R.id.btn8) {
            actionNumber("8");
        } else if (id == R.id.btn9) {
            actionNumber("9");
        }
        // Операторы
        else if (id == R.id.btnAdd) {
            actionOperator("+");
        } else if (id == R.id.btnSub) {
            actionOperator("-");
        } else if (id == R.id.btnMult) {
            actionOperator("×");
        } else if (id == R.id.btnDiv) {
            actionOperator("÷");
        }
        // Другие кнопки
        else if (id == R.id.btnEqual) {
            actionEquals();
        } else if (id == R.id.btnComma) {
            actionDot();
        } else if (id == R.id.btnClear) {
            actionClear();
        } else if (id == R.id.btnPercent) {
            actionPercent();
        } else if (id == R.id.btnBackspace) {
            actionBackspace();
        } else if (id == R.id.btnMC) {
            actionMemoryClear();
        } else if (id == R.id.btnMPlus) {
            actionMemoryAdd();
        } else if (id == R.id.btnMMinus) {
            actionMemorySubtract();
        } else if (id == R.id.btnMR) {
            actionMemoryRecall();
        }
    }

    // --- Логика действий ---

    private void actionNumber(String number) {
        if (isNewOperation) {
            currentInput = "0";
            isNewOperation = false;
            if (operator == null) {
                currentExpression = "";
            }
        }

        if (currentInput.equals("0")) {
            currentInput = number;
        } else {
            currentInput += number;
        }
        updateDisplay();
    }

    private void actionDot() {
        if (isNewOperation) {
            currentInput = "0";
            isNewOperation = false;
            if (operator == null) currentExpression = "";
        }

        if (!currentInput.contains(",")) {
            currentInput += ",";
            updateDisplay();
        }
    }

    private void actionClear() {
        currentInput = "0";
        firstOperand = null;
        operator = null;
        currentExpression = "";
        isNewOperation = true;
        updateDisplay();
    }

    private void actionPercent() {
        try {
            double value = parseDouble(currentInput);
            value = value / 100.0;
            currentInput = formatDouble(value);
            updateDisplay();
        } catch (NumberFormatException e) {
            // Игнорируем
        }
    }

    private void actionOperator(String op) {
        if (firstOperand != null && !isNewOperation) {
            double secondOperand = parseDouble(currentInput);
            double result = calculate(firstOperand, secondOperand, operator);

            firstOperand = result;
            currentInput = formatDouble(result);
            currentExpression = formatDouble(result) + " " + op + " ";
        } else {
            firstOperand = parseDouble(currentInput);
            currentExpression = currentInput + " " + op + " ";
        }

        operator = op;
        isNewOperation = true;
        updateDisplay();
    }

    private void actionEquals() {
        if (firstOperand == null || operator == null) {
            return;
        }

        double secondOperand = parseDouble(currentInput);
        double result = calculate(firstOperand, secondOperand, operator);

        String fullExpression = currentExpression + currentInput + " = " + formatDouble(result);

        tvResult.setText(fullExpression);

        currentInput = formatDouble(result);
        firstOperand = null;
        operator = null;
        currentExpression = "";
        isNewOperation = true;
    }

    // Метод для backspace (удаление последнего символа)
    private void actionBackspace() {
        if (!currentInput.equals("0")) {
            if (currentInput.length() == 1 ||
                    (currentInput.length() == 2 && currentInput.startsWith("-"))) {
                currentInput = "0";
            } else {
                currentInput = currentInput.substring(0, currentInput.length() - 1);
            }
            updateDisplay();
        }
    }

    // Кнопки памяти
    private void actionMemoryClear() {
        memoryValue = 0;
        Toast.makeText(this, "Память очищена", Toast.LENGTH_SHORT).show();
    }

    private void actionMemoryAdd() {
        try {
            double value = parseDouble(currentInput);
            memoryValue += value;
            Toast.makeText(this, "M+ = " + formatDouble(memoryValue), Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            // Игнорируем
        }
    }

    private void actionMemorySubtract() {
        try {
            double value = parseDouble(currentInput);
            memoryValue -= value;
            Toast.makeText(this, "M- = " + formatDouble(memoryValue), Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            // Игнорируем
        }
    }

    private void actionMemoryRecall() {
        currentInput = formatDouble(memoryValue);
        isNewOperation = true;
        updateDisplay();
    }

    // Вспомогательный метод для вычислений
    private double calculate(double op1, double op2, String op) {
        switch (op) {
            case "+": return op1 + op2;
            case "-": return op1 - op2;
            case "×": return op1 * op2;
            case "÷":
                if (op2 == 0) {
                    Toast.makeText(this, "На ноль делить нельзя!", Toast.LENGTH_SHORT).show();
                    actionClear();
                    return 0;
                }
                return op1 / op2;
            default: return 0;
        }
    }

    // Метод обновления экрана
    @SuppressLint("SetTextI18n")
    private void updateDisplay() {
        if (operator != null && !isNewOperation) {
            tvResult.setText(currentExpression + currentInput);
        } else if (operator != null) {
            tvResult.setText(currentExpression);
        } else {
            tvResult.setText(currentInput);
        }
    }

    private double parseDouble(String str) {
        return Double.parseDouble(str.replace(",", "."));
    }

    @SuppressLint("DefaultLocale")
    private String formatDouble(double value) {
        if (value == (long) value) {
            return String.format("%d", (long) value);
        } else {
            return String.valueOf(value).replace(".", ",");
        }
    }
}