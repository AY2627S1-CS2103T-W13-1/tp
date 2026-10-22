package seedu.address.storage;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.InjuryStatus;
import seedu.address.model.person.JerseyNumber;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Rating;
import seedu.address.model.person.Remark;
import seedu.address.model.person.SelectionStatus;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String phone;
    private final String selectionStatus;
    private final String rating;
    private final List<String> tags;
    private final String remark;
    private final String injuryStatus;
    @JsonInclude(JsonInclude.Include.NON_NULL) // a player without a jersey number is saved without the field
    private final String jerseyNumber;

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    public JsonAdaptedPerson(String name, String phone) {
        this(name, phone, null, null);
    }

    /** Constructs a stored player from a file without ratings. */
    public JsonAdaptedPerson(String name, String phone, String selectionStatus) {
        this(name, phone, selectionStatus, null);
    }

    /** Constructs a stored player from a file without tags. */
    public JsonAdaptedPerson(String name, String phone, String selectionStatus, String rating) {
        this(name, phone, selectionStatus, rating, null);
    }

    /**
     * Creates an adapted player, using model defaults for missing optional fields.
     *
     * @param name The stored player name.
     * @param phone The stored contact number.
     * @param selectionStatus The stored selection decision, or null for pending.
     * @param rating The stored rating, or null for unrated.
     * @param tags The stored labels, or null for no tags.
     */
    public JsonAdaptedPerson(String name, String phone, String selectionStatus, String rating, List<String> tags) {
        this(name, phone, selectionStatus, rating, tags, null);
    }

    /** Creates an adapted player, accepting missing remarks in older data files. */
    public JsonAdaptedPerson(String name, String phone, String selectionStatus, String rating,
                             List<String> tags, String remark) {
        this(name, phone, selectionStatus, rating, tags, remark, null);
    }

    /** Creates an adapted player, accepting missing injury status in older data files. */
    public JsonAdaptedPerson(String name, String phone, String selectionStatus, String rating,
                             List<String> tags, String remark, String injuryStatus) {
        this(name, phone, selectionStatus, rating, tags, remark, injuryStatus, null);
    }

    /**
     * Creates an adapted player, accepting a missing jersey number in older data files.
     * {@code jerseyNumber} is null for a player without one, including every player saved by v1.3.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("selectionStatus") String selectionStatus, @JsonProperty("rating") String rating,
            @JsonProperty("tags") List<String> tags, @JsonProperty("remark") String remark,
            @JsonProperty("injuryStatus") String injuryStatus, @JsonProperty("jerseyNumber") String jerseyNumber) {
        this.name = name;
        this.phone = phone;
        this.selectionStatus = selectionStatus;
        this.rating = rating;
        this.tags = tags == null ? List.of() : tags;
        this.remark = remark;
        this.injuryStatus = injuryStatus;
        this.jerseyNumber = jerseyNumber;
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        remark = source.getRemark().value;
        injuryStatus = source.getInjuryStatus().toString();
        selectionStatus = source.getSelectionStatus().name();
        rating = source.getRating().isRated() ? source.getRating().toString() : null;
        jerseyNumber = source.getJerseyNumber().map(JerseyNumber::toString).orElse(null);
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

        final SelectionStatus modelSelectionStatus = toModelSelectionStatus();

        if (rating != null && !Rating.isValidRating(rating)) {
            throw new IllegalValueException(Rating.MESSAGE_CONSTRAINTS);
        }
        final Rating modelRating = rating == null ? Rating.UNRATED : new Rating(rating);
        final InjuryStatus modelInjuryStatus;
        try {
            modelInjuryStatus = injuryStatus == null ? InjuryStatus.FIT : InjuryStatus.fromString(injuryStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalValueException(InjuryStatus.MESSAGE_CONSTRAINTS);
        }
        final Remark modelRemark = new Remark(remark == null ? "" : remark);

        if (jerseyNumber != null && !JerseyNumber.isValidJerseyNumber(jerseyNumber)) {
            throw new IllegalValueException(JerseyNumber.MESSAGE_CONSTRAINTS);
        }
        final JerseyNumber modelJerseyNumber = jerseyNumber == null ? null : new JerseyNumber(jerseyNumber);

        return new Person(modelName, modelPhone, modelSelectionStatus, modelRating, toModelTags(),
                modelRemark, modelInjuryStatus, modelJerseyNumber);
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
