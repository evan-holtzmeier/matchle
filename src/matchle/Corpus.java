package matchle;

import java.util.*;
import java.util.function.ToLongFunction;

/**
 * <p>Represents a collection of NGrams with filtering and scoring support.</p>
 */
public final class Corpus implements Iterable<NGram> {

    private final Set<NGram> corpus;
    private final int wordSize;

    private Corpus(Set<NGram> corpus, int wordSize) {
        this.corpus = Collections.unmodifiableSet(new HashSet<>(corpus));
        this.wordSize = wordSize;
    }

    /**
     * Returns a copy of the corpus set.
     *
     * @return set of NGrams
     */
    public Set<NGram> corpus() {
        return new HashSet<>(corpus);
    }

    /**
     * Returns the word size for all NGrams in the corpus.
     *
     * @return word size
     */
    public int wordSize() {
        return wordSize;
    }

    /**
     * Returns an iterator over the corpus.
     *
     * @return iterator over NGrams
     */
    @Override
    public Iterator<NGram> iterator() {
        return corpus.iterator();
    }

    /**
     * Counts the number of NGrams that satisfy a given filter.
     *
     * @param filter filtering condition
     * @return number of matches
     */
    public long size(Filter filter) {
        return corpus.stream().filter(filter::test).count();
    }

    /**
     * Builder class for Corpus construction.
     */
    public static final class Builder {
        private final Set<NGram> ngrams = new HashSet<>();

        private Builder() {}

        /**
         * Returns an empty Corpus builder.
         *
         * @return new Builder
         */
        public static Builder empty() {
            return new Builder();
        }

        /**
         * Creates a Builder initialized with another Corpus.
         *
         * @param corpus existing corpus
         * @return new Builder
         */
        public static Builder of(Corpus corpus) {
            Builder builder = new Builder();
            builder.ngrams.addAll(corpus.corpus());
            return builder;
        }

        /**
         * Adds an NGram to the builder.
         *
         * @param ngram NGram to add
         * @return builder itself
         */
        public Builder add(NGram ngram) {
            Objects.requireNonNull(ngram, "NGram cannot be null");
            ngrams.add(ngram);
            return this;
        }

        /**
         * Adds a collection of NGrams to the builder.
         *
         * @param ngrams collection to add
         * @return builder itself
         */
        public Builder addAll(Collection<NGram> ngrams) {
            Objects.requireNonNull(ngrams, "Collection cannot be null");
            for (NGram ngram : ngrams) {
                if (ngram != null) add(ngram);
            }
            return this;
        }

        /**
         * Checks if all NGrams have the same word size.
         *
         * @param wordSize size to check against
         * @return true if consistent
         */
        public boolean isConsistent(Integer wordSize) {
            return ngrams.stream().allMatch(ngram -> ngram.size() == wordSize);
        }

        /**
         * Builds the Corpus from current ngrams.
         *
         * @return new Corpus
         */
        public Corpus build() {
            if (ngrams.isEmpty()) return null;
            int size = ngrams.iterator().next().size();
            if (!isConsistent(size)) return null;
            return new Corpus(ngrams, size);
        }

        /**
         * Returns a new builder with NGrams filtered.
         *
         * @param filter filter to apply
         * @return new Builder
         */
        public Builder filter(Filter filter) {
            Builder filteredBuilder = new Builder();
            ngrams.stream().filter(filter::test).forEach(filteredBuilder::add);
            return filteredBuilder;
        }
    }

    /**
     * Scores how many NGrams survive after applying the filter between key and guess.
     *
     * @param key correct word
     * @param guess guessed word
     * @return number of matches
     */
    public long score(NGram key, NGram guess) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(guess);
        if (corpus.isEmpty()) throw new IllegalStateException("Corpus is empty");
        Filter filter = NGramMatcher.of(key, guess).match();
        return size(filter);
    }

    /**
     * Computes the worst-case score for a guess.
     *
     * @param guess guess to evaluate
     * @return worst-case survivors
     */
    public long scoreWorstCase(NGram guess) {
        Objects.requireNonNull(guess);
        return corpus.stream()
                .mapToLong(key -> score(key, guess))
                .max()
                .orElseThrow(() -> new IllegalStateException("Corpus is empty"));
    }

    /**
     * Computes the average-case score for a guess.
     *
     * @param guess guess to evaluate
     * @return average survivors
     */
    public long scoreAverageCase(NGram guess) {
        Objects.requireNonNull(guess);
        return corpus.stream()
                .mapToLong(key -> score(key, guess))
                .sum();
    }

    /**
     * Returns the guess with the best (minimal) worst-case score.
     *
     * @return best guess
     */
    public NGram bestWorstCaseGuess() {
        return bestGuess(this::scoreWorstCase);
    }

    /**
     * Returns the guess with the best (minimal) average-case score.
     *
     * @return best guess
     */
    public NGram bestAverageCaseGuess() {
        return bestGuess(this::scoreAverageCase);
    }

    /**
     * General-purpose guess selection using a custom scoring function.
     *
     * @param criterion scoring function
     * @return best guess
     */
    public NGram bestGuess(ToLongFunction<NGram> criterion) {
        return corpus.stream()
                .min(Comparator.comparingLong(criterion))
                .orElseThrow(() -> new IllegalStateException("Corpus is empty"));
    }
}
