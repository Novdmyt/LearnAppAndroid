package com.example.learnwordapp;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.speech.tts.TextToSpeech;

import com.example.learnwordapp.database.DatabaseHelper;
import com.example.learnwordapp.database.Word;

import java.util.List;
import java.util.Locale;

public class FourthFragment extends Fragment implements TextToSpeech.OnInitListener {

    private Spinner languageSpinner;
    private Spinner tableSpinner;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private WordAdapter wordAdapter;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private TextToSpeech tts;
    private Locale selectedLanguage = Locale.GERMAN;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fourth, container, false);

        languageSpinner = view.findViewById(R.id.languageSpinner);
        tableSpinner = view.findViewById(R.id.tableSpinner);
        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.recyclerView);

        dbHelper = new DatabaseHelper(getActivity());
        tts = new TextToSpeech(getActivity(), this);

        setupLanguageSpinner();
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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                wordAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                wordAdapter.filter(newText);
                return false;
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        wordAdapter = new WordAdapter(tts, selectedLanguage);
        recyclerView.setAdapter(wordAdapter);

        return view;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(selectedLanguage);
        }
    }

    private void setupLanguageSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.language_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguageName = (String) parent.getItemAtPosition(position);
                if (selectedLanguageName.equals("German")) {
                    selectedLanguage = Locale.GERMAN;
                } else if (selectedLanguageName.equals("English")) {
                    selectedLanguage = Locale.ENGLISH;
                }
                tts.setLanguage(selectedLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
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
