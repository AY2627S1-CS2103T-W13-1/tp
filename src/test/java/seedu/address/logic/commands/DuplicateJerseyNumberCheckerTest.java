package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class DuplicateJerseyNumberCheckerTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void getWarning_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> DuplicateJerseyNumberChecker.getWarning(null, ALICE));
        assertThrows(NullPointerException.class, () -> DuplicateJerseyNumberChecker.getWarning(model, null));
    }

    @Test
    public void getWarning_noJerseyNumber_returnsEmptyString() {
        assertEquals("", DuplicateJerseyNumberChecker.getWarning(model, ALICE));
    }

    @Test
    public void getWarning_uniqueJerseyNumber_returnsEmptyString() {
        Person player = addPlayer("Tan Wei Ming", "10");
        addPlayer("Lim Wei Jie", "11");

        assertEquals("", DuplicateJerseyNumberChecker.getWarning(model, player));
    }

    @Test
    public void getWarning_sharedJerseyNumber_namesOtherPlayers() {
        addPlayer("Lim Wei Jie", "10");
        addPlayer("Aaron Lee", "10");
        Person player = addPlayer("Tan Wei Ming", "10");

        assertEquals(String.format(Messages.MESSAGE_DUPLICATE_JERSEY_NUMBER, "10", "Lim Wei Jie, Aaron Lee"),
                DuplicateJerseyNumberChecker.getWarning(model, player));
    }

    @Test
    public void getWarning_leadingZero_treatedAsSameNumber() {
        addPlayer("Lim Wei Jie", "7");
        Person player = addPlayer("Tan Wei Ming", "07");

        assertEquals(String.format(Messages.MESSAGE_DUPLICATE_JERSEY_NUMBER, "7", "Lim Wei Jie"),
                DuplicateJerseyNumberChecker.getWarning(model, player));
    }

    @Test
    public void getWarning_jerseyNumberZero_notConfusedWithNoNumber() {
        // ALICE and the other typical players have no jersey number, so 0 is unique
        Person player = addPlayer("Tan Wei Ming", "0");
        assertEquals("", DuplicateJerseyNumberChecker.getWarning(model, player));

        addPlayer("Lim Wei Jie", "0");
        assertEquals(String.format(Messages.MESSAGE_DUPLICATE_JERSEY_NUMBER, "0", "Lim Wei Jie"),
                DuplicateJerseyNumberChecker.getWarning(model, player));
    }

    @Test
    public void getWarning_teammateHiddenByFilter_stillNamed() {
        addPlayer("Lim Wei Jie", "10");
        Person player = addPlayer("Tan Wei Ming", "10");
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("Tan")));

        assertEquals(String.format(Messages.MESSAGE_DUPLICATE_JERSEY_NUMBER, "10", "Lim Wei Jie"),
                DuplicateJerseyNumberChecker.getWarning(model, player));
    }

    private Person addPlayer(String name, String jerseyNumber) {
        Person player = new PersonBuilder().withName(name).withJerseyNumber(jerseyNumber).build();
        model.addPerson(player);
        return player;
    }
}
