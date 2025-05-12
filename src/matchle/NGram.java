package matchle;

import java.util.*;
import java.util.stream.*;

/**
 * <p>Represents an immutable sequence of characters (n-gram).</p>
 * <p>Provides iteration, matching, and containment operations.</p>
 */
public final class NGram implements Iterable<NGram.IndexedCharacter> {

    private final ArrayList<Character> ngram;
    private final Set<Character> charset;

    /**
     * Constructs a new NGram from a list of characters.
     * @param characters List of characters.
     */
    private NGram(List<Character> characters) {
        this.ngram = new ArrayList<>(characters);
        this.charset = new HashSet<>(characters);
    }

    /**
     * Creates an NGram from a list of characters, validating against nulls.
     *
     * @param characters list of characters (non-null, no null elements)
     * @return new NGram instance
     * @throws NullPointerException if the list is null
     * @throws IllegalArgumentException if any element is null
     */
    public static NGram from(List<Character> characters) {
        NullCharacterException.validate(characters);
        return new NGram(characters);
    }

    /**
     * Creates an NGram from a string.
     *
     * @param word non-null input string
     * @return new NGram instance
     * @throws NullPointerException if input string is null
     */
    public static NGram from(String word) {
        Objects.requireNonNull(word, "Input string cannot be null");
        return new NGram(word.chars().mapToObj(c -> (char) c).toList());
    }

    /**
     * Returns the character at a given index.
     *
     * @param index index in the n-gram
     * @return character at the specified index
     */
    public Character get(int index) {
        return ngram.get(index);
    }

    /**
     * Returns the number of characters in the n-gram.
     *
     * @return size of the n-gram
     */
    public int size() {
        return ngram.size();
    }

    /**
     * Converts this NGram into a new list of characters.
     *
     * @return list of characters
     */
    public List<Character> toCharacterList() {
        return new ArrayList<>(ngram);
    }

    /**
     * Checks whether the given object equals this NGram.
     *
     * @param obj object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof NGram)) return false;
        NGram other = (NGram) obj;
        return ngram.equals(other.ngram);
    }

    /**
     * Returns a hash code for this NGram.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(ngram);
    }

    /**
     * Checks if a character at an index matches.
     *
     * @param c indexed character
     * @return true if matches at index
     */
    public boolean matches(IndexedCharacter c) {
        Objects.requireNonNull(c, "Input cannot be null");
        return c.character.equals(ngram.get(c.index));
    }

    /**
     * Checks if the n-gram contains a given character.
     *
     * @param c character to find
     * @return true if contained
     */
    public boolean contains(char c) {
        return charset.contains(c);
    }

    /**
     * Checks if a character exists elsewhere (wrong position).
     *
     * @param c indexed character
     * @return true if character exists elsewhere
     */
    public boolean containsElsewhere(IndexedCharacter c) {
        Objects.requireNonNull(c, "Input cannot be null");
        return charset.contains(c.character) && !matches(c);
    }

    /**
     * Returns a stream of indexed characters.
     *
     * @return stream of IndexedCharacter
     */
    public Stream<IndexedCharacter> stream() {
        return IntStream.range(0, ngram.size())
                .mapToObj(i -> new IndexedCharacter(i, ngram.get(i)));
    }

    /**
     * Returns an iterator over indexed characters.
     *
     * @return Iterator of IndexedCharacter
     */
    @Override
    public Iterator<IndexedCharacter> iterator() {
        return new NGramIterator();
    }

    /**
     * Represents a character and its position in the NGram.
     */
    public static record IndexedCharacter(int index, Character character) {}

    /**
     * Private iterator class for IndexedCharacter.
     */
    private final class NGramIterator implements Iterator<IndexedCharacter> {
        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < ngram.size();
        }

        @Override
        public IndexedCharacter next() {
            if (!hasNext()) throw new NoSuchElementException();
            return new IndexedCharacter(index, ngram.get(index++));
        }
    }
}
