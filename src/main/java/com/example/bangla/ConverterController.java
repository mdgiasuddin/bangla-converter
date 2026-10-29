package com.example.bangla;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ConverterController {

    public record Result(String output, List<BanglaTransliterator.Token> tokens, List<String> suggestions) {
    }

    private final BanglaTransliterator transliterator;
    private final SuggestionService suggestionService;

    public ConverterController(BanglaTransliterator transliterator, SuggestionService suggestionService) {
        this.transliterator = transliterator;
        this.suggestionService = suggestionService;
    }

    /**
     * GET /api/transliterate?text=ami bhalo achi
     */
    @GetMapping("/transliterate")
    public Result transliterate(@RequestParam String text) {
        String[] words = text.split("\\s+");
        String last = (text.isEmpty() || Character.isWhitespace(text.charAt(text.length() - 1)) || words.length == 0)
                ? "" : words[words.length - 1];
        return new Result(
                transliterator.convert(text),
                transliterator.tokenize(last),
                suggestionService.suggest(last, 6));
    }

    /**
     * GET /api/candidates?word=sona -> {"candidates":["সনা","সোনা","শোনা","শোণা","sona"],"defaultIndex":1}
     */
    @GetMapping("/candidates")
    public SuggestionService.Candidates candidates(@RequestParam String word) {
        return suggestionService.candidates(word);
    }
}
