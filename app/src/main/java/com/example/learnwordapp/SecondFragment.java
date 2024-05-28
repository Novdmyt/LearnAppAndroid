package com.example.learnwordapp;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class SecondFragment extends Fragment {

    private Spinner tableSpinner;
    private EditText wordEditText;
    private EditText translateEditText;
    private Button addButton;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        tableSpinner = view.findViewById(R.id.tableSpinner);
        wordEditText = view.findViewById(R.id.wordEditText);
        translateEditText = view.findViewById(R.id.translateEditText);
        addButton = view.findViewById(R.id.addButton);

        dbHelper = new DatabaseHelper(getActivity());
        db = dbHelper.getWritableDatabase();

        loadTableNames();

        addButton.setOnClickListener(v -> {
            String tableName = (String) tableSpinner.getSelectedItem();
            String word = wordEditText.getText().toString().trim();
            String translate = translateEditText.getText().toString().trim();
            if (!word.isEmpty() && !translate.isEmpty() && tableName != null) {
                addWordToTable(tableName, word, translate);
            } else {
                Toast.makeText(getActivity(), "Please enter word and translation", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
    public void refreshTableNames() {
        if (dbHelper != null && db != null) {
            loadTableNames();
        }
    }

    private void loadTableNames() {
        List<String> tableNames = dbHelper.getTableNames(db);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, tableNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tableSpinner.setAdapter(adapter);
    }

    private void addWordToTable(String tableName, String word, String translate) {
        dbHelper.insertWord(db, tableName, word, translate);
        Toast.makeText(getActivity(), "Word added to table '" + tableName + "'", Toast.LENGTH_SHORT).show();
        wordEditText.setText("");
        translateEditText.setText("");
    }

    @Override
    public void onDestroy() {
        if (db != null && db.isOpen()) {
            db.close();
        }
        super.onDestroy();
    }
}