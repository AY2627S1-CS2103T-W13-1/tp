package seedu.address.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_DATA_FILE_UNREADABLE = "The data file is not in a readable format "
            + "and will be replaced by an empty team when you make your next change.";
    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command. Type 'help' to see the list of commands.";
    public static final String MESSAGE_SIMILAR_NAME =
            "\nWarning: this name looks like %1$s. Check that this is not the same player.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The player index provided is invalid.";
    public static final String MESSAGE_DUPLICATE_JERSEY_NUMBER =
            "\nWarning: jersey number %1$s is also used by %2$s.";
    public static final String MESSAGE_DUPLICATE_FIELDS = "Each of these can be given only once: %1$s\n"
            + "Remove the repeated one. Text such as \" r/\" inside a value also counts as a repeat.";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return String.format(MESSAGE_DUPLICATE_FIELDS, String.join(" ", duplicateFields));
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
