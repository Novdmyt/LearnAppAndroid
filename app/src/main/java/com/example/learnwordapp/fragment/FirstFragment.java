// FirstFragment.java
package com.example.learnwordapp.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.learnwordapp.MainActivity;
import com.example.learnwordapp.R;
import com.example.learnwordapp.database.DatabaseHelper;

public class FirstFragment extends Fragment {

    private EditText tableNameEditText;
    private Button createTableButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        tableNameEditText = view.findViewById(R.id.dbNameEditText);
        createTableButton = view.findViewById(R.id.createDbButton);

        createTableButton.setOnClickListener(v -> {
            String tableName = tableNameEditText.getText().toString().trim();
            if (isValidTableName(tableName)) {
                createTable(tableName);
            } else {
                Toast.makeText(getActivity(), "Invalid table name. Ensure it starts with a letter and contains only alphanumeric characters.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private boolean isValidTableName(String tableName) {
        return tableName.matches("[a-zA-Zа-яА-ЯäöüÄÖÜß][a-zA-Zа-яА-ЯäöüÄÖÜß0-9]*");
    }

    private void createTable(String tableName) {
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            if (!dbHelper.doesTableExist(db, tableName)) {
                dbHelper.createTable(db, tableName);
                Toast.makeText(getActivity(), "Table '" + tableName + "' created card. ", Toast.LENGTH_LONG).show();
                tableNameEditText.getText().clear(); // Clear the table name EditText
                refreshTableNamesInFragments(); // Update table names in other fragments
            } else {
                Toast.makeText(getActivity(), "Table '" + tableName + "' already exists.", Toast.LENGTH_SHORT).show();
            }
            db.close();
        } else {
            Toast.makeText(getActivity(), "Failed to create card", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshTableNamesInFragments() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.refreshTableNamesInFragments();
        }
    }
}
