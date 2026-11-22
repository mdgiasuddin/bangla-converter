# English → Bengali Typing Guideline

Table order follows the Bengali alphabet (স্বরবর্ণ → ব্যঞ্জনবর্ণ → চিহ্ন → যুক্তবর্ণ).
Input is **case-sensitive** (`T` ≠ `t`, `N` ≠ `n`, `S` ≠ `s`, …).
Where several keys are listed, the first one is the primary key. For vowels with two outputs, the first is the default
and the second is the alternate suggestion.

---

## 1. Independent vowels (word start, or after another vowel)

| Bengali | English key | Alternate       | Notes           |
|---------|-------------|-----------------|-----------------|
| অ       | `o`         |                 | default for `o` |
| আ       | `a`         | `o` → অ         |                 |
| য়া       | `a`         |                 |                 |
| ই       | `i`         |                 | alternate: ঈ    |
| ঈ       | `I`, `ee`   |                 | alternate: ই    |
| উ       | `u`         |                 | alternate: ঊ    |
| ঊ       | `oo`        |                 | alternate: উ    |
| ঋ       | `rri`, `Ri` |                 |                 |
| এ       | `e`         |                 |                 |
| ঐ       | `oi`        |                 |                 |
| ও       | `O`         | `o` (alternate) |                 |
| ঔ       | `ou`        |                 |                 |

## 2. Vowel signs (কার) after a consonant

| Bengali                | English key | Alternate       | Notes                         |
|------------------------|-------------|-----------------|-------------------------------|
| (inherent vowel, none) | `o`         | ো               | first option for `o` is empty |
| া                      | `a`         |                 |                               |
| ি                      | `i`         | ী               |                               |
| ী                      | `I`, `ee`   | ি               |                               |
| ু                       | `u`         | ূ                |                               |
| ূ                       | `oo`        | ু ৌ ও             |                               |
| ৃ                       | `rri`, `Ri` |                 |                               |
| ে                      | `e`         |                 |                               |
| ৈ                      | `oi`        | ই, োই            |                               |
| ো                      | `O`         | `o` (alternate) |                               |
| ৌ                      | `ou`        | উ, োউ            |                               |

## 3. Consonants

