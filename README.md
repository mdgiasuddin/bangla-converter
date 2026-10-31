# Phonetic Bengali Editor

A single-window editor that turns Roman letters into Bengali as you type (Avro-style phonetic input), built with Spring
Boot 4.1 and Java 25. Type a word, pick from the suggestions with ↑ / ↓, press Space or Enter, and the word is resolved
into plain text.

## Quick start

```bash
docker compose up -d --build        # then open http://localhost:9095
```

Other commands:

```bash
docker compose ps                                # shows "healthy" once the app is up
docker compose logs -f                           # follow the logs
docker compose down                              # stop and remove
docker compose build --build-arg MVN_ARGS=""     # build and also run the unit tests
mvn spring-boot:run                              # run without Docker (needs JDK 25)
```

## How to use the editor

| Action                               | Result                                                                           |
|--------------------------------------|----------------------------------------------------------------------------------|
| Type letters anywhere in the text    | Starts an *active word* at the caret; a suggestion list opens under it           |
| ↑ / ↓                                | Choose another suggestion (the text previews it)                                 |
| Space / Enter                        | Resolve the word with the highlighted suggestion, then type the space or newline |
| Esc                                  | Resolve the word as the Roman letters you typed                                  |
| Click a suggestion                   | Choose it and resolve the word                                                   |
| Backspace inside the active word     | Remove one Roman letter and recompute                                            |
| Backspace after the word is resolved | Normal deletion, one character at a time, no recomputation                       |
| Paste                                | Inserted exactly as copied, in any language, with no conversion                  |

A choice is remembered only when you deliberately pick it (arrow keys or click). The next time you type the same Roman
word, that choice is highlighted first. Choices are stored in the browser (`localStorage`), so no server storage is
needed.

## English to Bengali character mapping

Read the `guideline.md` file to see how the characters are mapped.


## Suggestions

For each active word the list is built in this order, with duplicates removed:

1. **Dictionary matches.** A word whose alias equals what you typed (see `dictionary.txt`), then fuzzy matches. In the
   fuzzy match each typed letter stands for a set of similar sounds, such as `s`, `sh`, `S` → স শ ষ.
2. **The direct conversion**, using the mapping above.
3. **Every combination** of the alternatives for each syllable (up to 60). For `sonar`, `so` gives স শ ষ সো শো ষো, `na`
   gives না ণা, and `r` gives র ড় ঢ়, so 6 × 2 × 3 = 36 words.
4. **The raw Roman word**, in case you want to keep English.


## API

| Endpoint                                     | Returns                                                            |
|----------------------------------------------|--------------------------------------------------------------------|
| `GET /api/candidates?word=sona`              | `{"candidates":[...],"defaultIndex":0}`, the list the editor shows |

## Project layout

| File                                   | Role                                                      |
|----------------------------------------|-----------------------------------------------------------|
| `BanglaTransliterator.java`            | Mapping tables, longest-match scan, context rules         |
| `OptionGenerator.java`                 | Per-syllable alternatives and their combinations          |
| `SuggestionService.java`               | Dictionary matching, aliases and the final candidate list |
| `ConverterController.java`             | The REST endpoints                                        |
| `src/main/resources/dictionary.txt`    | Word list with optional aliases                           |
| `src/main/resources/static/index.html` | The editor (HTML, CSS and JavaScript in one file)         |
| `Dockerfile`, `docker-compose.yml`     | Build and deployment                                      |

## Known limits

- Built for a desktop keyboard. Phone virtual keyboards often don't send normal key events.
- Undo (Ctrl+Z) may not reverse conversions correctly, because the editor edits the text itself.
- The dictionary is a small sample. Larger word lists should be indexed by their first letters instead of being scanned
  for each keystroke.