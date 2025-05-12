package matchle;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * <p>Encapsulates a predicate filter on NGrams for match validation.</p>
 */
public final class Filter {

    private final Predicate<NGram> predicate;

    /**
     * Private constructor wrapping a predicate.
     *
     * @param predicate the underlying predicate
     */
    private Filter(Predicate<NGram> predicate) {
        this.predicate = predicate;
    }

    /**
     * Creates a filter from a given predicate.
     *
     * @param predicate predicate to wrap
     * @return new Filter
     * @throws NullPointerException if predicate is null
     */
    public static Filter from(Predicate<NGram> predicate) {
        Objects.requireNonNull(predicate, "Predicate cannot be null");
        return new Filter(predicate);
    }

    /**
     * Tests whether a given NGram matches the filter.
     *
     * @param ngram NGram to test
     * @return true if matched
     */
    public boolean test(NGram ngram) {
        return predicate.test(ngram);
    }

    /**
     * Combines this filter with another using logical AND.
     *
     * @param other optional second filter
     * @return combined Filter
     */
    public Filter and(Optional<Filter> other) {
        return other.map(filter -> new Filter(this.predicate.and(filter.predicate)))
                .orElse(this);
    }

    /**
     * A static filter that matches no ngrams (always false).
     */
    public static final Filter FALSE = new Filter(n -> false);
}
