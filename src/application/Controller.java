package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
//	@FXML
//	private ListView<String> sirNameList;
	private HashSet<String> sirNameLabels = new HashSet<String>();
	
    @FXML
	public void sirNameSelected(ActionEvent e) {
		String value = sirNameCombo.getValue();
		System.out.println("sirNameSelected : value="+value);
		if (value != null) {
			sirNameLabels.clear();
		    anchorPane.getChildren().removeAll(visibleShapes);
			Platform.runLater(() -> townNameCombo.setValue(null));
			Collection<OrigDestPair> odPairs = DataSourceFactory.dataSource.getOrigDestPairBySirName(value);
			int orderIndex = 0;
			for (OrigDestPair odPair : odPairs) {
				drawArc(odPair, orderIndex);
				this.addLocLabel(odPair.getDestination(), true);
				this.addLocLabel(odPair.getOrigin(), false);
				
				orderIndex++;
			}
			
//			ObservableList<String> items =FXCollections.observableArrayList (value);
//			sirNameList.setItems(items);
		}
	}

	private void drawArc(OrigDestPair odPair, int orderIndex) {
		NamedPoint uSLocCoords = odPair.getDestination();

			Circle circ = new Circle();
			circ.setRadius(5);
			circ.setCenterX(translateUsX(uSLocCoords.getX()));
			circ.setCenterY(translateUsY(uSLocCoords.getY()));
			circ.setFill(Color.RED);
			circ.setStroke(Color.BLACK);
			anchorPane.getChildren().add(circ);
			visibleShapes.add(circ);
			
			//addLocLabel(uSLocCoords, true);

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
	      //calc control point based on order index
	      double controlX = usImageView.getLayoutX()+usImageView.getFitWidth();//at seem between 2 maps
	      double controlY = 26 + orderIndex * 20;//off set subsequent control points by 10 pixels
	      if (! sirNameLabels.contains(odPair.getPairName())) {
			//double controlY = circ2.getCenterY() + orderIndex * 20;//off set subsequent control points by 10 pixels
			//put label with sir name at control point
			Label nameLabel = new Label();
			nameLabel.setText(odPair.getPairName());
			nameLabel.setLayoutX(controlX);
			nameLabel.setLayoutY(26 + sirNameLabels.size()*16);
			nameLabel.getStyleClass().add("label-sir-name");
			anchorPane.getChildren().add(nameLabel);
			visibleShapes.add(nameLabel);
			sirNameLabels.add(odPair.getPairName());
		  }
		  quadCurve.setControlX(controlX); 
	      quadCurve.setControlY(controlY);
	      quadCurve.setStroke(Color.BLACK);
	      quadCurve.setStrokeWidth(1);
	      quadCurve.setFill(Color.TRANSPARENT);
	      anchorPane.getChildren().add(quadCurve);
	      visibleShapes.add(quadCurve);
//	      Label nameLabel = new Label();
//	      nameLabel.setText(odPair.getPairName());
//	      nameLabel.setLayoutX(circ2.getCenterX()+10);
//	      nameLabel.setLayoutY(controlY);
//	      anchorPane.getChildren().add(nameLabel);
//	      visibleShapes.add(nameLabel);

	}
	
	private void addLocLabel (NamedPoint point, boolean usNotEus) {
	      Label nameLabel = new Label();
	      nameLabel.setText(point.getName());
	      double x = 0;
	      double y = 0;
	      double offset =  25 ;
	      if (usNotEus) {
	    	  x = translateUsX(point.getX()) - offset;
	    	  y = translateUsY(point.getY());
	      }
	      else {
	    	  x = translateEusX(point.getX());
	    	  y = translateEusY(point.getY());
	      }
	      nameLabel.setLayoutX(x);
	      nameLabel.setLayoutY(y);
	      nameLabel.getStyleClass().add("label-location");
	      anchorPane.getChildren().add(nameLabel);
	      visibleShapes.add(nameLabel);		
	}
	
	private double fudgeUsX = 0;
	private double fudgeUsY = -13;
	private double fudgeEusX = -3;
	private double fudgeEusY = -5;
    
    private double translateEusY(double y) {
		double originEusY = eusImageView.getLayoutY();
		double extentEusY = eusImageView.getFitHeight();
		double gpsOriginEusY = DataSourceFactory.dataSource.getEusLocation(euskadiUpperLeftKey).getY();
		double gpsExtentEusY = DataSourceFactory.dataSource.getEusLocation(euskadiLowerRightKey).getY()-gpsOriginEusY;
		double fractionOfGPSExtent = (y - gpsOriginEusY) /gpsExtentEusY;
		return originEusY + fractionOfGPSExtent*extentEusY + fudgeEusY;	}

	private double translateEusX(double x) {
		double origin = eusImageView.getLayoutX();
		double extent = eusImageView.getFitWidth();
		double gpsOrigin = DataSourceFactory.dataSource.getEusLocation(euskadiUpperLeftKey).getX();
		double gpsExtent = DataSourceFactory.dataSource.getEusLocation(euskadiLowerRightKey).getX()-gpsOrigin;
		double fractionOfGPSExtent = (x - gpsOrigin) /gpsExtent;
		return origin + fractionOfGPSExtent*extent + fudgeEusX;
	}

	private double translateUsY(double y) {
		double origin = usImageView.getLayoutY();
		double extent = usImageView.getFitHeight();
		double gpsOrigin = DataSourceFactory.dataSource.getUSLocation(westUSUpperLeftKey).getY();
		double gpsExtent = DataSourceFactory.dataSource.getUSLocation(westUSLowerRightKey).getY()-gpsOrigin;
		double fractionOfGPSExtent = (y - gpsOrigin) /gpsExtent;
		return origin + fractionOfGPSExtent*extent + fudgeUsY;
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
		return origin + fractionOfGPSExtent*extent +fudgeUsX;
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
		if (value != null) {
			sirNameLabels.clear();
		    anchorPane.getChildren().removeAll(visibleShapes);	
			Platform.runLater(() -> sirNameCombo.setValue(null));
			SortedSet<String> sirNames = new TreeSet<String>();
			Collection<OrigDestPair> odPairs = DataSourceFactory.dataSource.getOrigDestPairsByTownName(value);
			int orderIndex = 0;
			for (OrigDestPair odPair : odPairs) {
				drawArc(odPair,orderIndex);
				//sirNames.add(odPair.getPairName());
				addLocLabel(odPair.getDestination(), true);
				addLocLabel(odPair.getOrigin(), false);
				orderIndex++ ;
			}
//			ObservableList<String> items =FXCollections.observableArrayList (sirNames);
//			sirNameList.setItems(items);
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
