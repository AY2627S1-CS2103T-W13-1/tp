package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.SelectionStatus;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String phone;
    private final String selectionStatus;

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("selectionStatus") String selectionStatus) {
        this.name = name;
        this.phone = phone;
        this.selectionStatus = selectionStatus;
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        selectionStatus = source.getSelectionStatus().name();
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

        final SelectionStatus modelSelectionStatus = toModelSelectionStatus();

        return new Person(modelName, modelPhone, modelSelectionStatus);
    }

    /**
     * Converts the saved selection status into the model's {@code SelectionStatus}.
     * Data files saved before selection statuses existed have none, so their players start as pending.
     *
     * @throws IllegalValueException if the saved status is not a valid selection status.
     */
    private SelectionStatus toModelSelectionStatus() throws IllegalValueException {
        if (selectionStatus == null) {
            return SelectionStatus.PENDING;
        }
        try {
            return SelectionStatus.fromName(selectionStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalValueException(SelectionStatus.MESSAGE_CONSTRAINTS);
        }
    }

}
