package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class NameContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = List.of("first");
        List<String> secondPredicateKeywordList = List.of("first", "second");

        NameContainsKeywordsPredicate firstPredicate = new NameContainsKeywordsPredicate(firstPredicateKeywordList);
        NameContainsKeywordsPredicate secondPredicate = new NameContainsKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        NameContainsKeywordsPredicate firstPredicateCopy = new NameContainsKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different person -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_nameContainsKeywords_returnsTrue() {
        // One keyword
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("Alice"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Multiple keywords
        predicate = new NameContainsKeywordsPredicate(List.of("Alice", "Bob"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Only one matching keyword
        predicate = new NameContainsKeywordsPredicate(List.of("Bob", "Carol"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Carol").build()));

        // Mixed-case keywords
        predicate = new NameContainsKeywordsPredicate(List.of("aLIce", "bOB"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // Zero keywords
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of());
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").build()));

        // Non-matching keyword
        predicate = new NameContainsKeywordsPredicate(List.of("Carol"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Keyword matches phone, but does not match name
        predicate = new NameContainsKeywordsPredicate(List.of("12345"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").withPhone("12345").build()));
    }

    @Test
    public void test_partialName_returnsFalse() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("We"));
        assertFalse(predicate.test(new PersonBuilder().withName("Tan Wei Ming").build()));
    }

    @Test
    public void test_slashKeyword_matchesWholeWordOnly() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("S/O"));
        assertTrue(predicate.test(new PersonBuilder().withName("Muthu s/o Ramasamy").build()));
        assertFalse(predicate.test(new PersonBuilder().withName("Muthu Ramasamy").build()));
        assertFalse(predicate.test(new PersonBuilder().withName("Muthu xs/o Ramasamy").build()));
    }

    @Test
    public void test_keywordMatchesPartOfPunctuatedWord_returnsTrue() {
        assertTrue(new NameContainsKeywordsPredicate(List.of("Ming"))
                .test(new PersonBuilder().withName("Tan Wei-Ming").build()));
        assertTrue(new NameContainsKeywordsPredicate(List.of("wei-ming"))
                .test(new PersonBuilder().withName("Tan Wei-Ming").build()));
        assertTrue(new NameContainsKeywordsPredicate(List.of("neil"))
                .test(new PersonBuilder().withName("Shaun O'Neil").build()));
        assertTrue(new NameContainsKeywordsPredicate(List.of("Jr"))
                .test(new PersonBuilder().withName("Lim Jr.").build()));
        assertTrue(new NameContainsKeywordsPredicate(List.of("Captain"))
                .test(new PersonBuilder().withName("Ali (Captain)").build()));
    }

    @Test
    public void test_partialPartOfPunctuatedWord_returnsFalse() {
        assertFalse(new NameContainsKeywordsPredicate(List.of("Min"))
                .test(new PersonBuilder().withName("Tan Wei-Ming").build()));
        assertFalse(new NameContainsKeywordsPredicate(List.of("Wei-Min"))
                .test(new PersonBuilder().withName("Tan Wei-Ming").build()));
    }

    @Test
    public void test_punctuationAroundCompoundWord_matchesWholeCompoundWord() {
        assertTrue(new NameContainsKeywordsPredicate(List.of("O'Neil"))
                .test(new PersonBuilder().withName("Shaun (O'Neil)").build()));
        assertTrue(new NameContainsKeywordsPredicate(List.of("Wei-Ming"))
                .test(new PersonBuilder().withName("Tan Wei-Ming, Jr.").build()));
        assertTrue(new NameContainsKeywordsPredicate(List.of("s/o"))
                .test(new PersonBuilder().withName("Muthu (s/o Ramasamy)").build()));
        assertTrue(new NameContainsKeywordsPredicate(List.of("O'Neil"))
                .test(new PersonBuilder().withName("((O'Neil))").build()));
    }

    @Test
    public void test_punctuationAroundKeyword_isIgnored() {
        Person nestedBrackets = new PersonBuilder().withName("Shaun ((O'Neil))").build();
        assertTrue(new NameContainsKeywordsPredicate(List.of("(O'Neil)")).test(nestedBrackets));
        assertTrue(new NameContainsKeywordsPredicate(List.of("((O'Neil))")).test(nestedBrackets));
        assertTrue(new NameContainsKeywordsPredicate(List.of("(((O'Neil)))")).test(nestedBrackets));
        assertTrue(new NameContainsKeywordsPredicate(List.of("((O'Neil))"))
                .test(new PersonBuilder().withName("Shaun O'Neil").build()));
        assertTrue(new NameContainsKeywordsPredicate(List.of("Jr."))
                .test(new PersonBuilder().withName("Lim (O'Neil (Jr.))").build()));
        assertTrue(new NameContainsKeywordsPredicate(List.of("(lee)"))
                .test(new PersonBuilder().withName("David(lee)").build()));
    }

    @Test
    public void test_punctuationOnlyKeyword_matchesOnlyExactWord() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("()"));
        assertFalse(predicate.test(new PersonBuilder().withName("Shaun (O'Neil)").build()));
        assertTrue(predicate.test(new PersonBuilder().withName("Ali () Tan").build()));
    }

    @Test
    public void test_partialWordWithEdgePunctuation_returnsFalse() {
        assertFalse(new NameContainsKeywordsPredicate(List.of("Min"))
                .test(new PersonBuilder().withName("Tan ((Wei-Ming)), Jr.").build()));
        assertFalse(new NameContainsKeywordsPredicate(List.of("((O'Nei))"))
                .test(new PersonBuilder().withName("Shaun ((O'Neil))").build()));
        assertFalse(new NameContainsKeywordsPredicate(List.of("Mary-J"))
                .test(new PersonBuilder().withName("Cy (((Mary-Jane)))").build()));
    }

    @Test
    public void test_partsSplitByPunctuation_areNotJoined() {
        Person player = new PersonBuilder().withName("David(l(e(e)))").build();
        assertTrue(new NameContainsKeywordsPredicate(List.of("l")).test(player));
        assertFalse(new NameContainsKeywordsPredicate(List.of("lee")).test(player));
        assertFalse(new NameContainsKeywordsPredicate(List.of("l(e(e))")).test(player));
        assertFalse(new NameContainsKeywordsPredicate(List.of("wei"))
                .test(new PersonBuilder().withName("Tan WeiMing").build()));
    }

    @Test
    public void test_keywordOrder_doesNotAffectMatching() {
        Person player = new PersonBuilder().withName("Tan Wei Ming").build();
        assertTrue(new NameContainsKeywordsPredicate(List.of("ming", "tan")).test(player));
        assertTrue(new NameContainsKeywordsPredicate(List.of("tan", "ming")).test(player));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(keywords);

        String expected = NameContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
