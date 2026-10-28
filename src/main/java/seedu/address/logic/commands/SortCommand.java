package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Comparator;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.model.Model;
import seedu.address.model.person.HasSelectionStatusPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.SelectionStatus;

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

    private static final Logger logger = LogsCenter.getLogger(SortCommand.class);

    private final boolean ascending;
    private final Scope scope;

    /** Creates a sort that preserves the current view. */
    public SortCommand(boolean ascending) {
        this(ascending, Scope.CURRENT);
    }

    /** Creates a rating sort with the requested direction and view scope. */
    public SortCommand(boolean ascending, Scope scope) {
        this.ascending = ascending;
        this.scope = requireNonNull(scope);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        updateScope(model);
        model.setPersonComparator(createRatingComparator());
        logger.fine("Sorted displayed players by rating, ascending=" + ascending + ", scope=" + scope);
        String direction = ascending ? "ascending" : "descending";
        String feedback = String.format(MESSAGE_SUCCESS, direction);
        if (scope != Scope.CURRENT) {
            String players = scope == Scope.ALL ? "all players" : "kept players";
            feedback = String.format(MESSAGE_SCOPED_SUCCESS, players, direction);
        }
        return new CommandResult(feedback);
    }

    /** Selects a complete roster or squad when explicitly requested. */
    private void updateScope(Model model) {
        switch (scope) {
            case ALL -> model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
            case SQUAD -> model.updateFilteredPersonList(new HasSelectionStatusPredicate(SelectionStatus.KEPT));
            case CURRENT -> { } // Retain the current name, tag, or selection filter.
            default -> throw new AssertionError("Unsupported sort scope: " + scope);
        }
    }

    /** Returns an ordering with unrated players last in either direction. */
    private Comparator<Person> createRatingComparator() {
        Comparator<Person> scoreComparator = Comparator.comparingInt(player -> {
            assert player.getRating().isRated();
            return player.getRating().getScore();
        });
        if (!ascending) {
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
        return other == this || other instanceof SortCommand otherCommand
                && ascending == otherCommand.ascending && scope == otherCommand.scope;
    }
}
