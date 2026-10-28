package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.SetStatusCommand;
import seedu.address.logic.commands.SquadCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.SelectionStatus;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_tabSeparatedPrefixes_matchesSpaceSeparatedCommands() throws Exception {
        assertEquals(parser.parseCommand("add n/Amy p/91234567 t/Striker"),
                parser.parseCommand("add\tn/Amy\tp/91234567\tt/Striker"));
        assertEquals(parser.parseCommand("edit 1 p/98765432"),
                parser.parseCommand("edit 1\tp/98765432"));
        assertEquals(parser.parseCommand("filter t/Striker t/Captain"),
                parser.parseCommand("filter\tt/Striker \tt/Captain"));
    }

    @Test
    public void parseCommand_tabSeparatedDuplicatePrefix_rejected() {
        assertThrows(ParseException.class, () -> parser.parseCommand("add\tn/Amy\tn/Bob\tp/91234567"));
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand("  clear \t ") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_clearWithArguments_throwsParseException() {
        String usage = "clear: Deletes all players from the team.\n"
                + "Parameters: none. Use 'clear' on its own.\n"
                + "Warning: This cannot be undone and does not ask for confirmation.";
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage);
        for (String input : List.of("clear 1", "clear everything", "clear n/Amy", "clear /")) {
            assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand(input));
        }
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = List.of("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
        assertTrue(parser.parseCommand("  help  ") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " all") instanceof ListCommand);
    }

    @Test
    public void parseCommand_keep() throws Exception {
        SetStatusCommand command = (SetStatusCommand) parser.parseCommand(
                SetStatusCommand.COMMAND_WORD_KEEP + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new SetStatusCommand(INDEX_FIRST_PERSON, SelectionStatus.KEPT), command);
    }

    @Test
    public void parseCommand_cut() throws Exception {
        SetStatusCommand command = (SetStatusCommand) parser.parseCommand(
                SetStatusCommand.COMMAND_WORD_CUT + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new SetStatusCommand(INDEX_FIRST_PERSON, SelectionStatus.CUT), command);
    }

    @Test
    public void parseCommand_pending() throws Exception {
        SetStatusCommand command = (SetStatusCommand) parser.parseCommand(
                SetStatusCommand.COMMAND_WORD_PENDING + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new SetStatusCommand(INDEX_FIRST_PERSON, SelectionStatus.PENDING), command);
    }

    @Test
    public void parseCommand_squad() throws Exception {
        assertTrue(parser.parseCommand(SquadCommand.COMMAND_WORD) instanceof SquadCommand);
        assertTrue(parser.parseCommand(SquadCommand.COMMAND_WORD + " 3") instanceof SquadCommand);
    }

    @Test
    public void parseCommand_squadWrongCase_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("Squad"));
    }

    @Test
    public void parseCommand_listWrongCaseOrMisspelling_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("List"));
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("lst"));
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand(""));
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand(" \t "));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        String expectedMessage = "Unknown command. Type 'help' to see the list of commands.";
        for (String input : List.of("unknownCommand", "addd", "Add", "remove 2", "Help")) {
            assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand(input));
        }
    }
}
