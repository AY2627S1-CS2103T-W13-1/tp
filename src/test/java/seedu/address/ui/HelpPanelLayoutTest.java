package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.JavaFxTestUtil.runOnFxThread;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

public class HelpPanelLayoutTest {
    @Test
    public void layout_normalAndMinimumWidths_alignsCommandDescriptions() throws Exception {
        runOnFxThread(() -> {
            ScrollPane help = (ScrollPane) new HelpPanel().getRoot();
            new Scene(help);

            for (double width : List.of(740.0, 450.0)) {
                help.resize(width, 600);
                help.applyCss();
                help.layout();
                Label firstSyntax = (Label) help.getContent().lookup("#addSyntax");
                Label firstDescription = (Label) help.getContent().lookup("#addDescription");
                double commandX = firstSyntax.localToScene(0, 0).getX();
                double descriptionX = firstDescription.localToScene(0, 0).getX();

                for (String command : List.of("add", "list", "find", "delete", "help", "exit")) {
                    Label syntax = (Label) help.getContent().lookup("#" + command + "Syntax");
                    Label description = (Label) help.getContent().lookup("#" + command + "Description");

                    assertEquals(commandX, syntax.localToScene(0, 0).getX(), 0.5);
                    assertEquals(descriptionX, description.localToScene(0, 0).getX(), 0.5);
                    assertEquals(syntax.localToScene(0, 0).getY(), description.localToScene(0, 0).getY(), 0.5);
                    assertTrue(syntax.getBoundsInParent().getMaxX() < description.getBoundsInParent().getMinX());
                    assertTrue(description.getWidth() <= width - descriptionX);
                    assertTrue(description.isWrapText());
                }
            }
        });
    }
}
