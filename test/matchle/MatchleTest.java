package matchle;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

/**
 * <p>Comprehensive unit tests for the Matchle project: NGram, Corpus, Filter, NGramMatcher, NullCharacterException.</p>
 */
public class MatchleTest {

    // ===========================
    // NGRAM TESTS
    // ===========================

    @Test
    public void testNGramFromString() {
        NGram ngram = NGram.from("hello");
        assertEquals(5, ngram.size());
        assertEquals(Character.valueOf('h'), ngram.get(0));
        assertEquals(Character.valueOf('o'), ngram.get(4));
    }

    @Test
    public void testNGramFromList() {
        List<Character> chars = Arrays.asList('a', 'b', 'c');
        NGram ngram = NGram.from(chars);
        assertEquals(3, ngram.size());
        assertEquals(Character.valueOf('a'), ngram.get(0));
    }

    @Test
    public void testNGramEqualsAndHashCode() {
        NGram n1 = NGram.from("abc");
        NGram n2 = NGram.from("abc");
        NGram n3 = NGram.from("xyz");
        assertEquals(n1, n2);
        assertNotEquals(n1, n3);
        assertEquals(n1.hashCode(), n2.hashCode());
    }

    @Test
    public void testNGramContains() {
        NGram ngram = NGram.from("matchle");
        assertTrue(ngram.contains('m'));
        assertFalse(ngram.contains('z'));
    }

    @Test
    public void testNGramMatchesIndexedCharacter() {
        NGram ngram = NGram.from("world");
        NGram.IndexedCharacter ic = new NGram.IndexedCharacter(0, 'w');
        assertTrue(ngram.matches(ic));

        NGram.IndexedCharacter icElse = new NGram.IndexedCharacter(1, 'w');
        assertTrue(ngram.containsElsewhere(icElse));
    }

    @Test
    public void testNGramIteratorAndStream() {
        NGram ngram = NGram.from("abc");
        List<Character> characters = new ArrayList<>();
        for (NGram.IndexedCharacter ic : ngram) {
            characters.add(ic.character());
        }
        assertEquals(Arrays.asList('a', 'b', 'c'), characters);
    }

    @Test(expected = NullPointerException.class)
    public void testNGramFromNullStringThrows() {
        NGram.from((String) null);
    }

    @Test(expected = NullPointerException.class)
    public void testNGramFromNullListThrows() {
        NGram.from((List<Character>) null);
    }

    @Test
    public void testNGramEmptyString() {
        NGram ngram = NGram.from("");
        assertEquals(0, ngram.size());
    }

    // ===========================
    // FILTER TESTS
    // ===========================

    @Test
    public void testFilterCreationAndTest() {
        Filter filter = Filter.from(ngram -> ngram.contains('a'));
        assertTrue(filter.test(NGram.from("abc")));
        assertFalse(filter.test(NGram.from("xyz")));
    }

    @Test
    public void testFilterAndCombination() {
        Filter f1 = Filter.from(ngram -> ngram.contains('x'));
        Filter f2 = Filter.from(ngram -> ngram.contains('y'));
        Filter combined = f1.and(Optional.of(f2));

        assertFalse(combined.test(NGram.from("abc")));
        assertFalse(combined.test(NGram.from("xoo")));
    }

    @Test
    public void testFilterFalseConstant() {
        assertFalse(Filter.FALSE.test(NGram.from("any")));
    }

    @Test(expected = NullPointerException.class)
    public void testFilterFromNullThrows() {
        Filter.from(null);
    }

    // ===========================
    // CORPUS TESTS
    // ===========================

    @Test
    public void testCorpusBuilderAddAndBuild() {
        Corpus.Builder builder = Corpus.Builder.empty();
        builder.add(NGram.from("hello"));
        builder.add(NGram.from("world"));
        Corpus corpus = builder.build();
        assertEquals(2, corpus.corpus().size());
    }

    @Test
    public void testCorpusSizeWithFilter() {
        Corpus corpus = Corpus.Builder.empty()
                .add(NGram.from("abc"))
                .add(NGram.from("abd"))
                .build();

        Filter filter = Filter.from(ngram -> ngram.contains('a'));
        assertEquals(2, corpus.size(filter));
    }

    @Test
    public void testCorpusScoringFunctions() {
        Corpus corpus = Corpus.Builder.empty()
                .add(NGram.from("hello"))
                .add(NGram.from("world"))
                .build();

        NGram key = NGram.from("hello");
        NGram guess = NGram.from("world");

        assertTrue(corpus.score(key, guess) >= 0);
        assertTrue(corpus.scoreWorstCase(guess) >= 0);
        assertTrue(corpus.scoreAverageCase(guess) >= 0);
    }

    @Test
    public void testCorpusBestGuessMethods() {
        Corpus corpus = Corpus.Builder.empty()
                .add(NGram.from("one"))
                .add(NGram.from("two"))
                .add(NGram.from("three"))
                .build();

        assertNotNull(corpus.bestWorstCaseGuess());
        assertNotNull(corpus.bestAverageCaseGuess());
    }

    @Test(expected = IllegalStateException.class)
    public void testCorpusEmptyScoreThrows() {
        Corpus corpus = Corpus.Builder.empty().build();
        corpus.score(NGram.from("abc"), NGram.from("abc"));
    }

    @Test(expected = IllegalStateException.class)
    public void testCorpusEmptyBestGuessThrows() {
        Corpus corpus = Corpus.Builder.empty().build();
        corpus.bestWorstCaseGuess();
    }

    @Test
    public void testCorpusBuilderEmptyBuild() {
        Corpus corpus = Corpus.Builder.empty().build();
        assertEquals(0, corpus.corpus().size());
    }

    // ===========================
    // NGRAM MATCHER TESTS
    // ===========================

    @Test
    public void testNGramMatcherExactMatch() {
        NGram key = NGram.from("hello");
        NGram guess = NGram.from("hello");

        Filter filter = NGramMatcher.of(key, guess).match();
        assertTrue(filter.test(key));
    }

    @Test
    public void testNGramMatcherPartialMismatch() {
        NGram key = NGram.from("hello");
        NGram guess = NGram.from("hella");

        Filter filter = NGramMatcher.of(key, guess).match();
        assertFalse(filter.test(NGram.from("world")));
    }

    @Test
    public void testNGramMatcherLengthMismatch() {
        NGram key = NGram.from("hello");
        NGram guess = NGram.from("hi");

        Filter filter = NGramMatcher.of(key, guess).match();
        assertFalse(filter.test(key));
    }

    // ===========================
    // NULL CHARACTER EXCEPTION TESTS
    // ===========================

    @Test(expected = NullCharacterException.class)
    public void testNullCharacterExceptionIsThrown() throws NullCharacterException {
        throw new NullCharacterException(1);
    }


    @Test
    public void testNullCharacterExceptionMessage() {
        NullCharacterException ex = new NullCharacterException(2);
        assertEquals("2", String.valueOf(ex.getMessage()));
    }

    // ===========================
    // STRESS TEST
    // ===========================

    @Test
    public void testCorpusStress() {
        Corpus.Builder builder = Corpus.Builder.empty();
        for (int i = 0; i < 1000; i++) {
            builder.add(NGram.from("word" + i));
        }
        Corpus corpus = builder.build();
        assertNotNull(corpus.bestWorstCaseGuess());
    }
}
