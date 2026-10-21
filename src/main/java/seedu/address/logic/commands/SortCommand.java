package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/** Sorts the displayed players by rating without changing their saved order. */
public class SortCommand extends Command {
    public static final String COMMAND_WORD = "sort";
    public static final String MESSAGE_USAGE = "sort: Sorts displayed players by rating.\n"
            + "Parameters: rating [asc|desc]\nExample: sort rating desc";
    public static final String MESSAGE_SUCCESS = "Sorted players by rating (%s); unrated players last";

    private static final Logger logger = LogsCenter.getLogger(SortCommand.class);

    private final boolean ascending;

    public SortCommand(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        Comparator<Person> scoreComparator = Comparator.comparingInt(player -> {
            assert player.getRating().isRated();
            return player.getRating().getScore();
        });
        if (!ascending) {
            scoreComparator = scoreComparator.reversed();
        }
        Comparator<Person> ratedComparator = scoreComparator;
        model.setPersonComparator((first, second) -> {
            boolean firstRated = first.getRating().isRated();
            boolean secondRated = second.getRating().isRated();
            if (firstRated && secondRated) {
                return ratedComparator.compare(first, second);
            }
            return Boolean.compare(secondRated, firstRated);
        });
        logger.fine("Sorted displayed players by rating, ascending=" + ascending);
        return new CommandResult(String.format(MESSAGE_SUCCESS, ascending ? "ascending" : "descending"));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof SortCommand otherCommand && ascending == otherCommand.ascending;
    }
}
