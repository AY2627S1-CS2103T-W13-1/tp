package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
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

    public static final String MESSAGE_PLAYERS_LISTED = "%1$d players listed!";

    public static final String MESSAGE_ONE_PLAYER_LISTED = "1 player listed!";

    private static final Logger logger = LogsCenter.getLogger(FilterCommand.class);

    private final Optional<TagsContainAnyPredicate> tagPredicate;
    private final Optional<InjuryStatus> injuryStatus;

    /**
     * Creates a command that lists players with any tag accepted by {@code tagPredicate}.
     */
    public FilterCommand(TagsContainAnyPredicate tagPredicate) {
        this(Optional.of(requireNonNull(tagPredicate)), Optional.empty());
    }

    /**
     * Creates a command that lists players matching every condition given.
     *
     * @param tagPredicate The tags to match, if any.
     * @param injuryStatus The injury status to match, if any.
     * @throws IllegalArgumentException if neither condition is given.
     */
    public FilterCommand(Optional<TagsContainAnyPredicate> tagPredicate, Optional<InjuryStatus> injuryStatus) {
        requireAllNonNull(tagPredicate, injuryStatus);
        checkArgument(tagPredicate.isPresent() || injuryStatus.isPresent(), "A filter needs at least one condition.");
        this.tagPredicate = tagPredicate;
        this.injuryStatus = injuryStatus;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(this::matches);
        int playerCount = model.getFilteredPersonList().size();
        assert playerCount <= model.getAddressBook().getPersonList().size() : "A filter cannot add players";
        logger.fine("Filter matched " + playerCount + " players.");
        String feedback = playerCount == 1 ? MESSAGE_ONE_PLAYER_LISTED
                : String.format(MESSAGE_PLAYERS_LISTED, playerCount);
        return new CommandResult(feedback);
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
