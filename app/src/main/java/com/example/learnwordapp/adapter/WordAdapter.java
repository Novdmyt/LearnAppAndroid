package com.example.learnwordapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.speech.tts.TextToSpeech;

import com.example.learnwordapp.R;
import com.example.learnwordapp.database.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {

    private List<Word> words = new ArrayList<>();
    private List<Word> filteredWords = new ArrayList<>();
    private TextToSpeech tts;
    private Locale language;

    public WordAdapter(TextToSpeech tts, Locale language) {
        this.tts = tts;
        this.language = language;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        Word word = filteredWords.get(position);
        holder.wordTextView.setText(word.getWord());
        holder.translationTextView.setText(word.getTranslation());

        holder.itemView.setOnClickListener(v -> {
            if (tts != null) {
                tts.speak(word.getWord(), TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredWords.size();
    }

    public void setWords(List<Word> words) {
        this.words = words;
        this.filteredWords = new ArrayList<>(words);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        filteredWords.clear();
        if (query.isEmpty()) {
            filteredWords.addAll(words);
        } else {
            query = query.toLowerCase();
            for (Word word : words) {
                if (word.getWord().toLowerCase().contains(query) || word.getTranslation().toLowerCase().contains(query)) {
                    filteredWords.add(word);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView wordTextView;
        TextView translationTextView;

        WordViewHolder(View itemView) {
            super(itemView);
            wordTextView = itemView.findViewById(R.id.wordTextView);
            translationTextView = itemView.findViewById(R.id.translationTextView);
        }
    }
}
