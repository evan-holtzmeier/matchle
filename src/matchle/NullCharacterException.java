package matchle;

import java.util.*;

/**
 * <p>Custom exception indicating a null character in an NGram.</p>
 */
public final class NullCharacterException extends Exception {
    private final int index;

    /**
     * Constructs the exception at a specific index.
     *
     * @param index position where null was found
     */
    public NullCharacterException(int index) {
        super("Null character found at index: " + index);
        this.index = index;
    }

    /**
     * Returns the index where the null character occurred.
     *
     * @return index of error
     */
    public int getIndex() {
        return index;
    }

    /**
     * Validates that a list of characters has no nulls.
     *
     * @param ngram list to check
     * @return original list if valid
     * @throws NullPointerException if the list itself is null
     * @throws IllegalArgumentException if any element is null
     */
    public static List<Character> validate(List<Character> ngram) {
        if (ngram == null) throw new NullPointerException("Input list cannot be null");
        for (int i = 0; i < ngram.size(); i++) {
            if (ngram.get(i) == null) {
                throw new IllegalArgumentException(new NullCharacterException(i));
            }
        }
        return ngram;
    }
}
