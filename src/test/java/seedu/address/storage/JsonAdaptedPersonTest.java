package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.BENSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.JerseyNumber;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.testutil.PersonBuilder;

public class JsonAdaptedPersonTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+65abcd";
    private static final String INVALID_JERSEY_NUMBER = "100";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();
    private static final String VALID_JERSEY_NUMBER = "10";

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(BENSON);
        assertEquals(BENSON, person.toModelType());
    }

    @Test
    public void toModelType_withJerseyNumber_returnsPerson() throws Exception {
        Person player = new PersonBuilder(BENSON).withJerseyNumber(VALID_JERSEY_NUMBER).build();
        assertEquals(player, new JsonAdaptedPerson(player).toModelType());
    }

    @Test
    public void toModelType_nullJerseyNumber_returnsPersonWithoutJerseyNumber() throws Exception {
        // players saved by v1.3 have no jersey number field
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, null);
        assertEquals(BENSON, person.toModelType());
    }

    @Test
    public void toModelType_invalidJerseyNumber_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, INVALID_JERSEY_NUMBER);
        assertThrows(IllegalValueException.class, JerseyNumber.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_slashContainingName_preservesName() throws Exception {
        Person player = new PersonBuilder(BENSON).withName("Muthu s/o Ramasamy").build();
        assertEquals(player, new JsonAdaptedPerson(player).toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(INVALID_NAME, VALID_PHONE, VALID_JERSEY_NUMBER);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_tooLongName_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson("a".repeat(Name.MAX_LENGTH + 1), VALID_PHONE, VALID_JERSEY_NUMBER);
        String expectedMessage = Name.MESSAGE_LENGTH_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(null, VALID_PHONE, VALID_JERSEY_NUMBER);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, INVALID_PHONE, VALID_JERSEY_NUMBER);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, null, VALID_JERSEY_NUMBER);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

}
