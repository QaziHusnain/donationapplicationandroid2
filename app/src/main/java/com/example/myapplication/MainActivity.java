package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Build;
import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_REQUEST_CODE = 1;
    private int totalDonation = 0;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        // Request SMS permissions at runtime (if not already granted)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.SEND_SMS},
                        SMS_PERMISSION_REQUEST_CODE
                );
            }
        }

        // Set up UI components
        final EditText nameEditText = findViewById(R.id.nameEditText);
        final EditText mobileEditText = findViewById(R.id.mobileEditText);
        final EditText donationEditText = findViewById(R.id.donationEditText);
        Button donateButton = findViewById(R.id.donateButton);
        Button showTotalButton = findViewById(R.id.showTotalButton);
        Button viewDonorListButton = findViewById(R.id.viewDonorListButton);




        donateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDonation(
                        nameEditText.getText().toString(),
                        mobileEditText.getText().toString(),
                        donationEditText.getText().toString()
                );
            }
        });
        showTotalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click event for the show total button
                showTotalDonation();
            }
        });


        viewDonorListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click event for the view donor list button
                Intent intent = new Intent(MainActivity.this, DonorListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void handleDonation(String name, String mobile, String donationAmountStr) {
        if (!name.isEmpty() && !mobile.isEmpty() && !donationAmountStr.isEmpty()) {
            int donationAmount = Integer.parseInt(donationAmountStr);

            totalDonation += donationAmount;

            // Create a Donation object
            Donation donation = new Donation(name, mobile, donationAmount);

            // Save the donation to the database
            databaseHelper.saveDonation(donation);

            // Send a thank-you SMS to the donor
            sendThankYouSMS(name, mobile, donationAmount);

            // Show a confirmation toast
            Toast.makeText(this, "Donation successful! Thank you, " + name + "!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendThankYouSMS(String name, String mobile, int donationAmount) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            String donorPhoneNumber = mobile;

            String thankYouMessage = name + " السلام علیکم ورحمتہ اللہ وبرکاتہ\n"
                    + donationAmount + " RS آپ کے طرف سے AKF اسلامک سنٹر، پنڈی گھیب کیلئے عطیہ موصول ہوگیا ہے۔\n"
                    + "اللّٰہ تعالیٰ " + name + " کا عطیہ " + donationAmount + " RS قبول فرمائے۔";


            // Use SmsManager to send the SMS with UTF-16 encoding
            SmsManager smsManager = SmsManager.getDefault();

            // Split the message into parts if it exceeds the maximum length
            ArrayList<String> parts = smsManager.divideMessage(thankYouMessage);

            // Send the message parts
            smsManager.sendMultipartTextMessage(donorPhoneNumber, null, parts, null, null);

            // Show a toast indicating that the thank-you message is sent
            Toast.makeText(this, "Thank you message sent in the background", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "SMS permission not granted.", Toast.LENGTH_SHORT).show();
        }
    }



    private void showTotalDonation() {
        // Retrieve total donation from the database
        int savedTotalDonation = databaseHelper.getTotalDonation();

        // Show total donation in a toast
        Toast.makeText(this, "Total Donation: " + savedTotalDonation + " RS", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now send SMS
                // You may choose to show a message or perform other actions
            } else {
                Toast.makeText(this, "SMS permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
