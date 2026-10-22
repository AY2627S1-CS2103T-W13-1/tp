package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_JERSEY_NUMBER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.JerseyNumber;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_TAG, PREFIX_JERSEY_NUMBER);

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_JERSEY_NUMBER);

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editPersonDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_PHONE).isPresent()) {
            editPersonDescriptor.setPhone(ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get()));
        }
        if (argMultimap.getValue(PREFIX_TAG).isPresent()) {
            List<String> labels = argMultimap.getAllValues(PREFIX_TAG);
            boolean isClearingTags = labels.size() == 1 && labels.getFirst().isEmpty();
            editPersonDescriptor.setTags(isClearingTags ? Set.of() : ParserUtil.parseTags(labels));
        }
        if (argMultimap.getValue(PREFIX_JERSEY_NUMBER).isPresent()) {
            editPersonDescriptor.setJerseyNumber(
                    parseJerseyNumberForEdit(argMultimap.getValue(PREFIX_JERSEY_NUMBER).get()));
        }

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editPersonDescriptor);
    }

    /**
     * Parses {@code jerseyNumber} into a {@code JerseyNumber}, or returns null if it is empty,
     * which means the player's jersey number is to be removed.
     *
     * @throws ParseException if the given {@code jerseyNumber} is not empty and is invalid.
     */
    private static JerseyNumber parseJerseyNumberForEdit(String jerseyNumber) throws ParseException {
        if (jerseyNumber.isBlank()) {
            return null;
        }
        return ParserUtil.parseJerseyNumber(jerseyNumber);
    }

}
