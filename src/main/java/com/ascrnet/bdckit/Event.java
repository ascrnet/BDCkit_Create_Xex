package com.ascrnet.bdckit;

import java.util.function.UnaryOperator;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextFormatter.Change;


public class Event {

	public static void limitTextField(TextField textField, int limit) {
		UnaryOperator<Change> textLimitFilter = change -> {
			if (change.isContentChange()) {
				int newLength = change.getControlNewText().length();
				if (newLength > limit) {
					String trimmedText = change.getControlNewText().substring(0, limit);
					change.setText(trimmedText);
					int oldLength = change.getControlText().length();
					change.setRange(0, oldLength);
				}
			}
			return change;
		};
		textField.setTextFormatter(new TextFormatter<Object>(textLimitFilter));
	}

	public static void showAlert(String mensaje, String title, int sw) {
		Alert alert;
		if (sw== 1) { 
			alert= new Alert(AlertType.WARNING);
		} else {
			alert= new Alert(AlertType.INFORMATION);
		}
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(mensaje);
		alert.showAndWait();
	}

}
