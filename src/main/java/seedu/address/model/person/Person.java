package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;
import java.util.Optional;

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
    private final JerseyNumber jerseyNumber; // null if the player has no jersey number

    /**
     * Creates a player without a jersey number. Every field must be present and not null.
     */
    public Person(Name name, Phone phone) {
        this(name, phone, null);
    }

    /**
     * Creates a player. The name and phone must be present and not null.
     * {@code jerseyNumber} may be null if the player has no jersey number.
     */
    public Person(Name name, Phone phone, JerseyNumber jerseyNumber) {
        requireAllNonNull(name, phone);
        this.name = name;
        this.phone = phone;
        this.jerseyNumber = jerseyNumber;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Optional<JerseyNumber> getJerseyNumber() {
        return Optional.ofNullable(jerseyNumber);
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
                && Objects.equals(jerseyNumber, otherPerson.jerseyNumber);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, jerseyNumber);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("jerseyNumber", jerseyNumber)
                .toString();
    }

}
