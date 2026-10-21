package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class TagsContainAnyPredicateTest {
    @Test
    public void constructor_missingTags_rejectsInvalidState() {
        assertThrows(NullPointerException.class, () -> new TagsContainAnyPredicate(null));
        assertThrows(IllegalArgumentException.class, () -> new TagsContainAnyPredicate(Set.of()));
        Set<Tag> invalid = new HashSet<>();
        invalid.add(null);
        assertThrows(NullPointerException.class, () -> new TagsContainAnyPredicate(invalid));
    }

    @Test
    public void test_exactCaseInsensitiveLabelsWithOrSemantics() {
        TagsContainAnyPredicate predicate = new TagsContainAnyPredicate(
                Set.of(new Tag("striker"), new Tag("Trial Group 2")));
        assertTrue(predicate.test(new PersonBuilder().withTags("Striker").build()));
        assertTrue(predicate.test(new PersonBuilder().withTags("Captain", "Trial Group 2").build()));
        assertFalse(predicate.test(new PersonBuilder().withTags("Striker Reserve").build()));
        assertFalse(predicate.test(new PersonBuilder().withTags("Trial Group 20").build()));
        assertFalse(predicate.test(new PersonBuilder().withName("Striker").build()));
        assertFalse(predicate.test(new PersonBuilder().withTags("Captain").build()));
        assertThrows(NullPointerException.class, () -> predicate.test(null));
    }

    @Test
    public void constructor_copiesLabels() {
        Set<Tag> tags = new HashSet<>(Set.of(new Tag("Striker")));
        TagsContainAnyPredicate predicate = new TagsContainAnyPredicate(tags);
        tags.clear();
        assertTrue(predicate.test(new PersonBuilder().withTags("Striker").build()));
    }

    @Test
    public void equals_ignoresTagOrderAndCase() {
        TagsContainAnyPredicate predicate = new TagsContainAnyPredicate(Set.of(new Tag("Striker"), new Tag("Captain")));
        assertEquals(predicate, predicate);
        assertEquals(predicate, new TagsContainAnyPredicate(Set.of(new Tag("captain"), new Tag("striker"))));
        assertFalse(predicate.equals(new TagsContainAnyPredicate(Set.of(new Tag("Captain")))));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals("Striker"));
    }
}
