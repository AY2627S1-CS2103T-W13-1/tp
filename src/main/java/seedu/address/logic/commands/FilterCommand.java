package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.person.TagsContainAnyPredicate;

/**
 * Lists players that have any of the supplied custom tags.
 */
public class FilterCommand extends Command {
    /** Command word for filtering the roster by tag. */
    public static final String COMMAND_WORD = "filter";
    /** Syntax shown when no valid tag parameters are supplied. */
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists players with any of the given tags (exact labels, ignoring case).\n"
            + "Parameters: t/TAG [t/TAG]...\n"
            + "Example: " + COMMAND_WORD + " t/Striker t/Trial Group 2";
    /** Feedback for zero or multiple matching players. */
    public static final String MESSAGE_PLAYERS_LISTED = "%1$d players listed!";
    /** Feedback for one matching player. */
    public static final String MESSAGE_ONE_PLAYER_LISTED = "1 player listed!";

    private static final Logger logger = LogsCenter.getLogger(FilterCommand.class);
    private final TagsContainAnyPredicate predicate;

    /**
     * Creates a command that filters players using the supplied predicate.
     *
     * @param predicate The non-null tag matching rule.
     */
    public FilterCommand(TagsContainAnyPredicate predicate) {
        this.predicate = requireNonNull(predicate);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        int playerCount = model.getFilteredPersonList().size();
        assert playerCount <= model.getAddressBook().getPersonList().size() : "A filter cannot add players";
        logger.fine("Tag filter matched " + playerCount + " players.");
        String feedback = playerCount == 1 ? MESSAGE_ONE_PLAYER_LISTED
                : String.format(MESSAGE_PLAYERS_LISTED, playerCount);
        return new CommandResult(feedback);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof FilterCommand otherCommand)) {
            return false;
        }
        return predicate.equals(otherCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("predicate", predicate).toString();
    }
}
