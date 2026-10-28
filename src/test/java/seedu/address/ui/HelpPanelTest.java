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
                    Map.entry("helpHint", "Type 'list' to see your players again."),
                    Map.entry("addSyntax", "add n/NAME p/PHONE\n[j/JERSEY] [t/TAG]..."),
                    Map.entry("addDescription", "Add a player with an optional jersey number and tags\n"
                            + "e.g. add n/Tan Wei Ming p/91234567 j/10 t/Striker"),
                    Map.entry("editSyntax", "edit INDEX [n/NAME]\n[p/PHONE] [j/JERSEY]"),
                    Map.entry("editDescription", "Edit name, phone, or jersey; other details are kept\n"
                            + "An empty j/ removes the jersey number\n"
                            + "Use tag to update tags. e.g. edit 1 j/23"),
                    Map.entry("tagSyntax", "tag INDEX t/TAG\n[t/TAG]..."),
                    Map.entry("tagDescription", "Add tags; a single empty t/ clears all tags\n"
                            + "e.g. tag 1 t/Striker t/Captain"),
                    Map.entry("filterSyntax", "filter [t/TAG]...\n[i/STATUS]"),
                    Map.entry("filterDescription", "Show players with any requested tag, the injury\n"
                            + "status, or both. Tags match complete labels, ignoring\n"
                            + "capitalisation. e.g. filter t/Striker i/injured"),
                    Map.entry("listSyntax", "list"), Map.entry("listDescription", "Show all players"),
                    Map.entry("findSyntax", "find KEYWORD..."),
                    Map.entry("findDescription", "Show players whose names contain a keyword\n"
                            + "Matches whole words only, ignores capitalisation\ne.g. find wei ming"),
                    Map.entry("deleteSyntax", "delete INDEX"),
                    Map.entry("deleteDescription", "Delete the player at that position in the list\n"
                            + "shown right now. Positions change after a delete.\ne.g. delete 3"),
                    Map.entry("statusSyntax", "keep INDEX\n\ncut INDEX\n\npending INDEX"),
                    Map.entry("statusDescription", "Mark the player at that position as kept, cut,\n"
                            + "or not yet decided (pending)\ne.g. keep 3"),
                    Map.entry("squadSyntax", "squad"),
                    Map.entry("squadDescription", "Show only the players you have kept"),
                    Map.entry("rateSyntax", "rate INDEX RATING"),
                    Map.entry("rateDescription", "Set a player's rating (whole number 0 to 10)\n"
                            + "Use unrated as RATING to remove it.\ne.g. rate 1 8, rate 1 unrated"),
                    Map.entry("helpSyntax", "help"), Map.entry("helpDescription", "Show this summary"),
                    Map.entry("clearSyntax", "clear"),
                    Map.entry("clearDescription", "Delete all players; cannot be undone\n"
                            + "No confirmation. Use clear on its own; extra text is rejected."),
                    Map.entry("exitSyntax", "exit"), Map.entry("exitDescription", "Close CouchCoach"),
                    Map.entry("injurySyntax", "injury INDEX i/STATUS"),
                    Map.entry("injuryDescription", "Mark a player injured or fit again; ignores case\n"
                            + "e.g. injury 1 i/injured or injury 1 i/fit"),
                    Map.entry("remarkSyntax", "remark INDEX r/REMARK"),
                    Map.entry("remarkDescription", "Set or replace a player remark; empty r/ clears it\n"
                            + "Use r/ only once; a second r/ after a space is rejected\n"
                            + "e.g. remark 1 r/Strong left foot"),
                    Map.entry("sortSyntax", "sort rating [all|squad]\n[asc|desc]"),
                    Map.entry("sortDescription", "Sort the current view, all players, or the kept squad\n"
                            + "Highest first by default; unrated players last. Use list to reset.\n"
                            + "e.g. sort rating squad desc"),
                    Map.entry("saveSyntax", "save"),
                    Map.entry("saveDescription", "Save the current team without changing players\n"
                            + "After a save failure, fix the problem and use save\n"
                            + "Do not repeat the original command"),
                    Map.entry("helpFooter", "Your data is saved automatically after every change."));

            expectedText.forEach((id, text) -> {
                Label label = (Label) help.getContent().lookup("#" + id);
                assertEquals(text, label.getText());
            });
            for (String command : List.of("add", "list", "find", "delete", "status", "squad",
                    "help", "exit", "edit", "rate", "filter", "remark", "injury", "sort", "clear", "tag", "save")) {
                assertTrue(((Label) help.getContent().lookup("#" + command + "Description")).isWrapText());
            }
        });
    }
}
