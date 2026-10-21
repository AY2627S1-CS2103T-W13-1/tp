package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final Phone phone;

    // Data fields
    private final SelectionStatus selectionStatus;
    private final Rating rating;

    /**
     * Creates a player who has not been kept or cut yet.
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone) {
        this(name, phone, SelectionStatus.PENDING, Rating.UNRATED);
    }

    /**
     * Creates a player with the given selection status.
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, SelectionStatus selectionStatus) {
        this(name, phone, selectionStatus, Rating.UNRATED);
    }

    /** Creates a pending player with the given rating. */
    public Person(Name name, Phone phone, Rating rating) {
        this(name, phone, SelectionStatus.PENDING, rating);
    }

    /** Creates a player with both a selection status and a rating. */
    public Person(Name name, Phone phone, SelectionStatus selectionStatus, Rating rating) {
        requireAllNonNull(name, phone, selectionStatus, rating);
        this.name = name;
        this.phone = phone;
        this.selectionStatus = selectionStatus;
        this.rating = rating;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Rating getRating() {
        return rating;
    }

    public SelectionStatus getSelectionStatus() {
        return selectionStatus;
    }

    /**
     * Returns a copy of this player with the given selection status and all other details unchanged.
     */
    public Person withSelectionStatus(SelectionStatus newStatus) {
        return new Person(name, phone, newStatus, rating);
    }

    /**
     * Returns true if both persons have the same name, ignoring case and repeated internal spaces.
     * The phone number is not considered, as different players may share a contact number.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getName().isSameName(getName());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && selectionStatus == otherPerson.selectionStatus
                && rating.equals(otherPerson.rating);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, selectionStatus, rating);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("selectionStatus", selectionStatus)
                .toString();
    }

}
