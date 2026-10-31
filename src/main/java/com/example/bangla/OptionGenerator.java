package com.example.bangla;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.bangla.BanglaTransliterator.C;
import static com.example.bangla.BanglaTransliterator.DIGITS;
import static com.example.bangla.BanglaTransliterator.V;
import static com.example.bangla.BanglaTransliterator.X;
import static com.example.bangla.BanglaTransliterator.matchAt;

/**
 * Breaks a Roman word into syllable units and lists every alternative for each unit,
 * then combines them. "sonar" = [so][na][r]:
 * so -> স শ ষ সো শো ষো,  na -> না ণা,  r -> র ড় ঢ়   =>  6 x 2 x 3 = 36 words.
 */
@Component
public class OptionGenerator {

    static final int MAX = 60;   // cap, because long words explode combinatorially

    // Confusable consonants (first entries are replaced by the engine's own mapping)
    private static final Map<String, String[]> GROUP = new HashMap<>();

    static {
        g("স,শ,ষ,স্ব,স্ম", "s", "sh", "S");
        g("ন,ণ", "n", "N");
        g("ত,ট", "t", "T");
        g("থ,ঠ", "th", "Th");
        g("দ,ড,দ্ব", "d", "D");
        g("ধ,ঢ", "dh", "Dh");
        g("র,ড়,ঢ়", "r", "R", "Rh");   // র ড় ঢ়
        g("জ,য", "j", "z");
        g("য়,য", "y");
        g("খ,ক্ষ", "kh");
        g("ঞ,ঙ", "NG");
        g("জ্ঞ,গ্গ,গ্য", "gg");
        g("ঞ্জ,ন্জ", "nj");
        g("ঞ্ঝ,ন্ঝ", "njh");
        g("ঞ্চ,ন্চ", "nc");
        g("ঞ্ছ,ন্ছ", "nch");                      // য় য
        g("ঙ্ক,ংক", "ngk");
        g("ঙ্খ,ংখ", "ngkh");
        g("ঙ্গ,ংগ", "ngg");
        g("ঙ্ঘ,ংঘ", "nggh");
        g("ক্ক,ক্ব,ক্য", "kk");
        g("ক্ষ,খ্য,ক্ষ্য", "kkh");
        g("চ্চ,চ্য", "cc");
        g("ত্ম,ত্ব,ত্য,ট্ট,ট্য", "tt");
        g("ত্থ,থ্য,ঠ্য", "tth");
        g("দ্দ,ড্ড,দ্য,দ্ব,ড্য", "dd");
        g("ন্ন,ন্ব,ন্য,ণ্য", "nn");
        g("প্প,প্য", "pp");
        g("ব্ব,ব্য", "bb");
        g("ম্ম,ম্য", "mm");
        g("ল্ল,ল্য", "ll");
        g("শ্ব,শ্ম,স্ম,স্ব", "ss");
    }

    // Vowel signs after a consonant. For "o" the first option is "" (inherent vowel)
    private static final Map<String, String[]> SIGN = Map.ofEntries(
            Map.entry("a", new String[]{"া"}), Map.entry("i", new String[]{"ি", "ী"}),
            Map.entry("I", new String[]{"ী", "ি"}), Map.entry("ee", new String[]{"ী", "ি"}),
            Map.entry("u", new String[]{"ু", "ূ"}), Map.entry("U", new String[]{"ূ", "ু"}),
            Map.entry("oo", new String[]{"ূ", "ু"}), Map.entry("e", new String[]{"ে"}),
            Map.entry("oi", new String[]{"ৈ"}), Map.entry("ou", new String[]{"ৌ"}),
            Map.entry("O", new String[]{"ো"}), Map.entry("rri", new String[]{"ৃ"}),
            Map.entry("o", new String[]{"", "ো"}));

