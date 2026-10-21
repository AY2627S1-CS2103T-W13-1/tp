package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.JerseyNumber;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * Jackson-friendly version of {@link Person}.
 * A player without a jersey number is saved without the jersey number field.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String phone;
    private final String jerseyNumber;

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     * {@code jerseyNumber} is null for a player without one, including every player saved by v1.3.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("jerseyNumber") String jerseyNumber) {
        this.name = name;
        this.phone = phone;
        this.jerseyNumber = jerseyNumber;
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        jerseyNumber = source.getJerseyNumber().map(JerseyNumber::toString).orElse(null);
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.hasValidCharacters(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        if (!Name.hasValidLength(name)) {
            throw new IllegalValueException(Name.MESSAGE_LENGTH_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        if (jerseyNumber != null && !JerseyNumber.isValidJerseyNumber(jerseyNumber)) {
            throw new IllegalValueException(JerseyNumber.MESSAGE_CONSTRAINTS);
        }
        final JerseyNumber modelJerseyNumber = jerseyNumber == null ? null : new JerseyNumber(jerseyNumber);

        return new Person(modelName, modelPhone, modelJerseyNumber);
    }

}
