package com.example.learnwordapp;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnwordapp.database.DatabaseHelper;
import com.example.learnwordapp.database.Word;

import java.util.List;

public class FourthFragment extends Fragment {

    private Spinner tableSpinner;
    private RecyclerView recyclerView;
    private WordAdapter wordAdapter;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fourth, container, false);

        tableSpinner = view.findViewById(R.id.tableSpinner);
        recyclerView = view.findViewById(R.id.recyclerView);

        dbHelper = new DatabaseHelper(getActivity());

        loadTableNames();

        tableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTable = (String) parent.getItemAtPosition(position);
                loadWordsFromTable(selectedTable);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        wordAdapter = new WordAdapter();
        recyclerView.setAdapter(wordAdapter);

        return view;
    }
    public void refreshTableNames() {
        if (dbHelper != null && db != null) {
            loadTableNames();
        }
    }

    private void loadTableNames() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> tableNames = dbHelper.getTableNames(db);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, tableNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tableSpinner.setAdapter(adapter);
    }

    private void loadWordsFromTable(String tableName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Word> words = dbHelper.getWordsFromTable(db, tableName);
        wordAdapter.setWords(words);
    }
}
