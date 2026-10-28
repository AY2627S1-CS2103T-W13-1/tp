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

public class SimilarNameCheckerTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void getWarning_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> SimilarNameChecker.getWarning(null, ALICE));
        assertThrows(NullPointerException.class, () -> SimilarNameChecker.getWarning(model, null));
    }

    @Test
    public void getWarning_playerNotInTeam_throwsAssertionError() {
        Person outsider = new PersonBuilder().withName("Not In Team").build();
        assertThrows(AssertionError.class, () -> SimilarNameChecker.getWarning(model, outsider));
    }

    @Test
    public void getWarning_noSimilarName_returnsEmptyString() {
        assertEquals("", SimilarNameChecker.getWarning(model, ALICE));
    }

    @Test
    public void getWarning_similarNames_namesEachOtherPlayer() {
        addPlayer("Tan Wei Ming");
        addPlayer("Tan-Wei Ming");
        Person player = addPlayer("TanWei Ming");

        assertEquals(String.format(Messages.MESSAGE_SIMILAR_NAME, "Tan Wei Ming, Tan-Wei Ming"),
                SimilarNameChecker.getWarning(model, player));
    }

    @Test
    public void getWarning_playerHiddenByFilter_stillNamed() {
        addPlayer("Tan Wei Ming");
        Person player = addPlayer("TanWei Ming");
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("TanWei")));

        assertEquals(String.format(Messages.MESSAGE_SIMILAR_NAME, "Tan Wei Ming"),
                SimilarNameChecker.getWarning(model, player));
    }

    private Person addPlayer(String name) {
        Person player = new PersonBuilder().withName(name).build();
        model.addPerson(player);
        return player;
    }
}
