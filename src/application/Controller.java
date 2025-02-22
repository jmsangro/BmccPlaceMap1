package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.QuadCurve;

public class Controller implements Initializable {
	@FXML
	private ComboBox<String> sirNameCombo;
	@FXML
	private ComboBox<String> townNameCombo;
	@FXML
	private AnchorPane anchorPane;	
	@FXML
	private ImageView usImageView;
	@FXML
	private ImageView eusImageView;
	
    @FXML
	public void sirNameSelected(ActionEvent e) {
		String value = sirNameCombo.getValue();
		townNameCombo.setValue(null);
	    anchorPane.getChildren().removeAll(visibleShapes);
		System.out.println("sirNameSelected : value="+value);
		if (value != null) {
			Collection<OrigDestPair> odPairs = DataSourceFactory.dataSource.getOrigDestPairBySirName(value);
			for (OrigDestPair odPair : odPairs) {
				drawArc(odPair);
			}
		}
	}

	private void drawArc(OrigDestPair odPair) {
		NamedPoint uSLocCoords = odPair.getDestination();

			Circle circ = new Circle();
			circ.setRadius(5);
			circ.setCenterX(translateUsX(uSLocCoords.getX()));
			circ.setCenterY(translateUsY(uSLocCoords.getY()));
			circ.setFill(Color.RED);
			circ.setStroke(Color.BLACK);
			anchorPane.getChildren().add(circ);
			visibleShapes.add(circ);

		NamedPoint eusLocCoords = odPair.getOrigin();

			Circle circ2 = new Circle();
			circ2.setRadius(5);
			circ2.setCenterX(translateEusX(eusLocCoords.getX()));
			circ2.setCenterY(translateEusY(eusLocCoords.getY()));
			circ2.setFill(Color.GREEN);
			circ2.setStroke(Color.BLACK);
			anchorPane.getChildren().add(circ2);
			visibleShapes.add(circ2);

		// draw curve between origin and destination
	      QuadCurve quadCurve = new QuadCurve();  
	       
	      //Adding properties to the Quad Curve 
	      quadCurve.setStartX(circ.getCenterX()); 
	      quadCurve.setStartY(circ.getCenterY()); 
	      quadCurve.setEndX(circ2.getCenterX()); 
	      quadCurve.setEndY(circ2.getCenterY()); 
	      quadCurve.setControlX(circ.getCenterX()); 
	      quadCurve.setControlY(circ2.getCenterY());
	      quadCurve.setStroke(Color.BLACK);
	      quadCurve.setStrokeWidth(2);
	      quadCurve.setFill(Color.TRANSPARENT);
	      anchorPane.getChildren().add(quadCurve);
	      visibleShapes.add(quadCurve);
	}
    
    private double translateEusY(double y) {
		double origin = eusImageView.getLayoutY();
		double extent = eusImageView.getFitHeight();
		double gpsOrigin = DataSourceFactory.dataSource.getEusLocation(euskadiUpperLeftKey).getY();
		double gpsExtent = DataSourceFactory.dataSource.getEusLocation(euskadiLowerRightKey).getY()-gpsOrigin;
		double fractionOfGPSExtent = (y - gpsOrigin) /gpsExtent;
		return origin + fractionOfGPSExtent*extent;	}

	private double translateEusX(double x) {
		double origin = eusImageView.getLayoutX();
		double extent = eusImageView.getFitWidth();
		double gpsOrigin = DataSourceFactory.dataSource.getEusLocation(euskadiUpperLeftKey).getX();
		double gpsExtent = DataSourceFactory.dataSource.getEusLocation(euskadiLowerRightKey).getX()-gpsOrigin;
		double fractionOfGPSExtent = (x - gpsOrigin) /gpsExtent;
		return origin + fractionOfGPSExtent*extent;
	}

	private double translateUsY(double y) {
		double origin = usImageView.getLayoutY();
		double extent = usImageView.getFitHeight();
		double gpsOrigin = DataSourceFactory.dataSource.getUSLocation(westUSUpperLeftKey).getY();
		double gpsExtent = DataSourceFactory.dataSource.getUSLocation(westUSLowerRightKey).getY()-gpsOrigin;
		double fractionOfGPSExtent = (y - gpsOrigin) /gpsExtent;
		return origin + fractionOfGPSExtent*extent;
	}
    
    public static String westUSUpperLeftKey = "WestUSUpperLeft";
    public static String westUSLowerRightKey = "WestUSLowerRight";
    public static String euskadiUpperLeftKey = "EuskadiUpperLeft";
    public static String euskadiLowerRightKey = "EuskadiLowerRight";

	private double translateUsX(double x) {
		double origin = usImageView.getLayoutX();
		double extent = usImageView.getFitWidth();
		double gpsOrigin = DataSourceFactory.dataSource.getUSLocation(westUSUpperLeftKey).getX();
		double gpsExtent = DataSourceFactory.dataSource.getUSLocation(westUSLowerRightKey).getX()-gpsOrigin;
		double fractionOfGPSExtent = (x - gpsOrigin) /gpsExtent;
		return origin + fractionOfGPSExtent*extent;
	}

	private static List<Node> visibleShapes = new ArrayList<Node>();
    
    private static HashMap<String, int[][]> usLocHash;
    static {
    	usLocHash = new HashMap<String, int[][]>();
    	usLocHash.put("Sangroniz", new int[][] {{50,70},{60,80}});
    	usLocHash.put("Zatica", new int[][] {{150,80},{170,100}});
    	
    }
	
	@FXML
	public void townNameSelected(ActionEvent e) {
		String value = townNameCombo.getValue();
		System.out.println("townNameSelected : value="+value);
		sirNameCombo.setValue(null);
	    anchorPane.getChildren().removeAll(visibleShapes);
		if (value != null) {
			Collection<OrigDestPair> odPairs = DataSourceFactory.dataSource.getOrigDestPairsByTownName(value);
			for (OrigDestPair odPair : odPairs) {
				drawArc(odPair);
			}
		}
	}





	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
        Collection<String> sirNames = DataSourceFactory.dataSource.getSirNames();
        sirNameCombo.setItems(FXCollections.observableArrayList(sirNames));
        Collection<String> townNames =DataSourceFactory.dataSource.getTownNames();
        townNameCombo.setItems(FXCollections.observableArrayList(townNames));
		
	};

}
