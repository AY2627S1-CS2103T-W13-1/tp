package seedu.address.testutil;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Rating;
import seedu.address.model.person.Remark;
import seedu.address.model.person.SelectionStatus;
import seedu.address.model.tag.Tag;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";

    private Name name;
    private Phone phone;
    private SelectionStatus selectionStatus;
    private Rating rating = Rating.UNRATED;
    private Remark remark;
    private Set<Tag> tags = Set.of();

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        selectionStatus = SelectionStatus.PENDING;
        remark = new Remark("");
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        phone = personToCopy.getPhone();
        selectionStatus = personToCopy.getSelectionStatus();
        rating = personToCopy.getRating();
        remark = personToCopy.getRemark();
        tags = personToCopy.getTags();
    }

    /**
     * Sets the {@code Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Person} that we are building.
     */
    public PersonBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the custom labels of the player being built.
     */
    public PersonBuilder withTags(String... labels) {
        tags = Arrays.stream(labels).map(Tag::new).collect(Collectors.toCollection(LinkedHashSet::new));
        return this;
    }

    /**
     * Sets the {@code SelectionStatus} of the {@code Person} that we are building.
     */
    public PersonBuilder withSelectionStatus(SelectionStatus selectionStatus) {
        this.selectionStatus = selectionStatus;
        return this;
    }

    /** Sets the rating of the player being built. */
    public PersonBuilder withRating(String rating) {
        this.rating = new Rating(rating);
        return this;
    }

    /** Sets the player's remark. */
    public PersonBuilder withRemark(String remark) {
        this.remark = new Remark(remark);
        return this;
    }

    public Person build() {
        return new Person(name, phone, selectionStatus, rating, tags, remark);
    }

}
