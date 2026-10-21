package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Set<Tag> tags;

    /**
     * Every field must be present and not null.
     *
     * @param name The player's name.
     * @param phone The player's contact number.
     */
    public Person(Name name, Phone phone) {
        this(name, phone, Set.of());
    }

    /**
     * Creates a player with a defensive copy of the supplied tags.
     * Every field and tag must be non-null.
     *
     * @param name The player's name.
     * @param phone The player's contact number.
     * @param tags The player's custom labels.
     */
    public Person(Name name, Phone phone, Set<Tag> tags) {
        requireAllNonNull(name, phone, tags);
        requireAllNonNull(tags);
        this.name = name;
        this.phone = phone;
        this.tags = Collections.unmodifiableSet(new LinkedHashSet<>(tags));
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    /**
     * Returns the player's tags as an unmodifiable set.
     *
     * @return The immutable tag set.
     */
    public Set<Tag> getTags() {
        return tags;
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
                && tags.equals(otherPerson.tags);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("tags", tags)
                .toString();
    }

}
