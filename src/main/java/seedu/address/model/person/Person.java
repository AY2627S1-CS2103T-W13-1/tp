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

    // Data fields
    private final SelectionStatus selectionStatus;
    private final Rating rating;
    private final Set<Tag> tags;
    private final Remark remark;

    /**
     * Creates a player who has not been kept or cut yet.
     * Every field must be present and not null.
     *
     * @param name The player's name.
     * @param phone The player's contact number.
     */
    public Person(Name name, Phone phone) {
        this(name, phone, SelectionStatus.PENDING, Rating.UNRATED, Set.of());
    }

    /** Creates a pending, unrated player with the given remark and no tags. */
    public Person(Name name, Phone phone, Remark remark) {
        this(name, phone, SelectionStatus.PENDING, Rating.UNRATED, Set.of(), remark);
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
        this(name, phone, SelectionStatus.PENDING, Rating.UNRATED, tags);
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
        this(name, phone, selectionStatus, rating, Set.of());
    }

    /**
     * Creates a player with all data fields, defensively copying the supplied tags.
     * Every field and tag must be non-null.
     *
     * @param name The player's name.
     * @param phone The player's contact number.
     * @param selectionStatus The coach's selection decision.
     * @param rating The player's rating, or {@link Rating#UNRATED}.
     * @param tags The player's custom labels.
     */
    public Person(Name name, Phone phone, SelectionStatus selectionStatus, Rating rating, Set<Tag> tags) {
        this(name, phone, selectionStatus, rating, tags, new Remark(""));
    }

    /**
     * Creates a player with all data fields, defensively copying the supplied tags.
     * Every field and tag must be non-null.
     */
    public Person(Name name, Phone phone, SelectionStatus selectionStatus, Rating rating, Set<Tag> tags,
                  Remark remark) {
        requireAllNonNull(name, phone, selectionStatus, rating, tags, remark);
        requireAllNonNull(tags);
        this.name = name;
        this.phone = phone;
        this.selectionStatus = selectionStatus;
        this.rating = rating;
        this.remark = remark;
        this.tags = Collections.unmodifiableSet(new LinkedHashSet<>(tags));
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Remark getRemark() {
        return remark;
    }

    /** Returns a copy with the given remark and all other player details unchanged. */
    public Person withRemark(Remark newRemark) {
        return new Person(name, phone, selectionStatus, rating, tags, newRemark);
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
        return new Person(name, phone, newStatus, rating, tags, remark);
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
                && selectionStatus == otherPerson.selectionStatus
                && rating.equals(otherPerson.rating)
                && tags.equals(otherPerson.tags)
                && remark.equals(otherPerson.remark);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, selectionStatus, rating, tags, remark);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("selectionStatus", selectionStatus)
                .add("tags", tags)
                .add("remark", remark)
                .toString();
    }

}
