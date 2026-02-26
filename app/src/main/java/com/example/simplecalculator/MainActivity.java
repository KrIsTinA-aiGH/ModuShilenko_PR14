package com.example.simplecalculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvResult;

    // Переменные состояния
    private String currentInput = "0";       // Текущее вводимое число (справа)
    private Double firstOperand = null;      // Первое число
    private String operator = null;          // Знак операции
    private boolean isNewOperation = true;   // Флаг начала новой операции

    // Новая переменная для хранения полного выражения (например: "5 + ")
    private String currentExpression = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);
        updateDisplay(); // Инициализируем экран

        // Навешиваем обработчики на все кнопки
        int[] btnIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btnAdd, R.id.btnSub, R.id.btnMult, R.id.btnDiv,
                R.id.btnEqual, R.id.btnDot, R.id.btnClear, R.id.btnSign, R.id.btnPercent
        };

        for (int id : btnIds) {
            findViewById(id).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        MaterialButton button = (MaterialButton) v;
        String text = button.getText().toString();

        switch (text) {
            case "AC":
                actionClear();
                break;
            case "+/-":
                actionSignChange();
                break;
            case "%":
                actionPercent();
                break;
            case "÷":
            case "×":
            case "-":
            case "+":
                actionOperator(text);
                break;
            case "=":
                actionEquals();
                break;
            case ",":
                actionDot();
                break;
            default:
                actionNumber(text);
                break;
        }
    }

    // --- Логика действий ---

    private void actionNumber(String number) {
        if (isNewOperation) {
            currentInput = "0";
            isNewOperation = false;
            // Если мы начинаем ввод нового числа после результата, сбрасываем выражение
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

    private void actionSignChange() {
        if (!currentInput.equals("0")) {
            if (currentInput.startsWith("-")) {
                currentInput = currentInput.substring(1);
            } else {
                currentInput = "-" + currentInput;
            }
            updateDisplay();
        }
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
        // Если уже была операция и пользователь ввел второе число -> считаем промежуточный итог
        if (firstOperand != null && !isNewOperation) {
            double secondOperand = parseDouble(currentInput);
            double result = calculate(firstOperand, secondOperand, operator);

            // Обновляем первое число результатом
            firstOperand = result;
            currentInput = formatDouble(result);

            // Формируем новое выражение: "Результат + НовыйОператор"
            currentExpression = formatDouble(result) + " " + op + " ";
        } else {
            // Первый раз нажали оператор
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

        // Формируем полную строку: "Число1 Оператор Число2 = Результат"
        String fullExpression = currentExpression + currentInput + " = " + formatDouble(result);

        tvResult.setText(fullExpression);

        // Подготовка к следующему действию
        currentInput = formatDouble(result);
        firstOperand = null;
        operator = null;
        currentExpression = ""; // Сбрасываем выражение, так как расчет завершен
        isNewOperation = true;
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
            // Если операция выбрана и мы вводим второе число, показываем: "Выражение + ТекущееЧисло"
            // Пример: "5 + 2"
            tvResult.setText(currentExpression + currentInput);
        } else if (operator != null) {
            // Если операция выбрана, но ввод еще не начался (сразу после нажатия знака)
            // Показываем только выражение: "5 + "
            tvResult.setText(currentExpression);
        } else {
            // Просто вводим число
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