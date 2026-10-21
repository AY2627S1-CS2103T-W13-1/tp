package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.storage.JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.person.InjuryStatus;
import seedu.address.model.person.JerseyNumber;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Rating;
import seedu.address.model.person.SelectionStatus;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class JsonAdaptedPersonTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+65abcd";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();
    private static final String VALID_STATUS = BENSON.getSelectionStatus().name();

    @Test
    public void toModelType_withJerseyNumber_returnsPerson() throws Exception {
        Person player = new PersonBuilder(BENSON).withJerseyNumber("10").build();
        assertEquals(player, new JsonAdaptedPerson(player).toModelType());
    }

    @Test
    public void toModelType_nullJerseyNumber_returnsPersonWithoutJerseyNumber() throws Exception {
        // players saved by v1.3 have no jersey number field
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, null, null, null, null, null, null);
        assertEquals(BENSON, person.toModelType());
    }

    @Test
    public void toModelType_invalidJerseyNumber_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, null, null, null, null, null, "100");
        assertThrows(IllegalValueException.class, JerseyNumber.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void jsonRoundTrip_jerseyNumber_savedOnlyWhenPresent() throws Exception {
        // a leading zero is dropped before saving, and the number reloads unchanged
        Person numberedPlayer = new PersonBuilder(BENSON).withJerseyNumber("07").build();
        String numberedJson = JsonUtil.toJsonString(new JsonAdaptedPerson(numberedPlayer));
        assertTrue(numberedJson.matches("(?s).*\"jerseyNumber\"\\s*:\\s*\"7\".*"));
        assertEquals(numberedPlayer, JsonUtil.fromJsonString(numberedJson, JsonAdaptedPerson.class).toModelType());

        // a player without a jersey number is saved without the field
        String unnumberedJson = JsonUtil.toJsonString(new JsonAdaptedPerson(BENSON));
        assertFalse(unnumberedJson.contains("jerseyNumber"));
        assertEquals(BENSON, JsonUtil.fromJsonString(unnumberedJson, JsonAdaptedPerson.class).toModelType());
    }

    @Test
    public void toModelType_rating_roundTripsAndRejectsInvalidScores() throws Exception {
        Person rated = new PersonBuilder(BENSON).withRating("8").build();
        assertEquals(rated, new JsonAdaptedPerson(rated).toModelType());
        assertEquals(BENSON, new JsonAdaptedPerson(VALID_NAME, VALID_PHONE).toModelType());
        for (String invalid : java.util.List.of("", "11", "-1", "8.5", "Unrated")) {
            assertThrows(IllegalValueException.class, () ->
                    new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, null, invalid).toModelType());
        }
    }

    @Test
    public void toModelType_tagsRoundTripAndLegacyRecords() throws Exception {
        Person player = new PersonBuilder(BENSON).withTags("Striker", "Trial Group 2").build();
        assertEquals(player, new JsonAdaptedPerson(player).toModelType());
        assertEquals(BENSON, new JsonAdaptedPerson(VALID_NAME, VALID_PHONE).toModelType());
        assertEquals(Set.of(new Tag("Striker")), new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, null, null,
                List.of("Striker", "striker")).toModelType().getTags());
    }

    @Test
    public void toModelType_legacyRatedKeptPlayer_preservesDataWithoutTags() throws Exception {
        Person player = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, "kept", "8").toModelType();
        assertEquals(SelectionStatus.KEPT, player.getSelectionStatus());
        assertEquals(new Rating("8"), player.getRating());
        assertEquals(Set.of(), player.getTags());
        assertEquals(player, new JsonAdaptedPerson(player).toModelType());
    }

    @Test
    public void toModelType_invalidOrNullTag_throwsIllegalValueException() {
        for (String label : Arrays.asList("", "Bad!", null, "a".repeat(Tag.MAX_LENGTH + 1))) {
            JsonAdaptedPerson player = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, null, null, Arrays.asList(label));
            assertThrows(IllegalValueException.class, Tag.MESSAGE_CONSTRAINTS, player::toModelType);
        }
    }

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(BENSON);
        assertEquals(BENSON, person.toModelType());
    }

    @Test
    public void toModelType_slashContainingName_preservesName() throws Exception {
        Person player = new PersonBuilder(BENSON).withName("Muthu s/o Ramasamy").build();
        assertEquals(player, new JsonAdaptedPerson(player).toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(INVALID_NAME, VALID_PHONE, VALID_STATUS);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_tooLongName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson("a".repeat(Name.MAX_LENGTH + 1), VALID_PHONE, VALID_STATUS);
        String expectedMessage = Name.MESSAGE_LENGTH_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(null, VALID_PHONE, VALID_STATUS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, INVALID_PHONE, VALID_STATUS);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, null, VALID_STATUS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_keptPlayer_roundTripsStatus() throws Exception {
        Person keptPlayer = new PersonBuilder(BENSON).withSelectionStatus(SelectionStatus.KEPT).build();
        assertEquals(keptPlayer, new JsonAdaptedPerson(keptPlayer).toModelType());
    }

    @Test
    public void toModelType_nullStatus_returnsPendingPlayer() throws Exception {
        // data files saved before selection statuses existed have no status
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, null);
        assertEquals(SelectionStatus.PENDING, person.toModelType().getSelectionStatus());
    }

    @Test
    public void toModelType_statusInAnyCase_returnsPlayer() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, "cut");
        assertEquals(SelectionStatus.CUT, person.toModelType().getSelectionStatus());
    }

    @Test
    public void toModelType_invalidStatus_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, "maybe");
        assertThrows(IllegalValueException.class, SelectionStatus.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_emptyAndMissingRemark_returnsEmptyRemark() throws Exception {
        assertEquals("", new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, null, null, null, "")
                .toModelType().getRemark().value);
        assertEquals("", new JsonAdaptedPerson(VALID_NAME, VALID_PHONE).toModelType().getRemark().value);
    }

    @Test
    public void toModelType_freeText_preservesPunctuationAndUnicode() throws Exception {
        String text = "Good teamwork! 教练 says: \"keep\"\nWorks well under pressure.";
        Person player = new PersonBuilder(BENSON).withRemark(text).build();
        assertEquals(player, new JsonAdaptedPerson(player).toModelType());
    }

    @Test
    public void toModelType_invalidInjuryStatus_throwsIllegalValueException() {
        for (String value : new String[] {"", "yes", "injured extra"}) {
            JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, null, null, null, null, value);
            assertThrows(IllegalValueException.class, InjuryStatus.MESSAGE_CONSTRAINTS, person::toModelType);
        }
    }

    @Test
    public void toModelType_missingInjuryStatus_defaultsToFit() throws Exception {
        assertEquals(InjuryStatus.FIT,
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE).toModelType().getInjuryStatus());
    }
}
