package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Set;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Matches players with any requested tag, using case-insensitive, complete labels.
 */
public class TagsContainAnyPredicate implements Predicate<Person> {
    private final Set<Tag> tags;

    /**
     * Creates a predicate with an immutable copy of the requested tags.
     *
     * @param tags At least one non-null tag to match.
     * @throws IllegalArgumentException If no tag is supplied.
     */
    public TagsContainAnyPredicate(Set<Tag> tags) {
        requireNonNull(tags);
        checkArgument(!tags.isEmpty(), "A tag filter must contain at least one tag.");
        this.tags = Set.copyOf(tags);
    }

    @Override
    public boolean test(Person person) {
        requireNonNull(person);
        return tags.stream().anyMatch(person.getTags()::contains);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof TagsContainAnyPredicate otherPredicate)) {
            return false;
        }
        return tags.equals(otherPredicate.tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("tags", tags).toString();
    }
}
