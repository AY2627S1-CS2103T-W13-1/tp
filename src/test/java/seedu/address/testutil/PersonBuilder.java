package seedu.address.testutil;

import seedu.address.model.person.JerseyNumber;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";

    private Name name;
    private Phone phone;
    private JerseyNumber jerseyNumber;

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        phone = personToCopy.getPhone();
        jerseyNumber = personToCopy.getJerseyNumber().orElse(null);
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
     * Sets the {@code JerseyNumber} of the {@code Person} that we are building.
     */
    public PersonBuilder withJerseyNumber(String jerseyNumber) {
        this.jerseyNumber = new JerseyNumber(jerseyNumber);
        return this;
    }

    /**
     * Removes the {@code JerseyNumber} of the {@code Person} that we are building.
     */
    public PersonBuilder withoutJerseyNumber() {
        this.jerseyNumber = null;
        return this;
    }

    public Person build() {
        return new Person(name, phone, jerseyNumber);
    }

}
