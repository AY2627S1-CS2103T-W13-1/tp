package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.InjuryStatus;
import seedu.address.model.person.Person;
import seedu.address.model.person.TagsContainAnyPredicate;

/**
 * Lists players who have any of the requested tags, the requested injury status, or both.
 * When both are given, a player must match both, e.g. injured strikers.
 */
public class FilterCommand extends Command {

    public static final String COMMAND_WORD = "filter";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists players with any of the given tags (exact labels, ignoring case), "
            + "the given injury status, or both.\n"
            + "Parameters: [t/TAG]... [i/STATUS] (at least one; STATUS is injured or fit)\n"
            + "Examples: " + COMMAND_WORD + " t/Striker t/Trial Group 2, " + COMMAND_WORD + " i/injured, "
            + COMMAND_WORD + " t/Striker i/fit";

    private static final Logger logger = LogsCenter.getLogger(FilterCommand.class);

    private final Optional<TagsContainAnyPredicate> tagPredicate;
    private final Optional<InjuryStatus> injuryStatus;
    private final String commandText;

    /**
     * Creates a command that lists players with any tag accepted by {@code tagPredicate}.
     *
     * @param tagPredicate The tags to match.
     * @param commandText The filter command to identify in feedback.
     */
    public FilterCommand(TagsContainAnyPredicate tagPredicate, String commandText) {
        this(Optional.of(requireNonNull(tagPredicate)), Optional.empty(), commandText);
    }

    /**
     * Creates a command that lists players matching every condition given.
     *
     * @param tagPredicate The tags to match, if any.
     * @param injuryStatus The injury status to match, if any.
     * @param commandText The filter command to identify in feedback.
     * @throws IllegalArgumentException if neither condition is given.
     */
    public FilterCommand(Optional<TagsContainAnyPredicate> tagPredicate, Optional<InjuryStatus> injuryStatus,
            String commandText) {
        requireAllNonNull(tagPredicate, injuryStatus, commandText);
        checkArgument(tagPredicate.isPresent() || injuryStatus.isPresent(), "A filter needs at least one condition.");
        this.tagPredicate = tagPredicate;
        this.injuryStatus = injuryStatus;
        this.commandText = commandText;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(this::matches, commandText);
        int playerCount = model.getFilteredPersonList().size();
        assert playerCount <= model.getAddressBook().getPersonList().size() : "A filter cannot add players";
        logger.fine("Filter matched " + playerCount + " players.");
        return new CommandResult(Messages.formatCurrentListSummary(model));
    }

    /**
     * Returns true if {@code player} meets every condition of this filter.
     */
    private boolean matches(Person player) {
        boolean hasRequestedTag = tagPredicate.map(predicate -> predicate.test(player)).orElse(true);
        boolean hasRequestedInjuryStatus = injuryStatus.map(status -> player.getInjuryStatus() == status)
                .orElse(true);
        return hasRequestedTag && hasRequestedInjuryStatus;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FilterCommand otherCommand)) {
            return false;
        }

        return tagPredicate.equals(otherCommand.tagPredicate) && injuryStatus.equals(otherCommand.injuryStatus);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", tagPredicate.orElse(null))
                .add("injuryStatus", injuryStatus.orElse(null))
                .toString();
    }
}
