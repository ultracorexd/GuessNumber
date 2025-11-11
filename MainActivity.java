package com.example.uriapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private EditText inputEditText;
    private RadioGroup radioGroup;
    private RadioButton radioWeb, radioGeo, radioPhone;
    private Button actionButton;


    private static final Pattern WEB_PATTERN = Pattern.compile("^(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?$");
    private static final Pattern COORDINATES_PATTERN = Pattern.compile("^-?\\d{1,3}\\.?\\d*\\s*-?\\d{1,3}\\.?\\d*$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(8|\\+7)[\\s-]?\\d{3}[\\s-]?\\d{3}[\\s-]?\\d{2}[\\s-]?\\d{2}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupButtonClickListener();
    }

    private void initViews() {
        inputEditText = findViewById(R.id.inputEditText);
        radioGroup = findViewById(R.id.radioGroup);
        radioWeb = findViewById(R.id.radioWeb);
        radioGeo = findViewById(R.id.radioGeo);
        radioPhone = findViewById(R.id.radioPhone);
        actionButton = findViewById(R.id.actionButton);
    }

    private void setupButtonClickListener() {
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processInput();
            }
        });
    }

    private void processInput() {
        String input = inputEditText.getText().toString().trim();

        if (TextUtils.isEmpty(input)) {
            showError("Пожалуйста, введите данные");
            return;
        }

        int selectedRadioId = radioGroup.getCheckedRadioButtonId();

        if (selectedRadioId == -1) {
            autoDetectAndProcess(input);
        } else {
            processWithSelectedType(input, selectedRadioId);
        }
    }

    private void autoDetectAndProcess(String input) {
        if (isValidWebAddress(input)) {
            openWebPage(input);
        } else if (isValidCoordinates(input)) {
            openMap(input);
        } else if (isValidPhoneNumber(input)) {
            simulatePhoneCall(input);
        } else {
            showError("Неверный ввод");
        }
    }

    private void processWithSelectedType(String input, int selectedRadioId) {
        boolean isValid = false;

        if (selectedRadioId == R.id.radioWeb && isValidWebAddress(input)) {
            openWebPage(input);
            isValid = true;
        } else if (selectedRadioId == R.id.radioGeo && isValidCoordinates(input)) {
            openMap(input);
            isValid = true;
        } else if (selectedRadioId == R.id.radioPhone && isValidPhoneNumber(input)) {
            simulatePhoneCall(input);
            isValid = true;
        }

        if (!isValid) {
            showError("Неверный ввод или выбор");
        }
    }

    private boolean isValidWebAddress(String input) {
        return WEB_PATTERN.matcher(input).matches();
    }

    private boolean isValidCoordinates(String input) {
        if (!COORDINATES_PATTERN.matcher(input).matches()) {
            return false;
        }

        try {
            String[] coords = input.split("\\s+");
            if (coords.length != 2) return false;

            double lat = Double.parseDouble(coords[0]);
            double lon = Double.parseDouble(coords[1]);

            return lat >= -90 && lat <= 90 && lon >= -180 && lon <= 180;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidPhoneNumber(String input) {
        // Убираем все пробелы и дефисы для проверки длины
        String cleanPhone = input.replaceAll("[\\s-]", "");
        return PHONE_PATTERN.matcher(input).matches() && cleanPhone.length() >= 11;
    }

    private void openWebPage(String url) {
        try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            showError("Не удалось открыть веб-страницу");
        }
    }

    private void openMap(String coordinates) {
        try {
            String[] coords = coordinates.split("\\s+");
            double lat = Double.parseDouble(coords[0]);
            double lon = Double.parseDouble(coords[1]);

            String geoUri = "geo:" + lat + "," + lon + "?q=" + lat + "," + lon;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
            startActivity(intent);
        } catch (Exception e) {
            showError("Не удалось открыть карты");
        }
    }

    private void simulatePhoneCall(String phoneNumber) {
        String cleanPhone = phoneNumber.replaceAll("[\\s-]", "");

        new AlertDialog.Builder(this)
                .setTitle("Телефонный звонок")
                .setMessage("Осуществляю звонок по телефону: " + cleanPhone)
                .setPositiveButton("OK", null)
                .show();


    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}