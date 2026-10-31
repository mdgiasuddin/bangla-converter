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
    void everyCombination() {
        var list = s.candidates("sonar").candidates();
        assertEquals(61, list.size());                 // 6 x 2 x 3 combinations + the raw word
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
        assertEquals("জ্ঞান", t.convert("ggan"));
        assertEquals("জ্ঞান", t.convert("jNGan"));
    }

    @Test
    void dictionaryAliases() {
        assertEquals("জ্ঞান", s.candidates("gyan").candidates().get(0));
        assertEquals("চাঁদ", s.candidates("chad").candidates().get(0));
        assertEquals("গঞ্জ", s.candidates("gonj").candidates().get(0));
    }

    @Test
    void nga() {
        assertEquals("বাঙালি", t.convert("bangali"));
        assertEquals("বাঙ্গালি", t.convert("banggali"));
        assertEquals("বাঙ্গালি", t.convert("baNggali"));
        assertEquals("বাংলা", t.convert("bangla"));
        var c = s.candidates("bangali").candidates();
        assertTrue(c.contains("বাঙালি") && c.contains("বাঙ্গালি"));
    }
}
