package com.example.learnwordapp.fragment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.LocaleList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.learnwordapp.R;
import com.example.learnwordapp.database.DatabaseHelper;

import java.util.List;

public class SecondFragment extends Fragment {

    private Spinner tableSpinner;
    private Spinner wordLanguageSpinner;
    private Spinner translateLanguageSpinner;
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
        wordLanguageSpinner = view.findViewById(R.id.wordLanguageSpinner);
        translateLanguageSpinner = view.findViewById(R.id.translateLanguageSpinner);
        wordEditText = view.findViewById(R.id.wordEditText);
        translateEditText = view.findViewById(R.id.translateEditText);
        addButton = view.findViewById(R.id.addButton);

        dbHelper = new DatabaseHelper(getActivity());
        db = dbHelper.getWritableDatabase();

        loadTableNames();
        loadLanguageOptions();

        wordLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setLanguageInputType(wordEditText, (String) wordLanguageSpinner.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        translateLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setLanguageInputType(translateEditText, (String) translateLanguageSpinner.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        addButton.setOnClickListener(v -> {
            String tableName = (String) tableSpinner.getSelectedItem();
            String word = wordEditText.getText().toString().trim();
            String translate = translateEditText.getText().toString().trim();
            String wordLanguage = (String) wordLanguageSpinner.getSelectedItem();
            String translateLanguage = (String) translateLanguageSpinner.getSelectedItem();

            if (!word.isEmpty() && !translate.isEmpty() && tableName != null) {
                addWordToTable(tableName, word, translate, wordLanguage, translateLanguage);
            } else {
                Toast.makeText(getActivity(), "Please enter word, translation, and select languages", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void setLanguageInputType(EditText editText, String language) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.restartInput(editText);
        }

        switch (language) {
            case "English":
                editText.setImeHintLocales(LocaleList.forLanguageTags("en"));
                break;
            case "German":
                editText.setImeHintLocales(LocaleList.forLanguageTags("de"));
                break;
            case "Ukrainian":
                editText.setImeHintLocales(LocaleList.forLanguageTags("uk"));
                break;
            case "Russian":
                editText.setImeHintLocales(LocaleList.forLanguageTags("ru"));
                break;
        }
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

    private void loadLanguageOptions() {
        String[] wordLanguages = {"English", "German"};
        String[] translateLanguages = {"Ukrainian", "Russian"};

        ArrayAdapter<String> wordAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, wordLanguages);
        wordAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wordLanguageSpinner.setAdapter(wordAdapter);

        ArrayAdapter<String> translateAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, translateLanguages);
        translateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        translateLanguageSpinner.setAdapter(translateAdapter);
    }

    private void addWordToTable(String tableName, String word, String translate, String wordLanguage, String translateLanguage) {
        dbHelper.insertWord(db, tableName, word, translate);
        Toast.makeText(getActivity(), "Word added to table '" + tableName + "' with languages " + wordLanguage + " and " + translateLanguage, Toast.LENGTH_SHORT).show();
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
