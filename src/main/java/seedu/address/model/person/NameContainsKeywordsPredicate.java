package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Name} matches any of the keywords given.
 * A keyword matches a whole word of the name, or a part of a word between punctuation,
 * e.g. both {@code Wei-Ming} and {@code Ming} match {@code Tan Wei-Ming}.
 */
public class NameContainsKeywordsPredicate implements Predicate<Person> {
    /** Matches the punctuation that splits a word into parts, i.e. anything but letters and digits. */
    private static final String WORD_PART_SEPARATOR_REGEX = "[^\\p{L}\\p{M}\\p{N}]+";

    private final List<String> keywords;

    public NameContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Person person) {
        String fullName = person.getName().fullName;
        String nameWithWordsSplitIntoParts = fullName.replaceAll(WORD_PART_SEPARATOR_REGEX, " ");
        return keywords.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(fullName, keyword)
                        || StringUtil.containsWordIgnoreCase(nameWithWordsSplitIntoParts, keyword));
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
