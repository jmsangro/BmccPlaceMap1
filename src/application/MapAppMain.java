package application;
	
import java.util.Properties;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class MapAppMain extends Application {
	@Override
	public void start(Stage stage) {
		try {
			Properties props = new Properties();
			
			props.load(this.getClass().getResourceAsStream("application.properties"));
			DataSourceFactory.dataSource = new CsvDataSource(props);
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);

			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
