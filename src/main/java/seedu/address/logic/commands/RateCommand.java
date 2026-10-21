package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Rating;

/** Sets the rating of a player in the displayed list. */
public class RateCommand extends Command {
    public static final String COMMAND_WORD = "rate";
    public static final String MESSAGE_USAGE = "rate: Sets a player's rating.\n"
            + "Parameters: INDEX (positive integer) RATING (whole number from 0 to 10)\nExample: rate 1 8";
    public static final String MESSAGE_SUCCESS = "Rated player: %1$s; Rating: %2$s/10";

    private static final Logger logger = LogsCenter.getLogger(RateCommand.class);

    private final Index index;
    private final Rating rating;

    /** Constructs a command for a displayed index and a rated value. */
    public RateCommand(Index index, Rating rating) {
        requireAllNonNull(index, rating);
        if (!rating.isRated()) {
            throw new IllegalArgumentException(Rating.MESSAGE_CONSTRAINTS);
        }
        this.index = index;
        this.rating = rating;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> displayed = model.getFilteredPersonList();
        if (index.getZeroBased() >= displayed.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        Person player = displayed.get(index.getZeroBased());
        assert rating.isRated();
        Person ratedPlayer = new Person(player.getName(), player.getPhone(), player.getSelectionStatus(), rating,
                player.getTags());
        model.setPerson(player, ratedPlayer);
        logger.fine("Updated rating at displayed index " + index.getOneBased());
        return new CommandResult(String.format(MESSAGE_SUCCESS, player.getName(), rating));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof RateCommand otherCommand
                && index.equals(otherCommand.index) && rating.equals(otherCommand.rating);
    }
}
