package com.example.bangla;

import com.example.bangla.model.Candidates;
import com.example.bangla.model.Entry;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Dictionary suggestions. Two ways a word can match:
 * 1. an explicit alias listed in dictionary.txt (e.g. "জ্ঞান gyan gnan jnan"), or
 * 2. a fuzzy pattern: each typed token expands to a character class of confusable
 * Bengali letters, e.g. s/sh/S -> [সশষ].
 */
@Service
public class SuggestionService {

    private static final String S3 = "সশষ", N4 = "নণঙং", T3 = "তটৎ", D2 = "দড", R3 = "রড়ঢ়";
    private static final Map<String, String> CLS = BanglaTransliterator.map(
            "s", S3, "sh", S3, "S", S3, "n", N4, "N", N4, "ng", N4, "Ng", N4, "t", T3, "T", T3,
            "th", "থঠ", "Th", "থঠ", "d", D2, "D", D2, "dh", "ধঢ", "Dh", "ধঢ",
            "r", R3, "R", R3, "j", "জয", "z", "জয", "y", "য়য",
            "a", "আঅা", "i", "ইঈিী", "I", "ইঈিী", "ee", "ইঈিী", "u", "উঊুূ", "U", "উঊুূ", "oo", "উঊুূ",
            "e", "এে", "oi", "ঐৈ", "ou", "ঔৌ", "O", "ওোঅ", "o", "ওোঅ");

    private final List<Entry> dictionary = load();
    private final BanglaTransliterator transliterator;
    private final OptionGenerator options;

    public SuggestionService(BanglaTransliterator transliterator, OptionGenerator options) {
        this.transliterator = transliterator;
        this.options = options;
    }

    /**
     * Candidate list, in order:
     * dictionary matches, the direct conversion, every per-syllable combination, the raw Roman word.
     * Duplicates (compared in NFC form) are removed.
     */
    public Candidates candidates(String word) {
        if (word == null || word.isBlank()) return new Candidates(List.of(), 0);
        Map<String, String> m = new LinkedHashMap<>();
        suggest(word, 5).forEach(w -> m.putIfAbsent(norm(w), w));
        m.putIfAbsent(norm(transliterator.convert(word)), transliterator.convert(word));
        options.combinations(word).forEach(w -> m.putIfAbsent(norm(w), w));
        m.putIfAbsent(norm(word), word);
        return new Candidates(new ArrayList<>(m.values()), 0);
    }

    private static String norm(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFC);
    }

    public List<String> suggest(String word, int limit) {
        if (word == null || word.isBlank()) return List.of();
        List<String> out = new ArrayList<>();

        String lower = word.toLowerCase();                       // 1. explicit aliases first
        dictionary.stream().filter(e -> e.aliases().contains(lower)).forEach(e -> out.add(e.word()));

        String p = pattern(word);                                // 2. fuzzy whole-word matches
        if (!p.isEmpty()) {
            Pattern re = Pattern.compile(p);
            dictionary.stream()
                    .map(Entry::word)
                    .filter(w -> re.matcher(w).matches())
                    .sorted(Comparator.comparingInt(String::length)) // shorter = closer
                    .filter(w -> !out.contains(w))
                    .forEach(out::add);
        }
        return out.stream().limit(limit).toList();
    }

    String pattern(String s) {
        StringBuilder p = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            String k = BanglaTransliterator.matchAt(s, i);
            if (k == null) {
                i++;
                continue;
            }
            i += k.length();

            boolean conjunct = false;
            String cls = CLS.get(k);
            if (cls == null) {
                cls = BanglaTransliterator.C.get(k);
                conjunct = cls != null && cls.indexOf('্') >= 0;   // e.g. ঞ্জ, জ্ঞ, ক্ষ: must match as a unit
            }
            if (cls == null && BanglaTransliterator.V.containsKey(k)) {
                String[] v = BanglaTransliterator.V.get(k);
                cls = v[0] + v[1];
            }
            if (cls == null) cls = BanglaTransliterator.X.get(k);
            if (cls == null) continue;                       // e.g. the backtick

            boolean consonant = BanglaTransliterator.C.containsKey(k) || k.equals("ng");
            String unit = conjunct ? "(?:" + cls + ")" : "[" + cls + "]";
            if (consonant) p.append(unit).append("\u200D?্?");            // hasanta optional
            else if (k.equals("o")) p.append(unit).append('?');            // inherent o optional
            else p.append(unit);
        }
        return p.toString();
    }

    private static List<Entry> load() {
        try (var r = new BufferedReader(new InputStreamReader(
                new ClassPathResource("dictionary.txt").getInputStream(), StandardCharsets.UTF_8))) {
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
