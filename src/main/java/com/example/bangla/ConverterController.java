package com.example.bangla;

import com.example.bangla.model.Candidates;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ConverterController {

    private final SuggestionService suggestionService;

    public ConverterController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    /**
     * GET /api/candidates?word=sona -> {"candidates":["সনা","সোনা","শোনা","শোণা","sona"],"defaultIndex":1}
     */
    @GetMapping("/candidates")
    public Candidates candidates(@RequestParam String word) {
        return suggestionService.candidates(word);
    }
}
