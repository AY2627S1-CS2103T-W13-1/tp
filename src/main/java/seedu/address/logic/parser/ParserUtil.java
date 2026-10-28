package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Messages;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.InjuryStatus;
import seedu.address.model.person.JerseyNumber;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer.";

    /** Matches input the user meant as an index, i.e. a whole number with an optional sign. */
    private static final String WHOLE_NUMBER_REGEX = "[+-]?\\d+";

    /**
     * Parses a collection of labels into distinct tags, retaining their first spelling and order.
     *
     * @param labels The labels to validate and normalize.
     * @return Distinct normalized tags.
     * @throws ParseException If any label is invalid, including a blank label.
     */
    public static Set<Tag> parseTags(Collection<String> labels) throws ParseException {
        requireNonNull(labels);
        Set<Tag> tags = new LinkedHashSet<>();
        for (String label : labels) {
            Optional<String> error = Tag.getValidationError(label);
            if (error.isPresent()) {
                throw new ParseException(error.get());
            }
            tags.add(new Tag(label));
        }
        return tags;
    }

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not a non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses {@code oneBasedIndex} into an {@code Index}, reporting a whole number that is not a valid index
     * (e.g. 0, -1, or too large to store) the same way as an index beyond the end of the displayed list.
     * A leading {@code +} is accepted, as {@code +1} clearly means the first player.
     * Input that is not a whole number at all is a format error, so it is reported with {@code messageUsage}.
     *
     * @param oneBasedIndex The index typed by the user.
     * @param messageUsage The usage message of the command being parsed.
     * @throws ParseException if {@code oneBasedIndex} is not a positive whole number.
     */
    public static Index parseIndex(String oneBasedIndex, String messageUsage) throws ParseException {
        requireNonNull(messageUsage);
        String trimmedIndex = oneBasedIndex.trim();
        if (!trimmedIndex.matches(WHOLE_NUMBER_REGEX)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, messageUsage));
        }
        String unsignedIndex = trimmedIndex.startsWith("+") ? trimmedIndex.substring(1) : trimmedIndex;
        if (!StringUtil.isNonZeroUnsignedInteger(unsignedIndex)) {
            throw new ParseException(MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(unsignedIndex));
    }

    /**
     * Throws a {@code ParseException} if {@code argMultimap} contains a remark or injury status, which
     * {@code commandWord} cannot set. Without this check, such a detail would be read as part of the
     * value before it, and the error would wrongly blame that value.
     */
    public static void requireNoRemarkOrInjury(ArgumentMultimap argMultimap, String commandWord)
            throws ParseException {
        boolean hasRemark = argMultimap.getValue(CliSyntax.PREFIX_REMARK).isPresent();
        boolean hasInjury = argMultimap.getValue(CliSyntax.PREFIX_INJURY).isPresent();
        if (hasRemark || hasInjury) {
            throw new ParseException(String.format(Messages.MESSAGE_DETAIL_NEEDS_OWN_COMMAND, commandWord));
        }
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed, and repeated internal whitespaces will be
     * collapsed into a single space.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String normalizedName = Name.normalize(name);
        if (!Name.hasValidCharacters(normalizedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        if (!Name.hasValidLength(normalizedName)) {
            throw new ParseException(Name.MESSAGE_LENGTH_CONSTRAINTS);
        }
        return new Name(normalizedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * All spaces and hyphens will be removed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String normalizedPhone = Phone.normalize(phone);
        if (!Phone.isValidPhone(normalizedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(normalizedPhone);
    }

    /**
     * Parses an injury status, allowing surrounding whitespace and mixed case.
     *
     * @throws ParseException if the value is neither injured nor fit.
     */
    public static InjuryStatus parseInjuryStatus(String value) throws ParseException {
        requireNonNull(value);
        try {
            return InjuryStatus.fromString(value.trim());
        } catch (IllegalArgumentException e) {
            throw new ParseException(InjuryStatus.MESSAGE_CONSTRAINTS, e);
        }
    }

    /**
     * Parses a {@code String jerseyNumber} into a {@code JerseyNumber}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code jerseyNumber} is invalid.
     */
    public static JerseyNumber parseJerseyNumber(String jerseyNumber) throws ParseException {
        requireNonNull(jerseyNumber);
        String trimmedJerseyNumber = jerseyNumber.trim();
        if (!JerseyNumber.isValidJerseyNumber(trimmedJerseyNumber)) {
            throw new ParseException(JerseyNumber.MESSAGE_CONSTRAINTS);
        }
        return new JerseyNumber(trimmedJerseyNumber);
    }
}
