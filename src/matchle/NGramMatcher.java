package matchle;

import java.util.*;
import java.util.function.Predicate;

/**
 * <p>Compares two NGrams and produces a consistency Filter.</p>
 */
public final class NGramMatcher {

    private final NGram key;
    private final NGram guess;
    private final boolean[] keyMatch;
    private final boolean[] guessMatch;

    /**
     * Constructs a matcher between a key and a guess.
     *
     * @param key correct word
     * @param guess guessed word
     */
    private NGramMatcher(NGram key, NGram guess) {
        Objects.requireNonNull(key, "Key cannot be null");
        Objects.requireNonNull(guess, "Guess cannot be null");
        this.key = key;
        this.guess = guess;
        this.keyMatch = new boolean[key.size()];
        this.guessMatch = new boolean[guess.size()];
    }

    /**
     * Factory method to create a matcher.
     *
     * @param key correct word
     * @param guess guessed word
     * @return new NGramMatcher
     */
    public static NGramMatcher of(NGram key, NGram guess) {
        return new NGramMatcher(key, guess);
    }

    /**
     * Returns a filter that tests consistency against this key-guess match.
     *
     * @return Filter matching consistent NGrams
     */
    public Filter match() {
        if (key.size() != guess.size()) {
            return Filter.FALSE;
        }

        List<Predicate<NGram>> predicates = new ArrayList<>();
        Map<Character, Integer> keyCharCounts = countKeyOccurrences();

        markExactMatches(predicates, keyCharCounts);
        markMisplacedMatches(predicates, keyCharCounts);
        markAbsentMatches(predicates);

        Predicate<NGram> finalPredicate = predicates.stream()
                .reduce(x -> true, Predicate::and);
        return Filter.from(finalPredicate);
    }

    /**
     * Counts character occurrences in the key.
     *
     * @return map of character to frequency
     */
    Map<Character, Integer> countKeyOccurrences() {
        Map<Character, Integer> counts = new HashMap<>();
        for (int i = 0; i < key.size(); i++) {
            counts.put(key.get(i), counts.getOrDefault(key.get(i), 0) + 1);
        }
        return counts;
    }

    /**
     * Marks exact matches (correct character and position).
     */
    void markExactMatches(List<Predicate<NGram>> predicates, Map<Character, Integer> keyCharCounts) {
        for (int i = 0; i < key.size(); i++) {
            if (key.get(i).equals(guess.get(i))) {
                keyMatch[i] = guessMatch[i] = true;
                keyCharCounts.put(key.get(i), keyCharCounts.get(key.get(i)) - 1);
                int index = i;
                predicates.add(ngram -> ngram.get(index).equals(guess.get(index)));
            }
        }
    }

    /**
     * Marks misplaced matches (correct character, wrong position).
     */
    void markMisplacedMatches(List<Predicate<NGram>> predicates, Map<Character, Integer> keyCharCounts) {
        for (int i = 0; i < guess.size(); i++) {
            if (!guessMatch[i] && keyCharCounts.getOrDefault(guess.get(i), 0) > 0) {
                keyCharCounts.put(guess.get(i), keyCharCounts.get(guess.get(i)) - 1);
                guessMatch[i] = true;
                int guessIndex = i;
                predicates.add(ngram -> ngram.contains(guess.get(guessIndex)));
            }
        }
    }

    /**
     * Marks absent matches (character does not exist in key).
     */
    void markAbsentMatches(List<Predicate<NGram>> predicates) {
        for (int i = 0; i < guess.size(); i++) {
            if (!guessMatch[i] && !key.contains(guess.get(i))) {
                char absentChar = guess.get(i);
                predicates.add(ngram -> !ngram.contains(absentChar));
            }
        }
    }
}
