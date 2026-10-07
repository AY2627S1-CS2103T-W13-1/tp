package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.JavaFxTestUtil.runOnFxThread;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

public class HelpPanelTest {
    @Test
    public void constructor_createsScrollableSummaryAtStart() throws Exception {
        runOnFxThread(() -> {
            ScrollPane help = (ScrollPane) new HelpPanel().getRoot();

            assertTrue(help.isFitToWidth());
            assertEquals(ScrollPane.ScrollBarPolicy.NEVER, help.getHbarPolicy());
            assertEquals(ScrollPane.ScrollBarPolicy.AS_NEEDED, help.getVbarPolicy());
            assertEquals(0, help.getVvalue());
        });
    }

    @Test
    public void constructor_summaryContainsMvpSyntaxAndGuidance() throws Exception {
        runOnFxThread(() -> {
            ScrollPane help = (ScrollPane) new HelpPanel().getRoot();
            Map<String, String> expectedText = Map.ofEntries(
                    Map.entry("helpHeading", "CouchCoach commands:"),
                    Map.entry("addSyntax", "add n/NAME p/PHONE"),
                    Map.entry("addDescription", "Add a player\ne.g. add n/Tan Wei Ming p/91234567"),
                    Map.entry("listSyntax", "list"), Map.entry("listDescription", "Show all players"),
                    Map.entry("findSyntax", "find KEYWORD..."),
                    Map.entry("findDescription", "Show players whose names contain a keyword\n"
                            + "Matches whole words only, ignores capitalisation\ne.g. find wei ming"),
                    Map.entry("deleteSyntax", "delete INDEX"),
                    Map.entry("deleteDescription", "Delete the player at that position in the list\n"
                            + "shown right now. Positions change after a delete.\ne.g. delete 3"),
                    Map.entry("helpSyntax", "help"), Map.entry("helpDescription", "Show this summary"),
                    Map.entry("exitSyntax", "exit"), Map.entry("exitDescription", "Close CouchCoach"),
                    Map.entry("helpFooter", "Your data is saved automatically after every change."));

            expectedText.forEach((id, text) -> {
                Label label = (Label) help.getContent().lookup("#" + id);
                assertEquals(text, label.getText());
            });
            for (String command : List.of("add", "list", "find", "delete", "help", "exit")) {
                assertTrue(((Label) help.getContent().lookup("#" + command + "Description")).isWrapText());
            }
        });
    }
}
