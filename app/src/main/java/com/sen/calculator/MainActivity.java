package com.sen.calculator;

import android.os.Bundle;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvExpression, tvResult;
    private StringBuilder expression = new StringBuilder();
    private double memory = 0.0;
    private boolean resultShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvExpression = findViewById(R.id.tvExpression);
        tvResult = findViewById(R.id.tvResult);
        setupButtons();
    }

    private void setupButtons() {
        int[] numIds = {R.id.btn0,R.id.btn1,R.id.btn2,R.id.btn3,R.id.btn4,
                        R.id.btn5,R.id.btn6,R.id.btn7,R.id.btn8,R.id.btn9};
        String[] numVals = {"0","1","2","3","4","5","6","7","8","9"};
        for (int i = 0; i < numIds.length; i++) {
            final String v = numVals[i];
            findViewById(numIds[i]).setOnClickListener(view -> { animateButton(view); appendToExpression(v); });
        }
        findViewById(R.id.btnAdd).setOnClickListener(v      -> { animateButton(v); appendOperator("+"); });
        findViewById(R.id.btnSub).setOnClickListener(v      -> { animateButton(v); appendOperator("-"); });
        findViewById(R.id.btnMul).setOnClickListener(v      -> { animateButton(v); appendOperator("*"); });
        findViewById(R.id.btnDiv).setOnClickListener(v      -> { animateButton(v); appendOperator("/"); });
        findViewById(R.id.btnDot).setOnClickListener(v      -> { animateButton(v); appendDot(); });
        findViewById(R.id.btnEquals).setOnClickListener(v   -> { animateButton(v); calculate(); });
        findViewById(R.id.btnClear).setOnClickListener(v    -> { animateButton(v); clearAll(); });
        findViewById(R.id.btnDel).setOnClickListener(v      -> { animateButton(v); deleteLast(); });
        findViewById(R.id.btnPlusMinus).setOnClickListener(v-> { animateButton(v); toggleSign(); });
        findViewById(R.id.btnPercent).setOnClickListener(v  -> { animateButton(v); appendToExpression("%"); });
        findViewById(R.id.btnOpenBr).setOnClickListener(v   -> { animateButton(v); appendToExpression("("); });
        findViewById(R.id.btnCloseBr).setOnClickListener(v  -> { animateButton(v); appendToExpression(")"); });
        findViewById(R.id.btnSin).setOnClickListener(v      -> { animateButton(v); appendFunction("sin("); });
        findViewById(R.id.btnCos).setOnClickListener(v      -> { animateButton(v); appendFunction("cos("); });
        findViewById(R.id.btnTan).setOnClickListener(v      -> { animateButton(v); appendFunction("tan("); });
        findViewById(R.id.btnLog).setOnClickListener(v      -> { animateButton(v); appendFunction("log("); });
        findViewById(R.id.btnLn).setOnClickListener(v       -> { animateButton(v); appendFunction("ln("); });
        findViewById(R.id.btnSqrt).setOnClickListener(v     -> { animateButton(v); appendFunction("sqrt("); });
        findViewById(R.id.btnPow2).setOnClickListener(v     -> { animateButton(v); appendToExpression("^2"); });
        findViewById(R.id.btnPow3).setOnClickListener(v     -> { animateButton(v); appendToExpression("^3"); });
        findViewById(R.id.btnPowN).setOnClickListener(v     -> { animateButton(v); appendToExpression("^"); });
        findViewById(R.id.btnPi).setOnClickListener(v       -> { animateButton(v); appendToExpression("3.14159265358979"); });
        findViewById(R.id.btnE).setOnClickListener(v        -> { animateButton(v); appendToExpression("2.71828182845905"); });
        findViewById(R.id.btnFact).setOnClickListener(v     -> { animateButton(v); appendToExpression("!"); });
        findViewById(R.id.btnAbs).setOnClickListener(v      -> { animateButton(v); appendFunction("abs("); });
        findViewById(R.id.btnInv).setOnClickListener(v      -> { animateButton(v); appendFunction("inv("); });
        findViewById(R.id.btnMC).setOnClickListener(v       -> { animateButton(v); memory = 0; showToast("Memory Cleared"); });
        findViewById(R.id.btnMR).setOnClickListener(v       -> { animateButton(v); appendToExpression(formatNumber(memory)); });
        findViewById(R.id.btnMPlus).setOnClickListener(v    -> { animateButton(v); memoryOp(true); });
        findViewById(R.id.btnMMinus).setOnClickListener(v   -> { animateButton(v); memoryOp(false); });
    }

    private void appendToExpression(String val) {
        if (resultShown) {
            if (val.matches("[+\\-*/%^]")) { resultShown = false; }
            else { expression.setLength(0); resultShown = false; }
        }
        expression.append(val);
        tvExpression.setText(expression.toString());
        tvResult.setText("");
    }

    private void appendOperator(String op) {
        resultShown = false;
        String expr = expression.toString();
        if (expr.length() > 0) {
            char last = expr.charAt(expr.length() - 1);
            if ("+-*/".indexOf(last) >= 0) expression.setLength(expression.length() - 1);
        }
        expression.append(op);
        tvExpression.setText(expression.toString());
        tvResult.setText("");
    }

    private void appendFunction(String fn) {
        if (resultShown) { expression.setLength(0); resultShown = false; }
        expression.append(fn);
        tvExpression.setText(expression.toString());
        tvResult.setText("");
    }

    private void appendDot() {
        String expr = expression.toString();
        int lastOp = Math.max(Math.max(expr.lastIndexOf('+'), expr.lastIndexOf('-')),
                     Math.max(expr.lastIndexOf('*'), expr.lastIndexOf('/')));
        String current = expr.substring(lastOp + 1);
        if (!current.contains(".")) appendToExpression(".");
    }

    private void deleteLast() {
        if (resultShown) { clearAll(); return; }
        if (expression.length() > 0) {
            expression.deleteCharAt(expression.length() - 1);
            tvExpression.setText(expression.toString());
        }
    }

    private void clearAll() {
        expression.setLength(0);
        tvExpression.setText("0");
        tvResult.setText("");
        resultShown = false;
    }

    private void toggleSign() {
        String expr = expression.toString();
        if (expr.startsWith("-")) expression.deleteCharAt(0);
        else expression.insert(0, "-");
        tvExpression.setText(expression.toString());
    }

    private void memoryOp(boolean add) {
        try {
            String val = tvResult.getText().toString();
            if (val.isEmpty()) val = tvExpression.getText().toString();
            double d = Double.parseDouble(val);
            memory = add ? memory + d : memory - d;
            showToast("M = " + formatNumber(memory));
        } catch (Exception e) { showToast("No result to store"); }
    }

    private void calculate() {
        String expr = expression.toString();
        if (expr.isEmpty()) return;
        try {
            double result = evaluate(expr);
            String formatted = formatNumber(result);
            tvResult.setText(formatted);
            expression.setLength(0);
            expression.append(formatted);
            resultShown = true;
            animateResult();
        } catch (Exception e) { tvResult.setText("Error"); }
    }

    private String formatNumber(double val) {
        if (Double.isNaN(val)) return "Error";
        if (Double.isInfinite(val)) return val > 0 ? "Infinity" : "-Infinity";
        if (val == Math.floor(val) && Math.abs(val) < 1e12) return String.valueOf((long) val);
        return String.format("%.8f", val).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    private double evaluate(String expr) {
        return new ExprParser(expr).parse();
    }

    private void animateButton(View v) {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator sx = ObjectAnimator.ofFloat(v, "scaleX", 1f, 0.88f, 1f);
        ObjectAnimator sy = ObjectAnimator.ofFloat(v, "scaleY", 1f, 0.88f, 1f);
        sx.setDuration(180); sy.setDuration(180);
        sx.setInterpolator(new OvershootInterpolator());
        sy.setInterpolator(new OvershootInterpolator());
        set.playTogether(sx, sy); set.start();
    }

    private void animateResult() {
        tvResult.setAlpha(0f); tvResult.setScaleX(0.8f);
        tvResult.animate().alpha(1f).scaleX(1f).setDuration(300)
                .setInterpolator(new OvershootInterpolator()).start();
    }

    private void showToast(String msg) {
        android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show();
    }

    static class ExprParser {
        private final String input;
        private int pos = 0;
        ExprParser(String input) { this.input = input.replaceAll("\\s+", ""); }
        double parse() { return parseExpr(); }

        private double parseExpr() {
            double r = parseTerm();
            while (pos < input.length() && (input.charAt(pos) == '+' || input.charAt(pos) == '-')) {
                char op = input.charAt(pos++);
                double t = parseTerm();
                r = op == '+' ? r + t : r - t;
            }
            return r;
        }

        private double parseTerm() {
            double r = parsePower();
            while (pos < input.length() && (input.charAt(pos) == '*' || input.charAt(pos) == '/')) {
                char op = input.charAt(pos++);
                double t = parsePower();
                if (op == '/' && t == 0) throw new ArithmeticException("Division by zero");
                r = op == '*' ? r * t : r / t;
            }
            return r;
        }

        private double parsePower() {
            double base = parseUnary();
            if (pos < input.length() && input.charAt(pos) == '^') {
                pos++;
                return Math.pow(base, parseUnary());
            }
            return base;
        }

        private double parseUnary() {
            if (pos < input.length() && input.charAt(pos) == '-') { pos++; return -parsePrimary(); }
            if (pos < input.length() && input.charAt(pos) == '+') { pos++; return parsePrimary(); }
            return parsePrimary();
        }

        private double parsePrimary() {
            if (pos < input.length() && input.charAt(pos) == '(') {
                pos++;
                double r = parseExpr();
                if (pos < input.length() && input.charAt(pos) == ')') pos++;
                return r;
            }
            String[] fns = {"sin","cos","tan","log","ln","sqrt","abs","inv"};
            for (String fn : fns) {
                if (input.startsWith(fn, pos)) {
                    pos += fn.length();
                    if (pos < input.length() && input.charAt(pos) == '(') pos++;
                    double arg = parseExpr();
                    if (pos < input.length() && input.charAt(pos) == ')') pos++;
                    switch (fn) {
                        case "sin":  return Math.sin(Math.toRadians(arg));
                        case "cos":  return Math.cos(Math.toRadians(arg));
                        case "tan":  return Math.tan(Math.toRadians(arg));
                        case "log":  return Math.log10(arg);
                        case "ln":   return Math.log(arg);
                        case "sqrt": return Math.sqrt(arg);
                        case "abs":  return Math.abs(arg);
                        case "inv":  return 1.0 / arg;
                    }
                }
            }
            return parseNumber();
        }

        private double parseNumber() {
            int start = pos;
            if (pos < input.length() && input.charAt(pos) == '-') pos++;
            while (pos < input.length() && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) pos++;
            boolean isPct = pos < input.length() && input.charAt(pos) == '%';
            if (isPct) pos++;
            double v = Double.parseDouble(input.substring(start, isPct ? pos - 1 : pos));
            if (isPct) v /= 100.0;
            if (pos < input.length() && input.charAt(pos) == '!') { pos++; return factorial(v); }
            return v;
        }

        private double factorial(double n) {
            if (n < 0 || n != Math.floor(n)) return Double.NaN;
            long ni = (long) n; double r = 1;
            for (long i = 2; i <= ni; i++) r *= i;
            return r;
        }
    }
}
