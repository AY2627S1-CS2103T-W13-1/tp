package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.commands.SortCommand.Direction;
import seedu.address.logic.commands.SortCommand.Scope;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses rating sorts with optional scope and direction in either order and any letter case. */
public class SortCommandParser implements Parser<SortCommand> {
    private static final Set<String> VALID_OPTIONS = Set.of("all", "squad", "asc", "desc");

    @Override
    public SortCommand parse(String args) throws ParseException {
        String[] parts = args.trim().toLowerCase(Locale.ROOT).split("\\s+");
        boolean hasExtraWords = parts.length > 3;
        boolean hasInvalidField = !"rating".equals(parts[0]);
        if (hasExtraWords || hasInvalidField) {
            throw invalidFormat();
        }
        List<String> options = List.of(parts).subList(1, parts.length);
        boolean hasUnknownOptions = !VALID_OPTIONS.containsAll(options);
        boolean hasRepeatedOptions = new HashSet<>(options).size() != options.size();
        boolean hasConflictingScopes = options.contains("all") && options.contains("squad");
        boolean hasConflictingDirections = options.contains("asc") && options.contains("desc");
        if (hasUnknownOptions || hasRepeatedOptions || hasConflictingScopes || hasConflictingDirections) {
            throw invalidFormat();
        }
        Direction direction = options.contains("asc") ? Direction.ASCENDING : Direction.DESCENDING;
        return new SortCommand(direction, parseScope(options));
    }

    private Scope parseScope(List<String> options) {
        if (options.contains("all")) {
            return Scope.ALL;
        }
        if (options.contains("squad")) {
            return Scope.SQUAD;
        }
        return Scope.CURRENT;
    }

    private ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
    }
}
