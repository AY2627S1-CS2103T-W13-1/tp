package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Checks whether a player's name looks like another player's name once spaces and punctuation are ignored,
 * e.g. {@code TanWei Ming} and {@code Tan Wei Ming}, so that commands can warn the coach about a likely
 * duplicate without rejecting a name that might genuinely be different.
 */
class SimilarNameChecker {

    private static final Logger logger = LogsCenter.getLogger(SimilarNameChecker.class);

    private SimilarNameChecker() {} // prevents instantiation

    /**
     * Returns a warning naming the other players in {@code model} whose names look like {@code player}'s,
     * or an empty string if there are none. {@code player} must already be in {@code model}.
     */
    static String getWarning(Model model, Person player) {
        requireNonNull(model);
        requireNonNull(player);
        assert model.hasPerson(player) : "The player should be in the team before checking for similar names";

        List<Person> similarPlayers = model.getAddressBook().getPersonList().stream()
                .filter(other -> !other.isSamePerson(player) && player.getName().isSimilarTo(other.getName()))
                .toList();
        if (similarPlayers.isEmpty()) {
            return "";
        }

        String similarNames = similarPlayers.stream()
                .map(other -> other.getName().fullName)
                .collect(Collectors.joining(", "));
        logger.info(player.getName() + " looks like existing players: " + similarNames);
        return String.format(Messages.MESSAGE_SIMILAR_NAME, similarNames);
    }
}
