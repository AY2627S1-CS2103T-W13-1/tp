package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.model.person.HasSelectionStatusPredicate.IS_KEPT;

import java.util.Comparator;
import java.util.Locale;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/** Sorts the displayed players by rating without changing their saved order. */
public class SortCommand extends Command {
    /** Command word for rating sorting. */
    public static final String COMMAND_WORD = "sort";
    /** Syntax guidance for current-view and explicit-scope sorting. */
    public static final String MESSAGE_USAGE = "sort: Sorts displayed players by rating.\n"
            + "Parameters: rating [all|squad] [asc|desc]\nExample: sort rating squad desc";
    /** Feedback for sorting the existing view. */
    public static final String MESSAGE_SUCCESS = "Sorted players by rating (%s); unrated players last";
    /** Feedback that identifies an explicitly selected scope. */
    public static final String MESSAGE_SCOPED_SUCCESS = "Sorted %s by rating (%s); unrated players last";

    /** The players to display before applying the rating order. */
    public enum Scope {
        /** Preserve the current filter. */
        CURRENT,
        /** Show the entire roster. */
        ALL,
        /** Show every kept player. */
        SQUAD
    }

    /** The order of rated players; unrated players always appear last. */
    public enum Direction {
        /** Order ratings from lowest to highest. */
        ASCENDING,
        /** Order ratings from highest to lowest. */
        DESCENDING
    }

    private static final Logger logger = LogsCenter.getLogger(SortCommand.class);

    private final Direction direction;
    private final Scope scope;

    /** Creates a sort that preserves the current view. */
    public SortCommand(Direction direction) {
        this(direction, Scope.CURRENT);
    }

    /** Creates a rating sort with the requested direction and view scope. */
    public SortCommand(Direction direction, Scope scope) {
        this.direction = requireNonNull(direction);
        this.scope = requireNonNull(scope);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        updateScope(model);
        model.setPersonComparator(createRatingComparator());
        logger.fine("Sorted displayed players by rating, direction=" + direction + ", scope=" + scope);
        String directionLabel = direction.name().toLowerCase(Locale.ROOT);
        String feedback = String.format(MESSAGE_SUCCESS, directionLabel);
        if (scope != Scope.CURRENT) {
            String players = scope == Scope.ALL ? "all players" : "kept players";
            feedback = String.format(MESSAGE_SCOPED_SUCCESS, players, directionLabel);
        }
        return new CommandResult(feedback);
    }

    /** Selects a complete roster or squad when explicitly requested. */
    private void updateScope(Model model) {
        if (scope == Scope.CURRENT) {
            return;
        }
        if (scope == Scope.SQUAD) {
            model.updateFilteredPersonList(IS_KEPT, SquadCommand.COMMAND_WORD);
        } else {
            model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        }
    }

    /** Returns an ordering with unrated players last in either direction. */
    private Comparator<Person> createRatingComparator() {
        Comparator<Person> scoreComparator = Comparator.comparingInt(player -> {
            assert player.getRating().isRated();
            return player.getRating().getScore();
        });
        if (direction == Direction.DESCENDING) {
            scoreComparator = scoreComparator.reversed();
        }
        Comparator<Person> ratedComparator = scoreComparator;
        return (first, second) -> {
            boolean firstRated = first.getRating().isRated();
            boolean secondRated = second.getRating().isRated();
            if (firstRated && secondRated) {
                return ratedComparator.compare(first, second);
            }
            return Boolean.compare(secondRated, firstRated);
        };
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SortCommand otherCommand)) {
            return false;
        }
        return direction == otherCommand.direction && scope == otherCommand.scope;
    }
}
