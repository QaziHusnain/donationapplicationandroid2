package com.example.myapplication;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.Donation;
import com.example.myapplication.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DonorListActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_list);

        databaseHelper = new DatabaseHelper(this);

        // Get all donations from the database
        List<Donation> donationList = getAllDonations();

        // Display the donations in a ListView (or any other UI component)
        displayDonations(donationList);

        // Add the export button and set its click listener
        Button exportButton = findViewById(R.id.exportButton);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportToExcel(donationList);
            }
        });
    }

    private List<Donation> getAllDonations() {
        List<Donation> donations = new ArrayList<>();
        Cursor cursor = databaseHelper.getAllDonations();

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_NAME));
                @SuppressLint("Range") String mobile = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_MOBILE));
                @SuppressLint("Range") int amount = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_AMOUNT));

                Donation donation = new Donation(name, mobile, amount);
                donations.add(donation);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return donations;
    }

    private void displayDonations(List<Donation> donations) {
        // Use a ListView to display the donation information
        ListView listView = findViewById(R.id.listView);

        // Create an ArrayAdapter to populate the ListView
        ArrayAdapter<Donation> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                donations
        );

        // Set the adapter for the ListView
        listView.setAdapter(adapter);
    }

    private void exportToExcel(List<Donation> donations) {
        try {
            // Get the "Downloads" directory
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            // Create a directory named "MyDonations" within the "Downloads" directory
            File myDonationsDir = new File(downloadsDir, "MyDonations");
            if (!myDonationsDir.exists()) {
                myDonationsDir.mkdirs();
            }

            // Create the CSV file within the "MyDonations" directory
            File file = new File(myDonationsDir, "donations.csv");
            FileWriter writer = new FileWriter(file);

            // Write header
            writer.write("Name,Mobile,Amount\n");

            // Write data
            for (Donation donation : donations) {
                writer.write(donation.getName() + "," + donation.getMobile() + "," + donation.getAmount() + "\n");
            }

            writer.flush();
            writer.close();

            Toast.makeText(this, "Data exported to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error exporting data", Toast.LENGTH_SHORT).show();
        }
    }

}
