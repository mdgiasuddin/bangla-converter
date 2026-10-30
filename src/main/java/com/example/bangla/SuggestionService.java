package com.example.bangla;

import com.example.bangla.model.Candidates;
import com.example.bangla.model.Entry;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.text.Normalizer.Form.NFC;

/**
 * Dictionary suggestions. Two ways a word can match:
 * 1. an explicit alias listed in dictionary.txt (e.g. "জ্ঞান gyan gnan jnan"), or
 * 2. a fuzzy pattern: each typed token expands to a character class of confusable
 * Bengali letters, e.g. s/sh/S -> [সশষ].
 */
@Service
public class SuggestionService {

    private final List<Entry> dictionary = load();
    private final BanglaTransliterator transliterator;
    private final OptionGenerator optionGenerator;

    public SuggestionService(BanglaTransliterator transliterator, OptionGenerator optionGenerator) {
        this.transliterator = transliterator;
        this.optionGenerator = optionGenerator;
    }

    /**
     * Candidate list, in order:
     * dictionary matches, the direct conversion.
     * Duplicates (compared in NFC form) are removed.
     */
    public Candidates candidates(String word) {
        if (word == null || word.isBlank()) {
            return new Candidates(List.of(), 0);
        }

        Map<String, String> m = new LinkedHashMap<>();
        suggest(word, 5).forEach(w -> m.putIfAbsent(norm(w), w));
        String convert = transliterator.convert(word);
        m.putIfAbsent(norm(convert), convert);
        optionGenerator.combinations(word).forEach(w -> m.putIfAbsent(norm(w), w));
        m.putIfAbsent(norm(word), word);

        return new Candidates(new ArrayList<>(m.values()), 0);
    }

    private static String norm(String s) {
        return Normalizer.normalize(s, NFC);
    }

    public List<String> suggest(String word, int limit) {
        if (word == null || word.isBlank()) return List.of();
        List<String> out = new ArrayList<>();

        String lower = word.toLowerCase();
        dictionary.stream()
                .filter(e -> e.aliases().contains(lower))
                .forEach(e -> out.add(e.word()));

        return out.stream().limit(limit).toList();
    }

    private static List<Entry> load() {
        try (var r = new BufferedReader(new InputStreamReader(
                new ClassPathResource("dictionary.txt").getInputStream(), UTF_8))) {
            List<Entry> list = new ArrayList<>();
            r.lines().map(String::trim).filter(s -> !s.isEmpty()).forEach(line -> {
                String[] parts = line.split("\\s+");              // word [alias ...]
                Set<String> aliases = new HashSet<>();
                for (int i = 1; i < parts.length; i++) aliases.add(parts[i].toLowerCase());
                list.add(new Entry(parts[0], aliases));
            });
            return list;
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read dictionary.txt", e);
        }
    }
}