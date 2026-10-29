package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameContainsKeywordsPredicate;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullPredicate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new FindCommand(null, "find test"));
        assertThrows(NullPointerException.class, () -> new FindCommand(preparePredicate("Alice"), null));
    }

    @Test
    public void equals() {
        NameContainsKeywordsPredicate firstPredicate =
                new NameContainsKeywordsPredicate(List.of("first"));
        NameContainsKeywordsPredicate secondPredicate =
                new NameContainsKeywordsPredicate(List.of("second"));

        FindCommand findFirstCommand = new FindCommand(firstPredicate, "find first");
        FindCommand findSecondCommand = new FindCommand(secondPredicate, "find second");

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate, "find first");
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_nonMatchingKeyword_noPlayerFound() {
        String expectedMessage = "0 players match the current command: find Nobody";
        NameContainsKeywordsPredicate predicate = preparePredicate("Nobody");
        FindCommand command = new FindCommand(predicate, "find Nobody");
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleKeywords_multiplePersonsFound() {
        String expectedMessage = "3 players match the current command: find Kurz Elle Kunz";
        NameContainsKeywordsPredicate predicate = preparePredicate("Kurz Elle Kunz");
        FindCommand command = new FindCommand(predicate, "find Kurz Elle Kunz");
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(CARL, ELLE, FIONA), model.getFilteredPersonList());
    }

    @Test
    public void execute_singleKeyword_onePlayerFound() {
        NameContainsKeywordsPredicate predicate = preparePredicate("alice");
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(new FindCommand(predicate, "find alice"), model,
                "1 player matches the current command: find alice", expectedModel);
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_repeatedAndOverlappingKeywords_eachPlayerListedOnce() {
        NameContainsKeywordsPredicate predicate = preparePredicate("Alice Pauline Alice");
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(new FindCommand(predicate, "find Alice Pauline Alice"), model,
                "1 player matches the current command: find Alice Pauline Alice", expectedModel);
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_afterPreviousFind_searchesWholeTeam() {
        new FindCommand(preparePredicate("alice"), "find alice").execute(model);
        NameContainsKeywordsPredicate predicate = preparePredicate("Kunz Kurz Elle");
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(new FindCommand(predicate, "find Kunz Kurz Elle"), model,
                "3 players match the current command: find Kunz Kurz Elle", expectedModel);
        assertEquals(List.of(CARL, ELLE, FIONA), model.getFilteredPersonList());
    }

    @Test
    public void execute_emptyTeam_noPlayerFound() {
        Model emptyModel = new ModelManager();
        NameContainsKeywordsPredicate predicate = preparePredicate("wei");
        assertCommandSuccess(new FindCommand(predicate, "find wei"), emptyModel,
                "0 players match the current command: find wei", new ModelManager());
        assertEquals(List.of(), emptyModel.getFilteredPersonList());
    }

    @Test
    public void execute_listAfterFind_restoresWholeTeam() {
        new FindCommand(preparePredicate("alice"), "find alice").execute(model);
        new ListCommand().execute(model);
        assertEquals(getTypicalAddressBook().getPersonList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_deleteAfterFind_usesFilteredIndexAndRetainsFilter() throws Exception {
        new FindCommand(preparePredicate("Kurz Elle Kunz"), "find Kurz Elle Kunz").execute(model);
        new DeleteCommand(Index.fromOneBased(1)).execute(model);
        assertEquals(List.of(ELLE, FIONA), model.getFilteredPersonList());
        assertFalse(model.hasPerson(CARL));
        assertTrue(model.hasPerson(ALICE));
        new DeleteCommand(Index.fromOneBased(1)).execute(model);
        new DeleteCommand(Index.fromOneBased(1)).execute(model);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void toStringMethod() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("keyword"));
        FindCommand findCommand = new FindCommand(predicate, "find keyword");
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private NameContainsKeywordsPredicate preparePredicate(String userInput) {
        return new NameContainsKeywordsPredicate(List.of(userInput.split("\\s+")));
    }
}
