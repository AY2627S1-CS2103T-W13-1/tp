package seedu.address.logic;

import java.io.IOException;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.SaveCommand;
import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.commands.SquadCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    /** Feedback shown when a change was applied in memory but could not be written to the data file. */
    public static final String MESSAGE_SAVE_FAILURE = "Your change was applied but could not be saved to disk. "
            + "Fix the problem and type 'save' to retry before closing CouchCoach, or the change will be lost. "
            + "Do not repeat the original command.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     *
     * @param model The model used to execute commands.
     * @param storage The storage used to save player changes.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        addressBookParser = new AddressBookParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        CommandResult commandResult;
        Command command = addressBookParser.parseCommand(commandText);
        commandResult = command.execute(model);

        if (isReadOnly(command)) {
            return commandResult;
        }

        try {
            storage.saveAddressBook(model.getAddressBook());
        } catch (IOException ioe) {
            // The coach gets a plain-language message; the technical cause is kept in the log for diagnosis.
            logger.warning("Could not save data: " + ioe);
            // Execution succeeded: let the command box clear instead of inviting a repeated mutation.
            if (command instanceof SaveCommand) {
                return new CommandResult(SaveCommand.MESSAGE_FAILURE);
            }
            return new CommandResult(commandResult.getFeedbackToUser() + "\n" + MESSAGE_SAVE_FAILURE);
        }

        return commandResult;
    }

    /**
     * Returns true if {@code command} never changes player data, so no save is needed after it.
     * Skipping the save also stops these commands from overwriting a data file that could not be read.
     */
    private static boolean isReadOnly(Command command) {
        return command instanceof FindCommand
                || command instanceof FilterCommand
                || command instanceof SortCommand
                || command instanceof ListCommand
                || command instanceof HelpCommand
                || command instanceof SquadCommand
                || command instanceof ExitCommand;
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
