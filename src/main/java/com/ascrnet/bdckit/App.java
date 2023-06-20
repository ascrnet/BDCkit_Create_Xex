package com.ascrnet.bdckit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.ascrnet.bdckit.model.PackArchivos;
import com.ascrnet.bdckit.util.Utils;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class App extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {

			PackArchivos packArchivos = new PackArchivos();
			ListView<String> listView = new ListView<String>();

			FileChooser fileChooserOpen = new FileChooser();
			fileChooserOpen.setTitle("Cav/Int Scenarios");
			fileChooserOpen.getExtensionFilters().addAll(
					new ExtensionFilter("Cav/Int Scenarios", "*.cav;*.int"));

			FileChooser fileChooserSave = new FileChooser();
			fileChooserSave.setTitle("Save XEX");
			fileChooserSave.getExtensionFilters().addAll(
					new ExtensionFilter("Binary XEX", "*.xex"));

			HBox hbtitulo = new HBox();
			VBox vbLista = new VBox();
			VBox vbItems = new VBox();
			HBox hbButtons = new HBox();

			hbtitulo.getStyleClass().add("spacing");
			vbLista.getStyleClass().add("spacing");
			vbItems.getStyleClass().add("spacing");
			hbButtons.getStyleClass().add("spacing");

			Button BtnGenerar = new Button();
			Button BtnSalir = new Button();
			Button BtnAgregar = new Button();
			Button BtnLimpiar = new Button();
			Button BtnCreditos = new Button();

			Label lTitulo = new Label("Title:");
			TextField ntitulo = new TextField();
			Event.limitTextField(ntitulo,20);
			ntitulo.textProperty().addListener((observable, oldValue, newValue) -> {
				if (!newValue.matches("\\sa-zA-Z0-9*")) {
					ntitulo.setText(newValue.replaceAll("[^\\sa-zA-Z0-9]", ""));
				}
			});

			/* Botón cerrar app */
			BtnSalir.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent arg0) {
					Platform.exit();
				}
			});


			/* Botón agrega escenario */
			BtnAgregar.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					List<File> lista = fileChooserOpen.showOpenMultipleDialog(primaryStage);
					if (lista!=null) {
						for (File l : lista) {

							byte[] contenido = new byte[(int)l.length()];
							if (l.length() == 504) {
								FileInputStream fis;

								try {
									fis = new FileInputStream(l);	
									fis.read(contenido);
									if (packArchivos.add(l.getName(),l.getPath()))
									{
										listView.getItems().add(l.getName());
									} 
									fis.close();
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
							} 			         
						}
					}

				} 
			});


			/* Botón generar xex */
			BtnGenerar.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					if (packArchivos.size() != 0) {
						if (packArchivos.size()>0 && packArchivos.size()< 47) {
							File fileOut = fileChooserSave.showSaveDialog(primaryStage);
							if (fileOut != null) {
								try {
									Utils.copyFile(fileOut, packArchivos,ntitulo.getText());
								} catch (IOException e) {
									System.out.println("Error no found engine.dat");
									e.printStackTrace();
								}
							}
						} else {
							Event.showAlert("Exceeded the maximum of 47 permitted levels !!!","Warning",1);	
						}
					} else {
						Event.showAlert("Add cavern or bonus scenarios !!!","Warning",1);
					}
				}
			});


			BtnLimpiar.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					listView.getItems().clear();
					packArchivos.clean();
				}
			});

			BtnCreditos.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					Event.showAlert("Original engine by Peter Liepa\nEnhanced engine by Homesoft & Fandal 2011\nBDKit Create XEX by AsCrNet 2023","Credits",0);
				}
			});

			BtnAgregar.setText("Add");
			BtnGenerar.setText("Generate");
			BtnSalir.setText("Exit");
			BtnLimpiar.setText("Clean");
			BtnCreditos.setText("Credits");


			hbtitulo.setAlignment(Pos.TOP_LEFT);
			hbtitulo.getChildren().addAll(lTitulo, ntitulo);

			vbLista.setAlignment(Pos.CENTER);	
			vbLista.getChildren().add(listView);

			vbItems.setAlignment(Pos.CENTER);	
			vbItems.getChildren().addAll(BtnAgregar, BtnLimpiar, BtnCreditos);

			hbButtons.setAlignment(Pos.CENTER);
			hbButtons.getChildren().addAll(BtnGenerar, BtnSalir);

			BorderPane root = new BorderPane();
			root.setTop(hbtitulo);
			root.setCenter(vbLista);
			root.setRight(vbItems);
			root.setBottom(hbButtons);

			Scene scene = new Scene(root,300,500);
			scene.getStylesheets().add(getClass().getResource("/resource/application.css").toExternalForm());

			primaryStage.getIcons().add(new Image(App.class.getResourceAsStream("/resource/dbk.png")));
			primaryStage.setTitle("BDKit Create XEX");
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}