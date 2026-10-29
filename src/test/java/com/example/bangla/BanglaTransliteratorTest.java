package com.example.bangla;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BanglaTransliteratorTest {

    private final BanglaTransliterator t = new BanglaTransliterator();
    private final SuggestionService s = new SuggestionService(t, new OptionGenerator());

    @Test
    void greeting() {
        assertEquals("আমি ভালো আছি", t.convert("ami bhalo achi"));
    }

    @Test
    void anusvara() {
        assertEquals("বাংলা", t.convert("bangla"));
    }

    @Test
    void inherentO() {
        assertEquals("কলম", t.convert("kolom"));
    }

    @Test
    void conjunct() {
        assertEquals("ক্ক", t.convert("kk"));
    }

    @Test
    void explicitO() {
        assertEquals("ভালোবাশি", t.convert("bhalObashi"));
    }

    @Test
    void digits() {
        assertEquals("১২৩।", t.convert("123."));
    }

    @Test
    void candidateList() {
        var list = s.candidates("sona").candidates();
        assertEquals(java.util.List.of("সোনা", "শোনা", "শোণা", "সনা"), list.subList(0, 4));
        assertEquals("sona", list.get(list.size() - 1));
    }

    @Test
    void everyCombination() {
        var list = s.candidates("sonar").candidates();
        assertEquals(37, list.size());                 // 6 x 2 x 3 combinations + the raw word
        assertTrue(list.contains("ষোণাঢ়"));
        assertTrue(list.contains("সোনার"));
    }

    @Test
    void yaPholaAfterRa() {
        assertEquals("র\u200D্যালি", t.convert("ryali"));
        assertEquals("র\u200D্যালি", s.candidates("ryali").candidates().get(0));
    }

    @Test
    void yaPhola() {
        assertEquals("ক্যাট", t.convert("kyaT"));
    }

    @Test
    void specialLetters() {
        assertEquals("চাঁদ", t.convert("ca^d"));
        assertEquals("গঞ্জ", t.convert("gonj"));
        assertEquals("ঝঞ্ঝা", t.convert("jhonjha"));
        assertEquals("জ্ঞান", t.convert("GGan"));
        assertEquals("জ্ঞান", t.convert("jNGan"));
    }

    @Test
    void dictionaryAliases() {
        assertEquals("জ্ঞান", s.candidates("gyan").candidates().get(0));
        assertEquals("চাঁদ", s.candidates("chand").candidates().get(0));
        assertEquals("গঞ্জ", s.candidates("gonj").candidates().get(0));
    }

    @Test
    void fuzzyMatch() {
        assertTrue(s.suggest("shundor", 6).contains("সুন্দর"));
    }
}
