package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.HasSelectionStatusPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.SelectionStatus;

/**
 * Contains integration tests (interaction with the Model) for {@code SquadCommand}.
 */
public class SquadCommandTest {

    private static final HasSelectionStatusPredicate IS_KEPT = new HasSelectionStatusPredicate(SelectionStatus.KEPT);

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_noKeptPlayers_showsEmptySquadMessage() {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(IS_KEPT);

        assertCommandSuccess(new SquadCommand(), model, SquadCommand.MESSAGE_EMPTY_SQUAD, expectedModel);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_oneKeptPlayer_showsOnlyThatPlayer() {
        Person keptAlice = markAs(ALICE, SelectionStatus.KEPT);
        markAs(BENSON, SelectionStatus.CUT);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.updateFilteredPersonList(IS_KEPT);

        assertCommandSuccess(new SquadCommand(), model, SquadCommand.MESSAGE_ONE_PLAYER_IN_SQUAD, expectedModel);
        assertEquals(List.of(keptAlice), model.getFilteredPersonList());
    }

    @Test
    public void execute_manyKeptPlayers_showsThemInTeamOrder() {
        Person keptDaniel = markAs(DANIEL, SelectionStatus.KEPT);
        Person keptAlice = markAs(ALICE, SelectionStatus.KEPT);
        Person keptCarl = markAs(CARL, SelectionStatus.KEPT);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.updateFilteredPersonList(IS_KEPT);
        String expectedMessage = String.format(SquadCommand.MESSAGE_SQUAD_LISTED, 3);

        assertCommandSuccess(new SquadCommand(), model, expectedMessage, expectedModel);
        assertEquals(List.of(keptAlice, keptCarl, keptDaniel), model.getFilteredPersonList());
    }

    @Test
    public void execute_afterFind_showsWholeSquad() {
        Person keptAlice = markAs(ALICE, SelectionStatus.KEPT);
        Person keptBenson = markAs(BENSON, SelectionStatus.KEPT);
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        new SquadCommand().execute(model);

        // the squad replaces the search results instead of narrowing them
        assertEquals(List.of(keptAlice, keptBenson), model.getFilteredPersonList());
    }

    @Test
    public void execute_cutPlayerWhileViewingSquad_removesThemFromView() throws Exception {
        Person keptAlice = markAs(ALICE, SelectionStatus.KEPT);
        Person keptBenson = markAs(BENSON, SelectionStatus.KEPT);
        new SquadCommand().execute(model);

        new SetStatusCommand(INDEX_FIRST_PERSON, SelectionStatus.CUT).execute(model);

        assertEquals(List.of(keptBenson), model.getFilteredPersonList());
        assertEquals(SelectionStatus.CUT,
                model.getAddressBook().getPersonList().get(0).getSelectionStatus());
        assertEquals(keptAlice.getName(), model.getAddressBook().getPersonList().get(0).getName());
    }

    /**
     * Replaces {@code player} in the model with a copy marked as {@code status}, and returns the copy.
     */
    private Person markAs(Person player, SelectionStatus status) {
        Person markedPlayer = player.withSelectionStatus(status);
        model.setPerson(player, markedPlayer);
        return markedPlayer;
    }
}