| Bengali | English key | Notes                                                    |
|---------|-------------|----------------------------------------------------------|
| ক       | `k`         |                                                          |
| খ       | `kh`        |                                                          |
| গ       | `g`         |                                                          |
| ঘ       | `gh`        |                                                          |
| ঙ       | `Ng`        |                                                          |
| চ       | `c`         |                                                          |
| ছ       | `ch`        |                                                          |
| জ       | `j`         | also reachable from `z` (see §6)                         |
| ঝ       | `jh`        |                                                          |
| ঞ       | `NG`        |                                                          |
| ত       | `t`         |                                                          |
| থ       | `th`        |                                                          |
| দ       | `d`         |                                                          |
| ধ       | `dh`        |                                                          |
| ন       | `n`         |                                                          |
| প       | `p`         |                                                          |
| ফ       | `ph`, `f`   |                                                          |
| ব       | `b`         |                                                          |
| ভ       | `bh`, `v`   |                                                          |
| ম       | `m`         |                                                          |
| য       | `z`         | also reachable from `j` (see §6)                         |
| র       | `r`         |                                                          |
| ল       | `l`         |                                                          |
| শ       | `sh`        |                                                          |
| স       | `s`         |                                                          |
| হ       | `h`         |                                                          |
| ড়       | `r`         | no direct key; matched via the `r` / `rh` group (see §6) |
| ঢ়       | `rh`        |                                                          |
| য়       | `y`         |                                                          |
| ৎ       | ``t` ``     | khanda-ta (backtick after `t`)                           |
| ক্স       | `x`         |                                                          |

## 4. Signs and punctuation

| Bengali | English key | Name |
|---------|-------------|------|
| ং       | `ng`        | অনুস্বার |
| ঃ       | `:`         | বিসর্গ  |
| ঁ        | `^`         | চন্দ্রবিন্দু |
| ।       | `.`         | দাঁড়ি   |

## 5. Conjuncts (যুক্তবর্ণ) with a direct key

| Bengali | English key  |
|---------|--------------|
| ক্ক       | `kk`         |
| ক্ষ       | `ksh`, `kkh` |
| ঙ্ক       | `nk`, `ngk`  |
| ঙ্খ       | `ngkh`       |
| ঙ্গ       | `ngg`        |
| ঙ্ঘ       | `nggh`       |
| চ্চ       | `cc`         |
| জ্ঞ       | `gg`         |
| ঞ্চ       | `nc`         |
| ঞ্ছ       | `nch`        |
| ঞ্জ       | `nj`         |
| ঞ্ঝ       | `njh`        |
| ত্থ       | `tth`        |
| ত্ম       | `tt`         |
| দ্দ       | `dd`         |
| ন্ন       | `nn`         |
| প্প       | `pp`         |
| ব্ব       | `bb`         |
| ভ্য       | `vv`         |
| ম্ম       | `mm`         |
| ল্ল       | `ll`         |
| শ্ব       | `ss`         |

## 6. Equivalence groups (Bengali letters that share an English key)

Each English key (left) is treated as matching **any** of the Bengali forms (right). The first key is the primary
spelling, the next ones are alternates.

| Bengali forms | English key(s) |
|---------------|----------------|
| ক্ক, ক্ব, ক্য       | `kk`           |
| ক্ষ, খ্য, ক্ষ্য       | `kkh`          |
| খ, ক্ষ          | `kh`           |
| ঙ্ক, ংক          | `ngk`          |
| ঙ্খ, ংখ, ংখ্য, ঙ্ক্ষ    | `nkh`, `ngkh`  |
| ঙ্গ, ংগ          | `ngg`          |
| ঙ্ঘ, ংঘ          | `nggh`         |
| চ্চ, চ্য          | `cc`           |
| চ্ছ             | `cch`          |
| জ, য          | `j`, `z`       |
| জ্ঞ, গ্গ,গ্য,গ্ম      | `gg`           |
| ঞ, ঙ          | `NG`           |
| ঞ্চ, ন্চ          | `nc`           |
| ঞ্ছ, ন্ছ          | `nch`          |
| ঞ্জ, ন্জ          | `nj`           |
| ঞ্ঝ, ন্ঝ          | `njh`          |
| ন্ধ, ন্ধ্য          | `ndh`          |
| ত, ট          | `t`            |
| ত্থ, থ্য, ঠ্য       | `tth`          |
| ত্ত,ত্ম,ত্ব,ত্য,ট্ট,ট্য,ত্ত্ব | `tt`           |
| থ, ঠ          | `th`           |
| দ, ড          | `d`            |
| দ্দ, ড্ড, দ্য, দ্ব, ড্য | `dd`           |
| দ্ধ, ধ্য, ঢ্য       | `ddh`          |
| ধ, ঢ          | `dh`           |
| ন, ণ          | `n`            |
| ন্ন, ন্ব, ন্য, ণ্য    | `nn`           |
| প্প, প্য          | `pp`           |
| ব্ব, ব্য          | `bb`           |
| ম্ম, ম্য          | `mm`           |
| য়, য          | `y`            |
| র, ড়, ঢ়       | `r`            |
| ড়, ঢ়          | `rh`           |
| ল্ল, ল্য          | `ll`           |
| শ্ব, স্ব, শ্ম, স্ম,   | `ss`           |
| স, শ, ষ       | `s`            |
| শ, ষ          | `sh`           |

---

## Some Special Words

| Bengali word | English keys                   |
|--------------|--------------------------------|
| পাঁচ           | pac, pach                      | 
| তাঁর           | tar                            |
| তাঁরা           | tara                           |
| চাঁদ           | cad, chad                      |
| জ্ঞান           | gyan, gnan, jnan               |
| মিঞা           | mia, miya                      |
| ভূঁইয়া          | vuia, vuiya, bhuia, bhuiya     |
| জ্যোৎস্না          | josna, josona, jochna, jochona |
| স্মরণ          | soron                          |
| স্বাধীন          | sadhin                         |
| স্বাধীনতা         | sadhinota                      |
| শ্মশান          | sosan                          |
| স্মৃতি           | sriti                          |
| সংগ্রাম          | songram, sonram                |
| ব্যর্থ           | barth, byrth                   |
| ব্যক্তি           | bakti, bykti                   |
| দ্বারা           | dara                           |

## Examples

| Typing  | Result |
|---------|--------|
| `ami`   | আমি     |
| `bhalo` | ভালো     |
| `kk`    | ক্ক      |
| `ngk`   | ঙ্ক      |
| `ng`    | ং      |
