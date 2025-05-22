package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
	private HashSet<String> locNameLabels = new HashSet<String>();
	
    @FXML
	public void sirNameSelected(ActionEvent e) {
		String value = sirNameCombo.getValue();
		System.out.println("sirNameSelected : value="+value);
		if (value != null) {
			sirNameLabels.clear();
			locNameLabels.clear();
		    anchorPane.getChildren().removeAll(visibleShapes);
			Platform.runLater(() -> townNameCombo.setValue(null));
			Collection<OrigDestPair> odPairs = DataSourceFactory.dataSource.getOrigDestPairBySirName(value);
			int orderIndex = 0;
			for (OrigDestPair odPair : odPairs) {
				drawArc(odPair, orderIndex);
//				this.addLocLabel(odPair.getDestination(), true);
//				this.addLocLabel(odPair.getOrigin(), false);
				
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
		double originX = usImageView.getLayoutX();
		double originY = usImageView.getLayoutY();
		NamedPoint canvasOrigin = new NamedPoint("usCanvasOrigin", originX, originY);
		double extentX = usImageView.getFitWidth();
		double extentY = usImageView.getFitHeight();
		NamedPoint canvasExtent = new NamedPoint("usCanvasExtent", extentX, extentY);
		NamedPoint usLoc = DataSourceFactory.dataSource.getUSMapper().map(uSLocCoords, canvasOrigin, canvasExtent);
		circ.setCenterX(usLoc.getX());
		circ.setCenterY(usLoc.getY());
		circ.setFill(Color.RED);
		circ.setStroke(Color.BLACK);
		anchorPane.getChildren().add(circ);
		this.addLocLabel(usLoc, true);
		visibleShapes.add(circ);
		
		//addLocLabel(uSLocCoords, true);

		NamedPoint eusLocCoords = odPair.getOrigin();
		originX = eusImageView.getLayoutX();
		originY = eusImageView.getLayoutY();
		canvasOrigin = new NamedPoint("eusCanvasOrigin", originX, originY);
		extentX = eusImageView.getFitWidth();
		extentY = eusImageView.getFitHeight();
		canvasExtent = new NamedPoint("eusCanvasExtent", extentX, extentY);
		NamedPoint eusLoc = DataSourceFactory.dataSource.getEusMapper().map(eusLocCoords, canvasOrigin, canvasExtent);
		Circle circ2 = new Circle();
		circ2.setRadius(5);
		circ2.setCenterX(eusLoc.getX());
		circ2.setCenterY(eusLoc.getY());
		circ2.setFill(Color.GREEN);
		circ2.setStroke(Color.BLACK);
		anchorPane.getChildren().add(circ2);
		visibleShapes.add(circ2);
		this.addLocLabel(eusLoc, false);
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

		String locName = point.getName();
		if (!locNameLabels.contains(locName)) {
	      Label nameLabel = new Label();
	      nameLabel.setText(point.getName());
	      double x = 0;
	      double y = 0;

	      if (usNotEus) {
	    	  x = point.getX() + baseXoffset + offsets[rotIndex][0];
	    	  y = point.getY() + offsets[rotIndex][1];
	      }
	      else {
	    	  x = point.getX();
	    	  y = point.getY();
	      }
	      nameLabel.setLayoutX(x);
	      nameLabel.setLayoutY(y);
	      nameLabel.getStyleClass().add("label-location");
	      anchorPane.getChildren().add(nameLabel);
	      visibleShapes.add(nameLabel);	
	      rotIndex = (rotIndex+1)%offsets.length;
	      locNameLabels.add(locName);
		}
	}
	
	private static final int[][] offsets = {{0,-14}, {0,-7}, {0, 0 }};
	private static final double baseXoffset =  -25 ;
	private int rotIndex = 0; 
	


	private static List<Node> visibleShapes = new ArrayList<Node>();
    
	
	@FXML
	public void townNameSelected(ActionEvent e) {
		String value = townNameCombo.getValue();
		System.out.println("townNameSelected : value="+value);
		if (value != null) {
			sirNameLabels.clear();
			locNameLabels.clear();
		    anchorPane.getChildren().removeAll(visibleShapes);	
			Platform.runLater(() -> sirNameCombo.setValue(null));
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
