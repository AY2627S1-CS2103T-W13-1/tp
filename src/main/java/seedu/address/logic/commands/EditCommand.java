package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_JERSEY_NUMBER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.JerseyNumber;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.SelectionStatus;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing player in the team.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    /** Syntax guidance for the name, phone, and jersey editing command. */
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the player identified "
            + "by the index number used in the displayed player list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_JERSEY_NUMBER + "JERSEY]\n"
            + "An empty " + PREFIX_JERSEY_NUMBER + " removes the jersey number.\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited player: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This player already exists in the team.";
    public static final String MESSAGE_NO_CHANGE = "No changes made: %1$s already has these details.";
    public static final String MESSAGE_JERSEY_NUMBER_REMOVED = " (jersey number removed)";
    public static final String MESSAGE_TAGS_CLEARED = " (tags cleared)";

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * Creates a command with a defensive copy of the requested edits.
     *
     * @param index The index in the displayed player list.
     * @param editPersonDescriptor The player details to replace.
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        if (isUnchanged(personToEdit, editedPerson)) {
            return new CommandResult(String.format(MESSAGE_NO_CHANGE, personToEdit.getName()));
        }

        // The displayed list is not reset, so the next index still refers to the same search results.
        // An edited player who no longer matches the search drops out of the displayed list.
        model.setPerson(personToEdit, editedPerson);
        // Only warn when this edit sets the jersey number, so that editing other details does not repeat it
        String warning = editPersonDescriptor.isJerseyNumberEdited()
                ? DuplicateJerseyNumberChecker.getWarning(model, editedPerson)
                : "";
        // Likewise, only check the name when this edit actually changes it, not merely when n/ is given
        if (!editedPerson.getName().equals(personToEdit.getName())) {
            warning += SimilarNameChecker.getWarning(model, editedPerson);
        }
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson))
                + describeRemovals(personToEdit, editedPerson) + warning);
    }

    /**
     * Returns true if {@code editedPerson} looks exactly like {@code personToEdit}. {@link Person#equals} is
     * not enough on its own, as tags are equal regardless of capitalisation, so it would treat changing a tag
     * from {@code STRIKER} to {@code Striker} as no change, although the stored spelling changes.
     */
    private static boolean isUnchanged(Person personToEdit, Person editedPerson) {
        return editedPerson.equals(personToEdit)
                && getTagSpellings(editedPerson).equals(getTagSpellings(personToEdit));
    }

    private static Set<String> getTagSpellings(Person person) {
        return person.getTags().stream().map(Tag::toString).collect(Collectors.toSet());
    }

    /**
     * Returns notes on the details this edit removed, which the success message would otherwise not
     * mention because {@link Messages#format(Person)} leaves out empty details.
     */
    private static String describeRemovals(Person personToEdit, Person editedPerson) {
        boolean isJerseyNumberRemoved = personToEdit.getJerseyNumber().isPresent()
                && editedPerson.getJerseyNumber().isEmpty();
        boolean areTagsCleared = !personToEdit.getTags().isEmpty() && editedPerson.getTags().isEmpty();
        return (isJerseyNumberRemoved ? MESSAGE_JERSEY_NUMBER_REMOVED : "")
                + (areTagsCleared ? MESSAGE_TAGS_CLEARED : "");
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        // edit cannot change the selection status (keep, cut and pending do), so keep the existing one
        SelectionStatus unchangedStatus = personToEdit.getSelectionStatus();

        Set<Tag> updatedTags = editPersonDescriptor.getTags().orElse(personToEdit.getTags());
        JerseyNumber updatedJerseyNumber = editPersonDescriptor.isJerseyNumberEdited()
                ? editPersonDescriptor.getJerseyNumber().orElse(null)
                : personToEdit.getJerseyNumber().orElse(null);
        return new Person(updatedName, updatedPhone, unchangedStatus, personToEdit.getRating(), updatedTags,
                personToEdit.getRemark(), personToEdit.getInjuryStatus(), updatedJerseyNumber);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand otherEditCommand)) {
            return false;
        }

        return index.equals(otherEditCommand.index)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Set<Tag> tags;
        private JerseyNumber jerseyNumber;
        // Needed because a null jerseyNumber may mean either "not edited" or "remove the jersey number"
        private boolean isJerseyNumberEdited;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setTags(toCopy.tags);
            if (toCopy.isJerseyNumberEdited) {
                setJerseyNumber(toCopy.jerseyNumber);
            }
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, tags) || isJerseyNumberEdited;
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        /**
         * Copies the requested tags; null means preserve tags, and an empty set means clear them.
         *
         * @param tags The replacement tags, or null for no tag edit.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = tags == null ? null : Set.copyOf(tags);
        }

        /**
         * Returns an unmodifiable replacement set if tags are being edited.
         *
         * @return Replacement tags, or an empty optional when tags are unchanged.
         */
        public Optional<Set<Tag>> getTags() {
            return Optional.ofNullable(tags);
        }

        /**
         * Sets the jersey number to change to. A null {@code jerseyNumber} removes the player's jersey number.
         */
        public void setJerseyNumber(JerseyNumber jerseyNumber) {
            this.jerseyNumber = jerseyNumber;
            isJerseyNumberEdited = true;
        }

        /**
         * Returns the jersey number to change to, which is empty if the jersey number is to be removed
         * or is not edited. Use {@link #isJerseyNumberEdited()} to tell the two apart.
         */
        public Optional<JerseyNumber> getJerseyNumber() {
            return Optional.ofNullable(jerseyNumber);
        }

        public boolean isJerseyNumberEdited() {
            return isJerseyNumberEdited;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor otherEditPersonDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone)
                    && Objects.equals(tags, otherEditPersonDescriptor.tags)
                    && Objects.equals(jerseyNumber, otherEditPersonDescriptor.jerseyNumber)
                    && isJerseyNumberEdited == otherEditPersonDescriptor.isJerseyNumberEdited;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("tags", tags)
                    .add("jerseyNumber", jerseyNumber)
                    .add("isJerseyNumberEdited", isJerseyNumberEdited)
                    .toString();
        }
    }
}
