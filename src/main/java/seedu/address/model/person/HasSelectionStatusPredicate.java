package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person} has the given {@code SelectionStatus}.
 */
public class HasSelectionStatusPredicate implements Predicate<Person> {
    /** Accepts every kept player, shared by squad viewing and scoped sorting. */
    public static final HasSelectionStatusPredicate IS_KEPT = new HasSelectionStatusPredicate(SelectionStatus.KEPT);

    private final SelectionStatus status;

    /**
     * Creates a predicate that accepts only players whose selection status is {@code status}.
     */
    public HasSelectionStatusPredicate(SelectionStatus status) {
        requireNonNull(status);
        this.status = status;
    }

    @Override
    public boolean test(Person person) {
        return person.getSelectionStatus() == status;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof HasSelectionStatusPredicate otherPredicate)) {
            return false;
        }

        return status == otherPredicate.status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("status", status).toString();
    }
}