    // Independent vowels (word start, or after another vowel)
    private static final Map<String, String[]> INDEP = Map.ofEntries(
            Map.entry("a", new String[]{"আ", "অ"}), Map.entry("i", new String[]{"ই", "ঈ"}),
            Map.entry("I", new String[]{"ঈ", "ই"}), Map.entry("ee", new String[]{"ঈ", "ই"}),
            Map.entry("u", new String[]{"উ", "ঊ"}), Map.entry("U", new String[]{"ঊ", "উ"}),
            Map.entry("oo", new String[]{"ঊ", "উ"}), Map.entry("e", new String[]{"এ"}),
            Map.entry("oi", new String[]{"ঐ"}), Map.entry("ou", new String[]{"ঔ"}),
            Map.entry("O", new String[]{"ও"}), Map.entry("rri", new String[]{"ঋ"}),
            Map.entry("o", new String[]{"অ", "ও"}));

    private static void g(String csv, String... keys) {
        for (String k : keys) GROUP.put(k, csv.split(","));
    }

    private static boolean isVowel(String k) {
        return V.containsKey(k) || k.equals("o");
    }

    private static List<String> consonantAlternatives(String k) {
        List<String> out = new ArrayList<>();
        out.add(C.get(k));                 // the engine's own choice first
        for (String alt : GROUP.getOrDefault(k, new String[0])) {
            if (!out.contains(alt)) out.add(alt);
        }
        return out;
    }

    /**
     * One list of alternatives per syllable unit.
     */
    private List<List<String>> units(String s) {
        List<String> toks = new ArrayList<>();
        for (int i = 0; i < s.length(); ) {
            String k = matchAt(s, i);
            if (k == null) k = String.valueOf(s.charAt(i));
            toks.add(k);
            i += k.length();
        }

        List<List<String>> units = new ArrayList<>();
        boolean join = false;                                    // next consonant forms a conjunct
        for (int i = 0; i < toks.size(); i++) {
            String k = toks.get(i);
            boolean ngVowel = k.equals("ng") && i + 1 < toks.size() && isVowel(toks.get(i + 1));
            if (C.containsKey(k) || ngVowel) {
                List<String> cons = ngVowel ? new ArrayList<>(List.of("ঙ", "ঙ্গ")) : consonantAlternatives(k);
                List<String> vows = List.of("");
                boolean hasVowel = false;
                if (i + 1 < toks.size() && isVowel(toks.get(i + 1))) {
                    vows = Arrays.asList(SIGN.get(toks.get(++i)));
                    hasVowel = true;
                }
                if (join && k.equals("y")) cons = new ArrayList<>(List.of("য", "য়"));   // ya-phola first
                if (k.equals("r") && !hasVowel && i + 1 < toks.size() && toks.get(i + 1).equals("y")) {
                    cons = cons.stream().map(c -> c.equals("র") ? "র\u200D" : c).toList();    // র + ZWJ
                }
                String pre = join ? "্" : "";
                List<String> alts = new ArrayList<>();
                for (String v : vows) for (String c : cons) alts.add(pre + c + v);   // vowel-major
                units.add(alts);
                join = !hasVowel && i + 1 < toks.size() && C.containsKey(toks.get(i + 1));
            } else {
                join = false;
                if (isVowel(k)) units.add(Arrays.asList(INDEP.get(k)));
                else if (k.equals("ng")) units.add(List.of("ং", "ঙ"));
                else if (X.containsKey(k)) units.add(List.of(X.get(k)));
                else if (!k.equals("`")) {
                    char ch = k.charAt(0);
                    units.add(List.of(k.length() == 1 && ch >= '0' && ch <= '9'
                            ? String.valueOf(DIGITS.charAt(ch - '0')) : k));
                }
            }
        }
        return units;
    }

    /**
     * Cartesian product of the unit alternatives (odometer, first unit varies slowest).
     */
    public List<String> combinations(String word) {
        List<List<String>> units = units(word);
        List<String> out = new ArrayList<>();
        if (units.isEmpty()) return out;
        int[] idx = new int[units.size()];
        while (out.size() < MAX) {
            StringBuilder sb = new StringBuilder();
            for (int u = 0; u < units.size(); u++) sb.append(units.get(u).get(idx[u]));
            out.add(sb.toString());
            int p = units.size() - 1;
            while (p >= 0 && ++idx[p] == units.get(p).size()) {
                idx[p] = 0;
                p--;
            }
            if (p < 0) break;
        }
        return out;
    }
}
