package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * An immutable, optional note about a person. An empty value represents no remark.
 */
public class Remark {

    public final String value;

    /**
     * Creates a remark with the given non-null text, which may be empty.
     */
    public Remark(String value) {
        this.value = requireNonNull(value);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Remark otherRemark)) {
            return false;
        }

        return value.equals(otherRemark.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
