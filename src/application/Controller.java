package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

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
	private static final int COL2_Y_OFFSET = 8;
	private static final int NAME_LABEL_TEXT_HEIGHT = 16;
	private static final int MAX_NUM_NAMES = 30;
	private static final double COLUMN_OFFSET = 70;
	private long lastActionTime;
	private static final long ACTION_TIMEOUT = 2*60*1000;//minutes*second/minute*ms/sec (1minute)
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
    	lastActionTime = new Date().getTime();
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

	      //determine label location based on order index
	      double labelLocX = usImageView.getLayoutX()+usImageView.getFitWidth();//at seem between 2 maps
	      double labelLocY = usImageView.getLayoutY() + orderIndex * NAME_LABEL_TEXT_HEIGHT;//off set subsequent control points by 10 pixels
	      //handle overflow case -> start a 2nd column
	      if (orderIndex > MAX_NUM_NAMES) {
	    	  labelLocX = labelLocX + COLUMN_OFFSET;
	    	  labelLocY = usImageView.getLayoutY()+COL2_Y_OFFSET + (orderIndex-MAX_NUM_NAMES)*NAME_LABEL_TEXT_HEIGHT;
	      }
	      //put label with sir name at label loc point
			Label nameLabel = new Label();
			nameLabel.setText(odPair.getPairName());
			nameLabel.setLayoutX(labelLocX);
			nameLabel.setLayoutY(labelLocY);
			nameLabel.getStyleClass().add("label-sir-name");
			anchorPane.getChildren().add(nameLabel);
			visibleShapes.add(nameLabel);
			sirNameLabels.add(odPair.getPairName());
		  labelLocY = labelLocY+NAME_LABEL_TEXT_HEIGHT;
	      addArc(usLoc.getX(),usLoc.getY(), labelLocX, labelLocY);
	      addArc(eusLoc.getX(), eusLoc.getY(), labelLocX, labelLocY);

	}

	private void addArc(double startX, double startY, double endX, double endY) {
		QuadCurve quadCurve = new QuadCurve();        
	      //Adding properties to the Quad Curve 
	      quadCurve.setStartX(startX); 
	      quadCurve.setStartY(startY); 
	      quadCurve.setEndX(endX); 
	      quadCurve.setEndY(endY);
	      //set control point x half way between start and end.
		  quadCurve.setControlX(startX +(endX-startX)/2); 
	      quadCurve.setControlY(endY);
	      quadCurve.setStroke(Color.BLACK);
	      quadCurve.setStrokeWidth(1);
	      quadCurve.setFill(Color.TRANSPARENT);
	      anchorPane.getChildren().add(quadCurve);
	      visibleShapes.add(quadCurve);
	}
	
	private void addLocLabel (NamedPoint point, boolean usNotEus) {

		String locName = point.getName();
		int numChars = locName.length();
		if (!locNameLabels.contains(locName)) {
	      Label nameLabel = new Label();
	      nameLabel.setText(point.getName());
	      double x = 0;
	      double y = 0;

	      if (usNotEus) {
	    	  x = point.getX() - (numChars*6.5) + offsets[rotIndex][0];
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
	
	private static final int[][] offsets = {{0,0}, {0,0}, {0, 0 }};
	//private static final double baseXoffset =  -25 ;
	private int rotIndex = 0; 
	


	private static List<Node> visibleShapes = new ArrayList<Node>();
    
	
	@FXML
	public void townNameSelected(ActionEvent e) {
    	lastActionTime = new Date().getTime();
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

		Timer timer = new Timer();
		timer.schedule(new IdleTimerTask(), 120000, 60000);		
	};
	
	private int idlePickIndex = 0;
	
	private class IdleTimerTask extends TimerTask{
		@Override
		public void run() {
			System.out.println("Idle Timer Task Run");
			long now = new Date().getTime();
			if ( now > lastActionTime + ACTION_TIMEOUT) {
				Platform.runLater( () -> {
					//move through sir names in reverse alphabetical order. 
					idlePickIndex = (idlePickIndex-1);
					if (idlePickIndex < 0) idlePickIndex = sirNameCombo.getItems().size()-1;
					System.out.println("Timeout occurred. new index is:"+idlePickIndex);
					sirNameCombo.setValue(sirNameCombo.getItems().get(idlePickIndex));
				} );
			}
			
		}


		
	}

}
