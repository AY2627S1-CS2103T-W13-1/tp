package seedu.address.ui;

import javafx.scene.layout.Region;

/**
 * Panel containing the CouchCoach command summary.
 */
public class HelpPanel extends UiPart<Region> {
    private static final String FXML = "HelpPanel.fxml";

    /**
     * Creates a panel containing the command summary.
     */
    public HelpPanel() {
        super(FXML);
    }
}
