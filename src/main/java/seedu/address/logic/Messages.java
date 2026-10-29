package seedu.address.logic;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.commands.SquadCommand;
import seedu.address.logic.parser.Prefix;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_DATA_FILE_UNREADABLE = "The data file is not in a readable format. "
            + "CouchCoach has started with an empty team. Your next change or 'save' will replace the file "
            + "with the current team. Back up the file before continuing.";
    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command. Type 'help' to see the list of commands.";
    public static final String MESSAGE_SIMILAR_NAME =
            "\nWarning: this name looks like %1$s. Check that this is not the same player.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The player index provided is invalid.";
    /** Feedback identifying the command selecting the displayed players. */
    public static final String MESSAGE_PLAYERS_MATCHING = "%1$d players match the current command: %2$s";
    /** Feedback for exactly one matching player. */
    public static final String MESSAGE_ONE_PLAYER_MATCHING = "1 player matches the current command: %1$s";
    /** Shown when {@code add} or {@code edit} is given a detail that only its own command can set. */
    public static final String MESSAGE_DETAIL_NEEDS_OWN_COMMAND = "%1$s cannot set a remark or injury status. "
            + "Use \"remark INDEX r/REMARK\" or \"injury INDEX i/STATUS\" instead.";
    public static final String MESSAGE_DUPLICATE_JERSEY_NUMBER =
            "\nWarning: jersey number %1$s is also used by %2$s.";
    public static final String MESSAGE_DUPLICATE_FIELDS = "Each of these can be given only once: %1$s\n"
            + "Remove the repeated one. Text such as \" %2$s\" inside a value also counts as a repeat.";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        List<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).distinct().sorted().toList();

        return String.format(MESSAGE_DUPLICATE_FIELDS, String.join(" ", duplicateFields), duplicateFields.get(0));
    }

    /**
     * Returns feedback for the current view using its refreshed displayed count.
     * Returns an empty string when no command describes the view.
     *
     * @param model The model containing the displayed list and its selecting command.
     * @return The current count and view description, or an empty string.
     */
    public static String formatCurrentListSummary(Model model) {
        int count = model.getFilteredPersonList().size();
        return model.getCurrentListCommand().map(commandText -> {
            if (commandText.equals(SquadCommand.COMMAND_WORD)) {
                return count == 1 ? "1 player in your squad" : count + " players in your squad";
            }
            return count == 1 ? String.format(MESSAGE_ONE_PLAYER_MATCHING, commandText)
                    : String.format(MESSAGE_PLAYERS_MATCHING, count, commandText);
        }).orElse("");
    }

    /**
     * Appends the current view's count on a new line, if the view has a command description.
     *
     * @param feedback The existing command feedback, including any warnings.
     * @param model The model containing the refreshed displayed list.
     * @return The feedback with a view summary when one is available.
     */
    public static String appendCurrentListSummary(String feedback, Model model) {
        String summary = formatCurrentListSummary(model);
        return summary.isEmpty() ? feedback : feedback + "\n" + summary;
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Phone: ")
                .append(person.getPhone());
        person.getJerseyNumber().ifPresent(jerseyNumber -> builder.append("; Jersey: ").append(jerseyNumber));
        if (!person.getTags().isEmpty()) {
            builder.append("; Tags: ").append(person.getTags().stream()
                    .map(Object::toString).sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.joining(", ")));
        }
        return builder.toString();
    }

}
