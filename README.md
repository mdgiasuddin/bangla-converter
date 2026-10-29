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

Matching is **case-sensitive** and **longest match first**: `kh` is read as খ, not ক + হ. Capital letters are different
sounds from small letters (`t` is ত, `T` is ট).

### Consonants

| Type | Bengali | Type    | Bengali | Type | Bengali |
|------|---------|---------|---------|------|---------|
| `k`  | ক       | `kh`    | খ       | `g`  | গ       |
| `gh` | ঘ       | `Ng`    | ঙ       | `c`  | চ       |
| `ch` | ছ       | `j`     | জ       | `jh` | ঝ       |
| `NG` | ঞ       | `T`     | ট       | `Th` | ঠ       |
| `D`  | ড       | `Dh`    | ঢ       | `N`  | ণ       |
| `t`  | ত       | `th`    | থ       | `d`  | দ       |
| `dh` | ধ       | `n`     | ন       | `p`  | প       |
| `ph` | ফ       | `f`     | ফ       | `b`  | ব       |
| `bh` | ভ       | `v`     | ভ       | `m`  | ম       |
| `z`  | য       | `r`     | র       | `l`  | ল       |
| `sh` | শ       | `S`     | ষ       | `s`  | স       |
| `h`  | হ       | `R`     | ড়       | `Rh` | ঢ়       |
| `y`  | য়       | ``t` `` | ৎ       |      |         |

### Conjuncts with their own keys

| Type  | Bengali | Example        |
|-------|---------|----------------|
| `ksh` | ক্ষ       | `ksho`         |
| `GG`  | জ্ঞ       | `GGan` → জ্ঞান    |
| `nj`  | ঞ্জ       | `gonj` → গঞ্জ    |
| `njh` | ঞ্ঝ       | `jhonjha` → ঝঞ্ঝা |
| `nc`  | ঞ্চ       |                |
| `nch` | ঞ্ছ       |                |

Because these keys take priority, `nj` always gives ঞ্জ. To get a plain ন্জ, put a backtick between the letters:
``n`j``.

### Vowels

Each vowel has an **independent form** (at the start of a word, or after another vowel) and a **sign** (*kar*, attached
to a consonant).

| Type        | Independent | Sign | Type  | Independent | Sign |
|-------------|-------------|------|-------|-------------|------|
| `a`         | আ           | া    | `e`   | এ           | ে    |
| `i`         | ই           | ি    | `oi`  | ঐ           | ৈ    |
| `I` or `ee` | ঈ           | ী    | `O`   | ও           | ো    |
| `u`         | উ           | ু     | `ou`  | ঔ           | ৌ    |
| `U` or `oo` | ঊ           | ূ     | `rri` | ঋ           | ৃ     |

The lowercase **`o`** depends on where it is:

| Position                                                              | Result                                   | Example       |
|-----------------------------------------------------------------------|------------------------------------------|---------------|
| Start of a word                                                       | অ                                        | `ogni` → অগ্নি   |
| After a vowel                                                         | ও                                        |               |
| After a consonant                                                     | Nothing (the consonant's inherent sound) | `kolom` → কলম |
| After a consonant, at the end of a word that already has a vowel sign | ো                                        | `bhalo` → ভালো  |

Use capital `O` when you always want ো: `bhalObashi` → ভালোবাশি.

### Signs and punctuation

| Type    | Bengali   | Notes                                                                                                 |
|---------|-----------|-------------------------------------------------------------------------------------------------------|
| `ng`    | ং         | anusvara, e.g. `bangla` → বাংলা                                                                          |
| `^`     | ঁ          | chandrabindu, e.g. `ca^d` → চাঁদ                                                                        |
| `:`     | ঃ         | visarga, e.g. `du:kho`                                                                                |
| `.`     | ।         | full stop; typed right after a word it resolves the word and inserts ।                                |
| `` ` `` | (nothing) | break: stops two letters from combining                                                               |
| `0`–`9` | ০–৯       | Digits are typed as Bengali digits and resolve the active word first. Pasted text is never converted. |

`^` and `:` belong to the word only while a word is active. A standalone `:` or `^` stays as typed. Other punctuation
stays as typed and resolves the active word first.

### Special rules

- **Conjuncts (যুক্তাক্ষর):** two consonants typed together are joined with a hasanta (্). `kk` → ক্ক, `nt` → ন্ত,
  `stri` → স্ত্রি.
- **Ya-phola:** `y` after a consonant is ্য, not য়. `kyaT` → ক্যাট.
- **Ra with ya-phola:** `r` followed by `y` inserts an invisible joiner (ZWJ), so the র is not drawn as a reph.
  `ryali` → র‍্যালি.
- **Reph:** `r` before another consonant is joined with a hasanta, which fonts draw as a reph. `kormo` → কর্ম, `rk` →
  র্ক.

### Examples

| Type              | Result                                         |
|-------------------|------------------------------------------------|
| `ami bhalo achi`  | আমি ভালো আছি                                       |
| `bangla`          | বাংলা                                             |
| `kolom`           | কলম                                            |
| `ca^d`            | চাঁদ                                             |
| `gonj`            | গঞ্জ                                             |
| `jhonjha`         | ঝঞ্ঝা                                             |
| `GGan` or `jNGan` | জ্ঞান                                             |
| `ryali`           | র‍্যালি                                             |
| `sonar`           | সনার, with all combinations offered (see below) |

## Suggestions

For each active word the list is built in this order, with duplicates removed:

1. **Dictionary matches.** A word whose alias equals what you typed (see `dictionary.txt`), then fuzzy matches. In the
   fuzzy match each typed letter stands for a set of similar sounds, such as `s`, `sh`, `S` → স শ ষ.
2. **The direct conversion**, using the mapping above.
3. **Every combination** of the alternatives for each syllable (up to 60). For `sonar`, `so` gives স শ ষ সো শো ষো, `na`
   gives না ণা, and `r` gives র ড় ঢ়, so 6 × 2 × 3 = 36 words.
4. **The raw Roman word**, in case you want to keep English.

### Adding words to the dictionary

Edit `src/main/resources/dictionary.txt`: one Bengali word per line, optionally followed by extra Roman spellings.

```
জ্ঞান gyan gnan jnan
চাঁদ chand chaand cand
```

Rebuild afterwards (`docker compose up -d --build`).

## API

| Endpoint                                     | Returns                                                            |
|----------------------------------------------|--------------------------------------------------------------------|
| `GET /api/candidates?word=sona`              | `{"candidates":[...],"defaultIndex":0}`, the list the editor shows |
| `GET /api/transliterate?text=ami bhalo achi` | `{"output":"আমি ভালো আছি","tokens":[...],"suggestions":[...]}`         |

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