module BmccPlaceMap1 {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.base;
	requires com.opencsv;
	
	opens application to javafx.graphics, javafx.fxml;
}
