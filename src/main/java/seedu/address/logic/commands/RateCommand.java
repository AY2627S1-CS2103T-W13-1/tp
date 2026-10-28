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

/** Sets or removes the rating of a player in the displayed list. */
public class RateCommand extends Command {
    public static final String COMMAND_WORD = "rate";
    /** The word typed in place of a score to remove a player's rating. */
    public static final String UNRATED_KEYWORD = "unrated";
    public static final String MESSAGE_USAGE = "rate: Sets a player's rating, or removes it with \""
            + UNRATED_KEYWORD + "\".\n"
            + "Parameters: INDEX (positive integer) RATING (whole number from 0 to 10, or " + UNRATED_KEYWORD + ")\n"
            + "Example: rate 1 8";
    public static final String MESSAGE_INVALID_RATING = Rating.MESSAGE_CONSTRAINTS
            + " To remove a rating, use: rate INDEX " + UNRATED_KEYWORD;
    public static final String MESSAGE_SUCCESS = "Rated player: %1$s; Rating: %2$s/10";
    public static final String MESSAGE_UNRATE_SUCCESS = "Removed the rating of player: %1$s; Rating: Unrated";
    public static final String MESSAGE_ALREADY_UNRATED = "%1$s is already unrated.";

    private static final Logger logger = LogsCenter.getLogger(RateCommand.class);

    private final Index index;
    private final Rating rating;

    /** Constructs a command for a displayed index and a rating, which is {@code Rating.UNRATED} to remove it. */
    public RateCommand(Index index, Rating rating) {
        requireAllNonNull(index, rating);
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
        if (!rating.isRated() && !player.getRating().isRated()) {
            return new CommandResult(String.format(MESSAGE_ALREADY_UNRATED, player.getName()));
        }
        Person ratedPlayer = new Person(player.getName(), player.getPhone(), player.getSelectionStatus(), rating,
                player.getTags(), player.getRemark(), player.getInjuryStatus(), player.getJerseyNumber().orElse(null));
        model.setPerson(player, ratedPlayer);
        logger.fine("Updated rating at displayed index " + index.getOneBased());
        if (!rating.isRated()) {
            return new CommandResult(String.format(MESSAGE_UNRATE_SUCCESS, player.getName()));
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, player.getName(), rating));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof RateCommand otherCommand
                && index.equals(otherCommand.index) && rating.equals(otherCommand.rating);
    }
}
