package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.person.NameContainsKeywordsPredicate;

/**
 * Finds and lists players whose names match any keyword as a whole word, ignoring case.
 */
public class FindCommand extends Command {

    /** Command word used to find players by name. */
    public static final String COMMAND_WORD = "find";

    /** Usage displayed when no search keyword is supplied. */
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds players whose names contain any of the given keywords.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " wei ming";

    /** Feedback format for zero or multiple matches. */
    public static final String MESSAGE_PLAYERS_LISTED_OVERVIEW = "%1$d players listed!";
    /** Feedback for exactly one match. */
    public static final String MESSAGE_ONE_PLAYER_LISTED = "1 player listed!";

    private final NameContainsKeywordsPredicate predicate;

    /**
     * Creates a command that lists players matching {@code predicate}.
     *
     * @param predicate The name-matching predicate.
     */
    public FindCommand(NameContainsKeywordsPredicate predicate) {
        this.predicate = requireNonNull(predicate);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        int playerCount = model.getFilteredPersonList().size();
        String feedback = playerCount == 1 ? MESSAGE_ONE_PLAYER_LISTED
                : String.format(MESSAGE_PLAYERS_LISTED_OVERVIEW, playerCount);
        return new CommandResult(feedback);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand otherFindCommand)) {
            return false;
        }

        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
