package com.example.bangla;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Rule-based phonetic transliterator (Roman -> Bengali), Avro-style.
 * Longest-match-first scanning plus a small context state machine.
 */
@Component
public class BanglaTransliterator {

    public record Token(String src, String out) {
    }

    private enum State {START, CONSONANT, INHERENT, VOWEL, OTHER, BREAK}

    private static final String HASANTA = "্";
    static final String DIGITS = "০১২৩৪৫৬৭৮৯";

    // Consonants
    static final Map<String, String> C = map(
            "k", "ক", "kh", "খ", "g", "গ", "gh", "ঘ", "Ng", "ঙ", "c", "চ", "ch", "ছ", "j", "জ", "jh", "ঝ",
            "T", "ট", "Th", "ঠ", "D", "ড", "Dh", "ঢ", "N", "ণ", "t", "ত", "th", "থ", "d", "দ", "dh", "ধ",
            "n", "ন", "p", "প", "ph", "ফ", "f", "ফ", "b", "ব", "bh", "ভ", "v", "ভ", "m", "ম", "z", "য",
            "r", "র", "l", "ল", "sh", "শ", "S", "ষ", "s", "স", "h", "হ", "R", "ড়", "Rh", "ঢ়", "y", "য়",
            "ksh", "ক্ষ", "t`", "ৎ",
            "NG", "ঞ", "GG", "জ্ঞ", "nj", "ঞ্জ", "njh", "ঞ্ঝ", "nc", "ঞ্চ", "nch", "ঞ্ছ");

    // Vowels: [independent form, vowel sign (kar)]
    static final Map<String, String[]> V = new HashMap<>();

    static {
        v("a", "আ", "া");
        v("i", "ই", "ি");
        v("I", "ঈ", "ী");
        v("ee", "ঈ", "ী");
        v("u", "উ", "ু");
        v("U", "ঊ", "ূ");
        v("oo", "ঊ", "ূ");
        v("e", "এ", "ে");
        v("oi", "ঐ", "ৈ");
        v("O", "ও", "ো");
        v("ou", "ঔ", "ৌ");
        v("rri", "ঋ", "ৃ");
    }

    // Signs and punctuation
    static final Map<String, String> X = map("ng", "ং", ":", "ঃ", "^", "ঁ", ".", "।");

    // Every pattern the scanner knows, longest first
    static final List<String> KEYS = Stream.of(C.keySet(), V.keySet(), X.keySet(), Set.of("o", "`"))
            .flatMap(Collection::stream)
            .sorted(Comparator.comparingInt(String::length).reversed())
            .toList();

    static String matchAt(String s, int i) {
        for (String k : KEYS) {
            if (s.startsWith(k, i)) return k;
        }
        return null;
    }

    public String convert(String text) {
        // Convert word by word, keep whitespace as is
        StringBuilder sb = new StringBuilder();
        for (String part : text.split("(?<=\\s)|(?=\\s)")) {
            if (part.isBlank()) sb.append(part);
            else tokenize(part).forEach(t -> sb.append(t.out()));
        }
        return sb.toString();
    }

    public List<Token> tokenize(String s) {
        List<Token> toks = new ArrayList<>();
        State st = State.START;
        boolean hasSign = false;   // word already contains a vowel sign
        int i = 0;

        String lastCons = null;    // previous consonant token

        while (i < s.length()) {
            String k = matchAt(s, i);
            if (k == null) {                       // unknown char: pass through
                char ch = s.charAt(i++);
                String out = (ch >= '0' && ch <= '9') ? String.valueOf(DIGITS.charAt(ch - '0')) : String.valueOf(ch);
                toks.add(new Token(String.valueOf(ch), out));
                st = State.START;
                hasSign = false;
                continue;
            }
            i += k.length();
            String out;
            State next;

            if (C.containsKey(k)) {                // consonant (+ hasanta if it follows a consonant)
                if (k.equals("y") && st == State.CONSONANT) {
                    // ya-phola (্য). After "r" a ZWJ keeps র from turning into a reph: র‍্য
                    out = ("r".equals(lastCons) ? "\u200D" : "") + HASANTA + "য";
                } else {
                    out = (st == State.CONSONANT ? HASANTA : "") + C.get(k);
                }
                next = State.CONSONANT;
                lastCons = k;
            } else if (V.containsKey(k)) {         // vowel: sign after a consonant, else independent
                out = V.get(k)[(st == State.CONSONANT || st == State.INHERENT) ? 1 : 0];
                next = State.VOWEL;
                hasSign = true;
            } else if (X.containsKey(k)) {
                out = X.get(k);
                next = State.OTHER;
            } else if (k.equals("`")) {            // explicit break
                out = "";
                next = State.BREAK;
            } else {                               // plain "o"
                if (st == State.CONSONANT) {
                    boolean atEnd = i >= s.length() || !Character.isLetter(s.charAt(i));
                    if (atEnd && hasSign) {
                        out = "ো";
                        next = State.VOWEL;
                    } else {
                        out = "";
                        next = State.INHERENT;
                    }
                } else if (st == State.VOWEL) {
                    out = "ও";
                    next = State.VOWEL;
                } else {
                    out = "অ";
                    next = State.VOWEL;
                }
            }
            toks.add(new Token(k, out));
            st = next;
        }
        return toks;
    }

    private static void v(String key, String full, String sign) {
        V.put(key, new String[]{full, sign});
    }

    static Map<String, String> map(String... kv) {
        Map<String, String> m = new HashMap<>();
        for (int i = 0; i < kv.length; i += 2) m.put(kv[i], kv[i + 1]);
        return m;
    }
}
