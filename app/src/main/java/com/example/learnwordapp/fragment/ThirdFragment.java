package com.example.learnwordapp.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.learnwordapp.R;
import com.example.learnwordapp.database.DatabaseHelper;
import com.example.learnwordapp.database.Word;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ThirdFragment extends Fragment {

    private Spinner tableSpinner;
    private EditText wordTextView;
    private EditText translateEditText;
    private Button checkButton;
    private Button helpButton;
    private ImageButton speakerButton;
    private Switch randomOrderSwitch;
    private Spinner languageSpinner;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private List<Word> words;
    private int currentIndex = 0;
    private boolean randomOrder = false;
    private TextToSpeech textToSpeech;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_third, container, false);

        tableSpinner = view.findViewById(R.id.tableSpinner);
        wordTextView = view.findViewById(R.id.wordTextView);
        translateEditText = view.findViewById(R.id.translateEditText);
        checkButton = view.findViewById(R.id.checkButton);
        helpButton = view.findViewById(R.id.helpButton);
        speakerButton = view.findViewById(R.id.speakerButton);
        randomOrderSwitch = view.findViewById(R.id.randomOrderSwitch);
        languageSpinner = view.findViewById(R.id.languageSpinner);

        dbHelper = new DatabaseHelper(getActivity());
        db = dbHelper.getWritableDatabase();

        loadTableNames();
        setupLanguageSpinner();

        textToSpeech = new TextToSpeech(getActivity(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH); // Default language
            }
        });

        tableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTable = (String) tableSpinner.getSelectedItem();
                if (selectedTable != null && !selectedTable.isEmpty()) {
                    loadWordsFromTable(selectedTable);
                } else {
                    Toast.makeText(getActivity(), "Please select a table", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        randomOrderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            randomOrder = isChecked;
            if (words != null && !words.isEmpty()) {
                if (randomOrder) {
                    Collections.shuffle(words);
                } else {
                    Collections.sort(words, (w1, w2) -> w1.getWord().compareTo(w2.getWord())); // Sort by word
                }
                currentIndex = 0;
                showNextWord();
            }
        });

        speakerButton.setOnClickListener(v -> speakWord());

        checkButton.setOnClickListener(v -> checkTranslation());
        helpButton.setOnClickListener(v -> showHelp());

        return view;
    }
    public void refreshTableNames() {
        if (dbHelper != null && db != null) {
            loadTableNames();
        }
    }

    private void setupLanguageSpinner() {
        String[] languages = {"English", "German"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage = (String) languageSpinner.getSelectedItem();
                switch (selectedLanguage) {
                    case "English":
                        textToSpeech.setLanguage(Locale.ENGLISH);
                        break;
                    case "German":
                        textToSpeech.setLanguage(Locale.GERMAN);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void loadTableNames() {
        List<String> tableNames = dbHelper.getTableNames(db);
        tableNames.add(0, ""); // Add empty option at the beginning
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, tableNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tableSpinner.setAdapter(adapter);
    }

    private void loadWordsFromTable(String tableName) {
        words = dbHelper.getWordsFromTable(db, tableName);
        currentIndex = 0;
        if (randomOrder) {
            Collections.shuffle(words);
        }
        showNextWord();
    }

    private void showNextWord() {
        if (words != null && !words.isEmpty()) {
            wordTextView.setText("");
            translateEditText.setText(words.get(currentIndex).getTranslation());
        }
    }

    public void checkTranslation() {
        if (words != null && !words.isEmpty()) {
            String userWord = wordTextView.getText().toString().trim();
            String correctWord = words.get(currentIndex).getWord().trim();

            if (userWord.equalsIgnoreCase(correctWord)) {
                Toast.makeText(getActivity(), "Correct!", Toast.LENGTH_SHORT).show();
                currentIndex++;
                if (currentIndex >= words.size()) {
                    currentIndex = 0;
                    if (randomOrder) {
                        Collections.shuffle(words);
                    }
                    Toast.makeText(getActivity(), "All words checked! Starting over.", Toast.LENGTH_SHORT).show();
                }
                wordTextView.setTextColor(Color.BLACK); // Reset text color to black
                showNextWord();
            } else {
                wordTextView.setTextColor(Color.RED); // Set text color to red
                Toast.makeText(getActivity(), "Incorrect! Try again.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "No words loaded!", Toast.LENGTH_SHORT).show();
        }
    }

    public void showHelp() {
        if (words != null && !words.isEmpty()) {
            wordTextView.setText(words.get(currentIndex).getWord());
        }
    }

    public void speakWord() {
        if (words != null && !words.isEmpty()) {
            String word = words.get(currentIndex).getWord();
            textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    public void onDestroy() {
        if (db != null && db.isOpen()) {
            db.close();
        }
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
