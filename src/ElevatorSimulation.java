// Owner: Jason Curcio

import java.util.logging.Logger;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ElevatorSimulation extends Application {
	private ElevatorSimController controller;
	private BorderPane main = new BorderPane();
	private GridPane gpLeft = new GridPane();
	private GridPane gpRight = new GridPane();
	private StackPane stateStack = new StackPane();
	private int currFloor;
	private int passengers;
	private int time;

	private Button stepSim;
	private Button stepSimNumTimes;
	private Button run;
	private Button log;
	
	private Text clockText;
	private Text up1;
	private Text up2;
	private Text up3;
	private Text up4;
	private Text up5;
	private Text up6;
	private Text down1;
	private Text down2;
	private Text down3;
	private Text down4;
	private Text down5;
	private Text down6;
	private Text floor1;
	private Text floor2;
	private Text floor3;
	private Text floor4;
	private Text floor5;
	private Text floor6;
	private Text passengers1;
	private Text passengers2;
	private Text passengers3;
	private Text passengers4;
	private Text passengers5;
	private Text passengers6;
	
	private Rectangle doorsClosed;
	private Rectangle doorsOpen;
	private Polygon board;
	private Polygon offload;
	private Polygon movingUp;
	private Polygon movingDown;
	
	private TextField tfStepSimNumTimes = new TextField();
	
	private Timeline t = new Timeline(new KeyFrame(Duration.millis(25),
			ae -> controller.stepSim()));
	
	private final static int STOP = 0;
	private final static int MVTOFLR = 1;
	private final static int OPENDR = 2;
	private final static int OFFLD = 3;
	private final static int BOARD = 4;
	private final static int CLOSEDR = 5;
	private final static int MV1FLR = 6;

	public ElevatorSimulation() {
		controller = new ElevatorSimController(this);	
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene(main, 400, 600);

		// Clock at the top of borderpane
		HBox textBox = new HBox();
		clockText = new Text("Time: " + time);
		textBox.getChildren().add(clockText);
		textBox.setAlignment(Pos.CENTER);
		main.setTop(textBox);
		
		createStackPaneStates();
		createFloorsAndPassengersText();
		createUpAndDownQueueText();
		
		
		stepSim = new Button("StepSim");		
		stepSim.setOnAction(e -> controller.stepSim());
		
		stepSimNumTimes = new Button("Step N");
		stepSimNumTimes.setOnAction(e -> {
			t.setCycleCount(Integer.valueOf(tfStepSimNumTimes.getText()));
			t.play();
			tfStepSimNumTimes.clear();
		});
		
		
		run = new Button("Run");
		run.setOnAction(e -> {
			t.setCycleCount(Animation.INDEFINITE);
			t.play();
		});
		
		
		log = new Button("Log");
		log.setOnAction(e -> controller.enableLogging());
		
		HBox btnBox = new HBox(10);
		btnBox.getChildren().addAll(stepSim, stepSimNumTimes, tfStepSimNumTimes, run, log);
		main.setBottom(btnBox);
		
		passengers1.setText("0");
		
		primaryStage.setTitle("Elevator Simulation");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	
	
	
	private void createStackPaneStates() {
		doorsClosed = new Rectangle(25, 25, Color.BLACK);
		
		doorsOpen = new Rectangle(25, 25, Color.TRANSPARENT);
		doorsOpen.setStroke(Color.BLACK);
		doorsOpen.setStrokeWidth(2);
		doorsOpen.setVisible(false);
		
		board = new Polygon();
		board.getPoints().addAll(new Double[]{
			    0.0, 10.0,
			    20.0, 0.0,
			    20.0, 20.0 });
		board.setFill(Color.CYAN);
		board.setVisible(false);
		
		offload = new Polygon();
		offload.getPoints().addAll(new Double[]{
			    0.0, 0.0,
			    0.0, 20.0,
			    20.0, 10.0 });
		offload.setFill(Color.CYAN);
		offload.setVisible(false);
		
		
		movingUp = new Polygon();
		movingUp.getPoints().addAll(new Double[]{
			    10.0, 0.0,
			    0.0, 20.0,
			    20.0, 20.0 });
		movingUp.setFill(Color.RED);
		movingUp.setVisible(false);
		
		movingDown = new Polygon();
		movingDown.getPoints().addAll(new Double[]{
			    0.0, 0.0,
			    20.0, 0.0,
			    10.0, 20.0 });
		movingDown.setFill(Color.RED);
		movingDown.setVisible(false);
		
		
		stateStack.getChildren().addAll(movingDown, movingUp, offload, board, doorsClosed, doorsOpen);
		gpLeft.add(stateStack, 0, 5);
	}
	
	
	
	
	private void createFloorsAndPassengersText() {
		gpLeft.setVgap(50);
		gpLeft.setPrefWidth(120.0);
		
		floor1 = new Text("1st");
		floor2 = new Text("2nd");
		floor3 = new Text("3rd");
		floor4 = new Text("4th");
		floor5 = new Text("5th");
		floor6 = new Text("6th");
		
		passengers1 = new Text("");
		passengers2 = new Text("");
		passengers3 = new Text("");
		passengers4 = new Text("");
		passengers5 = new Text("");
		passengers6 = new Text("");
		
		gpLeft.add(floor6, 1, 0);
		gpLeft.add(floor5, 1, 1);
		gpLeft.add(floor4, 1, 2);
		gpLeft.add(floor3, 1, 3);
		gpLeft.add(floor2, 1, 4);
		gpLeft.add(floor1, 1, 5);
		
		gpLeft.add(passengers1, 2, 5);
		gpLeft.add(passengers2, 2, 4);
		gpLeft.add(passengers3, 2, 3);
		gpLeft.add(passengers4, 2, 2);
		gpLeft.add(passengers5, 2, 1);
		gpLeft.add(passengers6, 2, 0);
		
		main.setLeft(gpLeft);
	}
	
	
	
	
	private void createUpAndDownQueueText() {
		gpRight.setVgap(50);
		gpRight.setHgap(40);
		
		up1 = new Text("Up: ");
		up2 = new Text("Up: ");
		up3 = new Text("Up: ");
		up4 = new Text("Up: ");
		up5 = new Text("Up: ");
		up6 = new Text("Up: ");
		down1 = new Text("Down: ");
		down2 = new Text("Down: ");
		down3 = new Text("Down: ");
		down4 = new Text("Down: ");
		down5 = new Text("Down: ");
		down6 = new Text("Down: ");
		
		gpRight.add(up6, 0, 0);
		gpRight.add(down6, 1, 0);
		gpRight.add(up5, 0, 1);
		gpRight.add(down5, 1, 1);
		gpRight.add(up4, 0, 2);
		gpRight.add(down4, 1, 2);
		gpRight.add(up3, 0, 3);
		gpRight.add(down3, 1, 3);
		gpRight.add(up2, 0, 4);
		gpRight.add(down2, 1, 4);
		gpRight.add(up1, 0, 5);
		gpRight.add(down1, 1, 5);
		
		main.setCenter(gpRight);
	}
	
	
	

	public void updateSim(int passInElevator, String[][] passengers2, int currFloor,
			int currState) {
		
		time = controller.getTime();
		clockText.setText("Time: " + time);
		
		if (controller.isEndSim()) {
			t.stop();
		}
		
		clearStatesAndElevatorPassengers();
		setPassInElevatorToCurrentFloor(passInElevator, currFloor);
		setStackPaneToCurrentFloor(currFloor);
		setCurrStateVisibility(currState);
		
		clearPassengerQueues();
		setPassengerQueues(passengers2);      
	}

	private void clearStatesAndElevatorPassengers() {
		doorsClosed.setVisible(false);
		doorsOpen.setVisible(false);
		board.setVisible(false);
		offload.setVisible(false);
		movingUp.setVisible(false);
		movingDown.setVisible(false);
		
		passengers1.setText("");
		passengers2.setText("");
		passengers3.setText("");
		passengers4.setText("");
		passengers5.setText("");
		passengers6.setText("");
	}
	
	
	private void setStackPaneToCurrentFloor(int currFloor) {
		gpLeft.getChildren().remove(stateStack);
		
		switch (currFloor) {
		case 0:
			gpLeft.add(stateStack, 0, 5);
			break;
		case 1: 
			gpLeft.add(stateStack,  0, 4);
			break;
		case 2:
			gpLeft.add(stateStack,  0, 3);
			break;
		case 3:
			gpLeft.add(stateStack,  0, 2);
			break;
		case 4:
			gpLeft.add(stateStack,  0, 1);
			break;
		case 5:
			gpLeft.add(stateStack,  0, 0);
			break;
		}
	}
	
	
	private void setCurrStateVisibility(int currState) {
		
		if (controller.requestDirectionOfElevator() == 1) {
			switch (currState) {
			case STOP: 
				doorsClosed.setVisible(true);
				break;
			case MVTOFLR:
				movingUp.setVisible(true);
				break;
			case OPENDR: 
				doorsOpen.setVisible(true);
				break;
			case OFFLD:
				offload.setVisible(true);
				break;
			case BOARD:
				board.setVisible(true);
				break;
			case CLOSEDR:
				doorsClosed.setVisible(true);
				break;
			case MV1FLR:
				movingUp.setVisible(true);
				break;
			}
		}
		
		
		else {
			switch (currState) {
			case STOP: 
				doorsClosed.setVisible(true);
				break;
			case MVTOFLR:
				movingDown.setVisible(true);
				break;
			case OPENDR: 
				doorsOpen.setVisible(true);
				break;
			case OFFLD:
				offload.setVisible(true);
				break;
			case BOARD:
				board.setVisible(true);
				break;
			case CLOSEDR:
				doorsClosed.setVisible(true);
				break;
			case MV1FLR:
				movingDown.setVisible(true);
				break;
			}
		}
	}

	
	private void setPassInElevatorToCurrentFloor(int passInElevator, int currFloor) {
		
		switch (currFloor) {
		case 0: 
			passengers1.setText(Integer.toString(passInElevator));
			break;
		case 1: 
			passengers2.setText(Integer.toString(passInElevator));
			break;
		case 2:
			passengers3.setText(Integer.toString(passInElevator));
			break;
		case 3:
			passengers4.setText(Integer.toString(passInElevator));
			break;
		case 4:
			passengers5.setText(Integer.toString(passInElevator));
			break;
		case 5:
			passengers6.setText(Integer.toString(passInElevator));
			break;
		}
	}
	
	private void clearPassengerQueues() {
		up1.setText("Up: ");
		up2.setText("Up: ");
		up3.setText("Up: ");
		up4.setText("Up: ");
		up5.setText("Up: ");
		up6.setText("Up: ");
		down1.setText("Down: ");
		down2.setText("Down: ");
		down3.setText("Down: ");
		down4.setText("Down: ");
		down5.setText("Down: ");
		down6.setText("Down: ");
	}
	
	private void setPassengerQueues(String[][] queues) {
		up1.setText("Up: " + queues[0][0]);
		down1.setText("Down: " + queues[0][1]);
		up2.setText("Up: " + queues[1][0]);
		down2.setText("Down: " + queues[1][1]);
		up3.setText("Up: " + queues[2][0]);
		down3.setText("Down: " + queues[2][1]);
		up4.setText("Up: " + queues[3][0]);
		down4.setText("Down: " + queues[3][1]);
		up5.setText("Up: " + queues[4][0]);
		down5.setText("Down: " + queues[4][1]);
		up6.setText("Up: " + queues[5][0]);
		down6.setText("Down: " + queues[5][1]);
	}
	
	
	public static void main (String[] args) {
		Application.launch(args);
	}

}
