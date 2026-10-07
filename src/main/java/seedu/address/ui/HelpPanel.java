package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;

/**
 * Panel containing the CouchCoach command summary.
 */
public class HelpPanel extends UiPart<Region> {

    public static final String HELP_MESSAGE = "CouchCoach commands:\n\n"
            + "add n/NAME p/PHONE   Add a player\n"
            + "                     e.g. add n/Tan Wei Ming p/91234567\n"
            + "list                 Show all players\n"
            + "find KEYWORD...      Show players whose names contain a keyword\n"
            + "                     Matches whole words only, ignores capitalisation\n"
            + "                     e.g. find wei ming\n"
            + "delete INDEX         Delete the player at that position in the list\n"
            + "                     shown right now. Positions change after a delete.\n"
            + "                     e.g. delete 3\n"
            + "help                 Show this summary\n"
            + "exit                 Close CouchCoach\n\n"
            + "Your data is saved automatically after every change.";

    private static final String FXML = "HelpPanel.fxml";

    @FXML
    private TextArea helpText;

    /**
     * Creates a panel containing the command summary.
     */
    public HelpPanel() {
        super(FXML);
        helpText.setText(HELP_MESSAGE);
        helpText.positionCaret(0);
    }
}
