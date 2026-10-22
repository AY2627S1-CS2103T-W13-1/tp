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
 * Checks whether a player's jersey number is also used by other players in the team,
 * so that commands can warn the coach without rejecting the change.
 */
class DuplicateJerseyNumberChecker {

    private static final Logger logger = LogsCenter.getLogger(DuplicateJerseyNumberChecker.class);

    private DuplicateJerseyNumberChecker() {} // prevents instantiation

    /**
     * Returns a warning naming the other players in {@code model} who share {@code player}'s jersey number,
     * or an empty string if no other player shares it or {@code player} has no jersey number.
     * {@code player} must already be in {@code model}.
     */
    static String getWarning(Model model, Person player) {
        requireNonNull(model);
        requireNonNull(player);
        assert model.hasPerson(player) : "The player should be in the team before checking for a shared number";

        List<Person> otherPlayers = model.getAddressBook().getPersonList().stream()
                .filter(other -> !other.isSamePerson(player) && player.hasSameJerseyNumber(other))
                .toList();
        if (otherPlayers.isEmpty()) {
            return "";
        }

        String otherNames = otherPlayers.stream()
                .map(other -> other.getName().fullName)
                .collect(Collectors.joining(", "));
        logger.info("Jersey number " + player.getJerseyNumber().get() + " is shared by " + player.getName()
                + " and " + otherNames);
        return String.format(Messages.MESSAGE_DUPLICATE_JERSEY_NUMBER, player.getJerseyNumber().get(), otherNames);
    }
}
