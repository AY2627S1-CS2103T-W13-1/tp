package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.JavaFxTestUtil.runOnFxThread;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.scene.control.TextArea;

public class HelpPanelTest {
    @Test
    public void constructor_createsReadOnlySummaryAtStart() throws Exception {
        runOnFxThread(() -> {
            TextArea helpText = (TextArea) new HelpPanel().getRoot();

            assertEquals(HelpPanel.HELP_MESSAGE, helpText.getText());
            assertFalse(helpText.isEditable());
            assertTrue(helpText.isWrapText());
            assertEquals(0, helpText.getCaretPosition());
            assertEquals(0, helpText.getScrollTop());
        });
    }

    @Test
    public void constructor_summaryContainsMvpSyntaxAndGuidance() throws Exception {
        runOnFxThread(() -> {
            String summary = ((TextArea) new HelpPanel().getRoot()).getText();
            List<String> expectedContent = List.of("CouchCoach commands:", "add n/NAME p/PHONE",
                    "list - Show all players", "find KEYWORD...", "delete INDEX",
                    "help - Show this summary", "exit - Close CouchCoach",
                    "e.g. add n/Tan Wei Ming p/91234567", "e.g. find wei ming", "e.g. delete 3",
                    "Matches whole words only, ignores capitalisation", "Positions change after a delete.",
                    "Your data is saved automatically after every change.");

            for (String expected : expectedContent) {
                assertTrue(summary.contains(expected), "Missing help content: " + expected);
            }
        });
    }
}
