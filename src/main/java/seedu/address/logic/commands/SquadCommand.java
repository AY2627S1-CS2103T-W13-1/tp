package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;
import seedu.address.model.person.HasSelectionStatusPredicate;
import seedu.address.model.person.SelectionStatus;

/**
 * Lists only the players marked as kept, i.e. the coach's squad.
 * The list stays filtered until another command, such as {@code list}, changes it.
 */
public class SquadCommand extends Command {

    /** Command word used to view the squad. */
    public static final String COMMAND_WORD = "squad";

    /** Feedback format for a squad of zero or multiple players. */
    public static final String MESSAGE_SQUAD_LISTED = "%1$d players in your squad.";
    /** Feedback for a squad of exactly one player. */
    public static final String MESSAGE_ONE_PLAYER_IN_SQUAD = "1 player in your squad.";
    /** Feedback when no player is kept yet, telling the coach how to build the squad. */
    public static final String MESSAGE_EMPTY_SQUAD = "No players in your squad yet. "
            + "Use 'keep INDEX' to keep a player, or 'list' to see all players.";

    private static final HasSelectionStatusPredicate IS_KEPT = new HasSelectionStatusPredicate(SelectionStatus.KEPT);

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(IS_KEPT);
        int squadSize = model.getFilteredPersonList().size();

        if (squadSize == 0) {
            return new CommandResult(MESSAGE_EMPTY_SQUAD);
        }
        if (squadSize == 1) {
            return new CommandResult(MESSAGE_ONE_PLAYER_IN_SQUAD);
        }
        return new CommandResult(String.format(MESSAGE_SQUAD_LISTED, squadSize));
    }
}
