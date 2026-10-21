package seedu.address.storage;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String phone;
    private final List<String> tags;

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     *
     * @param name The stored player name.
     * @param phone The stored contact number.
     */
    public JsonAdaptedPerson(String name, String phone) {
        this(name, phone, null);
    }

    /**
     * Creates an adapted player, treating missing tags in older files as an empty list.
     *
     * @param name The stored player name.
     * @param phone The stored contact number.
     * @param tags The stored tag strings, or null in legacy records.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("tags") List<String> tags) {
        this.name = name;
        this.phone = phone;
        this.tags = tags == null ? List.of() : tags;
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        tags = source.getTags().stream().map(Object::toString).sorted(String.CASE_INSENSITIVE_ORDER).toList();
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

        return new Person(modelName, modelPhone, toModelTags());
    }

    /**
     * Converts stored label strings to validated, distinct model values.
     *
     * @throws IllegalValueException If any stored label is invalid or null.
     */
    private Set<Tag> toModelTags() throws IllegalValueException {
        Set<Tag> modelTags = new LinkedHashSet<>();
        for (String tag : tags) {
            if (tag == null || !Tag.isValidTagName(tag)) {
                throw new IllegalValueException(Tag.MESSAGE_CONSTRAINTS);
            }
            modelTags.add(new Tag(tag));
        }
        return modelTags;
    }

}
