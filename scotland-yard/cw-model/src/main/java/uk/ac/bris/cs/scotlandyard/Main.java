package uk.ac.bris.cs.scotlandyard;

import javafx.application.Application;
import javafx.stage.Stage;
import uk.ac.bris.cs.scotlandyard.ui.Utils;
import uk.ac.bris.cs.scotlandyard.ui.controller.LocalGameController;

/**
 * Main entry point
 */
public final class Main {

	// prevents JPMS related issues
	public static final class JFXApp extends Application {
		@Override public void start(Stage stage) {
			Thread.currentThread().setUncaughtExceptionHandler(
					(thread, throwable) -> Utils.handleFatalException(throwable));
			LocalGameController.newGame(Utils.setupResources(), stage);
		}
	}
	public static void main(String[] args) { JFXApp.launch(JFXApp.class, args); }

}
