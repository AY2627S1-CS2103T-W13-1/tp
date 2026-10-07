package seedu.address.testutil;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;

/**
 * Runs UI test actions on the JavaFX Application Thread.
 */
public final class JavaFxTestUtil {
    private static boolean isToolkitInitialized;

    private JavaFxTestUtil() {}

    /**
     * Runs {@code action} on the JavaFX Application Thread and waits for it to finish.
     *
     * @throws Exception if the action fails or does not finish within ten seconds.
     */
    public static void runOnFxThread(Runnable action) throws Exception {
        initializeToolkit();
        FutureTask<Void> task = new FutureTask<>(action, null);
        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }

    /**
     * Starts the JavaFX runtime once and keeps it available between UI tests.
     */
    private static synchronized void initializeToolkit() {
        if (!isToolkitInitialized) {
            Platform.startup(() -> Platform.setImplicitExit(false));
            isToolkitInitialized = true;
        }
    }
}
