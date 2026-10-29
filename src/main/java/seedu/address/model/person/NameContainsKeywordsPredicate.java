package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Name} matches any of the keywords given.
 * A keyword matches a whole word of the name, or a part of a word between punctuation,
 * e.g. both {@code Wei-Ming} and {@code Ming} match {@code Tan Wei-Ming}.
 * Punctuation at the start or end of a word or keyword is ignored, so {@code O'Neil}
 * and {@code (O'Neil)} both match {@code Shaun ((O'Neil))}.
 */
public class NameContainsKeywordsPredicate implements Predicate<Person> {
    /** Matches the punctuation that splits a word into parts, i.e. anything but letters and digits. */
    private static final String WORD_PART_SEPARATOR_REGEX = "[^\\p{L}\\p{M}\\p{N}]+";
    /** Matches punctuation at the start or end of a word, e.g. the brackets around {@code (O'Neil)}. */
    private static final String WORD_EDGE_PUNCTUATION_REGEX =
            "(?<=^|\\s)[^\\p{L}\\p{M}\\p{N}\\s]+|[^\\p{L}\\p{M}\\p{N}\\s]+(?=\\s|$)";

    private final List<String> keywords;

    public NameContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Person person) {
        String fullName = person.getName().fullName;
        String nameWithWordsSplitIntoParts = fullName.replaceAll(WORD_PART_SEPARATOR_REGEX, " ");
        String nameWithWordEdgesTrimmed = trimWordEdges(fullName);
        return keywords.stream()
                .anyMatch(keyword -> matchesWordOrPart(fullName, nameWithWordsSplitIntoParts, keyword)
                        || matchesTrimmedKeyword(nameWithWordEdgesTrimmed, nameWithWordsSplitIntoParts, keyword));
    }

    private static boolean matchesWordOrPart(String fullName, String nameWithWordsSplitIntoParts, String keyword) {
        return StringUtil.containsWordIgnoreCase(fullName, keyword)
                || StringUtil.containsWordIgnoreCase(nameWithWordsSplitIntoParts, keyword);
    }

    /**
     * Returns true if {@code keyword}, without punctuation at its edges, matches a word of the name without
     * punctuation at its edges, or a part of a word between punctuation.
     */
    private static boolean matchesTrimmedKeyword(String nameWithWordEdgesTrimmed, String nameWithWordsSplitIntoParts,
            String keyword) {
        String trimmedKeyword = trimWordEdges(keyword);
        if (trimmedKeyword.isEmpty()) {
            return false;
        }
        return StringUtil.containsWordIgnoreCase(nameWithWordEdgesTrimmed, trimmedKeyword)
                || StringUtil.containsWordIgnoreCase(nameWithWordsSplitIntoParts, trimmedKeyword);
    }

    private static String trimWordEdges(String text) {
        return text.replaceAll(WORD_EDGE_PUNCTUATION_REGEX, "");
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NameContainsKeywordsPredicate otherNameContainsKeywordsPredicate)) {
            return false;
        }

        return keywords.equals(otherNameContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
