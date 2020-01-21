package main;
/**
 * 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


/**
 * @author mimlo
 *
 */
public class MainApplication extends Application {
	AnimationTimer timer;
	Timeline scorePoints;
	Scanner sc = new Scanner(System.in);

	Scene sceneMainMenu, sceneSettings, scene, sceneLeaderboard;
	Stage stage;
	Group group = new Group();
	Group leaderboardGroup = new Group();
	Text scorePrint;
	Text [] highScoreNames = new Text[5];
	Text [] highScores = new Text[5];
	Font font;
	int index = 0;
	BorderPane pane3 = new BorderPane();

	private static Pane root = new Pane();

	ImagePattern playerRun = null;
	ImagePattern playerJump = null;
	ImagePattern coin = null;

	static int counter = 0;

	static ImagePattern [] backgroundImages  = new ImagePattern [3];
	Node icon;
	private Player player = null;
	ArrayList<Integer> leaderboardScore = new ArrayList<Integer>();
	ArrayList <String> leaderboardName = new ArrayList<String>();
	File file = new File("Resources/LeaderBoard.txt");

	//Starting Platforms


	//Making Platforms and Coins
	ArrayList<GamePlatform> platforms = new ArrayList<GamePlatform>();
	static ArrayList<GameBackground> backgrounds = new ArrayList<GameBackground>();
	ArrayList<Coins> coins = new ArrayList<Coins>();
	GamePlatform p;
	Coins c;
	int numCoins = 0;
	private boolean canJump = true;
	private boolean doubleJump = true;
	private int jump = 26;//Changes the jump height
	private int score = 0;
	boolean isDead = false;
	KeyCode jumpButton;

	GamePlatform s = new GamePlatform(0, 340, 800, 5,  "platform", Color.BLACK);
	GamePlatform s2 = new GamePlatform(400, 240, 500, 5, "platform", Color.BLACK);

	public Parent initGame() {

		scorePoints = new Timeline(
				new KeyFrame(Duration.millis(400), e -> {
					group.getChildren().remove(scorePrint);
					score++;
					System.out.println(score); //temp
					scorePrint = new Text(Integer.toString(score));
					font = new Font("Candara", 38);
					scorePrint.setFont(font);
					scorePrint.setFill(Color.CRIMSON);
					group.getChildren().add(scorePrint);
				})
				);
		scorePoints.setCycleCount(Timeline.INDEFINITE);
		scorePoints.play();


		timer = new AnimationTimer() {

			public void handle(long now) {
				coins();
				platform();

				//player.antiGravity();

				//For kurtis to not die, and actually get to the red part :)
				for(int i = 0; i < platforms.size();i++) {

					if(player.getBoundsInParent().intersects(platforms.get(i).getBoundsInParent())) {
						if(	player.getBottom() < platforms.get(i).getTop()+10) {
							player.setY(platforms.get(i).getTop() - player.getHeight());
							canJump = true;
							doubleJump = true;
							player.antiGravity();
							player.setFill(playerRun);
						}							
					}
				}

				for(int i = 0; i < coins.size(); i++) {
					if(player.getBoundsInParent().intersects(coins.get(i).getBoundsInParent())) {
						root.getChildren().remove(coins.get(i));
						coins.remove(i);
						numCoins++;
						score += 10;
					}
				}
				player.gravity();
				if(canJump == true) {
					if(jumpButton == KeyCode.SPACE) {
						if(jump >= 0) {
							player.jump(jump);
							jump--;
							player.setFill(playerJump);
						}
						else {
							canJump = false;
							jump = 26;
							jumpButton = KeyCode.ALT;
						}
					}
				}
				else if(doubleJump == true) {
					if(jumpButton == KeyCode.SPACE) {
						if(jump >= 0) {
							player.jump(jump);
							jump--;
						}
						else {
							doubleJump = false;
							jump = 26;
							jumpButton = KeyCode.ALT;
						}
					}
				}
				if(player.getY() >= 600) {
					System.out.println("dead");
					dead(timer);
				}
				try {
					if (backgrounds.get(backgrounds.size()-1).getX() <= -2000 ) {
						background();
					}
				}catch(Exception e) {

				}
				try {
					if (score <10) {
						scorePrint.setX(player.getX()+10);
						scorePrint.setY(player.getY()-20);
					}
					else if (score >= 10 && score < 100) {
						scorePrint.setX(player.getX());
						scorePrint.setY(player.getY()-20);
					}
					else if (score >=100  && score < 1000) {
						scorePrint.setX(player.getX()-10);
						scorePrint.setY(player.getY()-20);
					}
					else{
						scorePrint.setX(player.getX()-20);
						scorePrint.setY(player.getY()-20);
					}

				}catch(Exception e) {
				}
			}


		};
		timer.start();

		platform();

		return root;
	}

	public void dead(AnimationTimer timer) {
		int leaderboardPos = linear(leaderboardScore, score);
		timer.stop();
		scorePoints.stop();
		stage.setScene(sceneLeaderboard);
		//Display Number of Coins
		if(leaderboardPos == -1) {
		}
		else if (leaderboardPos > 4){
		}
		else {
			leaderboardScore.add(leaderboardPos, score);
			System.out.println("New Highscore! Please enter a 4 character name");
			for(int i = 0; i < 1;) {
				String name = sc.nextLine();
				if(name.length() > 4 || name.length() < 4) {
					System.out.println("Please Enter a valid name");
				}
				else {
					i++;
					leaderboardName.add(leaderboardPos, name);
				}
			}
		}
		for(int i = 0; i < 5; i++) {
			highScores [i]= new Text(Integer.toString(leaderboardScore.get(i)));
			highScoreNames [i]= new Text(leaderboardName.get(i));
			if (i==0) {
				highScores [i].setX(550);
				highScores [i].setY(200);
				highScoreNames [i].setX(170);
				highScoreNames [i].setY(200);
			}
			else {
				highScores [i].setX(550);
				highScores [i].setY(200 + 40*i);
				highScoreNames [i].setX(170);
				highScoreNames [i].setY(200 + 40*i);
			}
			font = new Font("Candara", 38);
			highScores [i].setFont(font);
			highScores [i].setFill(Color.BLACK);
			highScoreNames [i].setFont(font);
			highScoreNames [i].setFill(Color.BLACK);
			pane3.getChildren().addAll(highScores [i], highScoreNames[i]);
		}
		//save data from current session into file
		PrintStream fps;
		try {
			fps = new PrintStream(file);
			for(int j = 0; j < 5; j++) {
				fps.println(toString(leaderboardName.get(j),leaderboardScore.get(j)));
			}
			fps.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Leaderboards and score here
		numCoins = 0;
		canJump = true;
		doubleJump = true;
		jump = 26;//Changes the jump height
		score = 0;
		isDead = false;
		counter = 0;
		group.getChildren().remove(scorePrint);
		backgrounds.clear();
		//Make a button, when pressed runs the code underneath
	}

	private void platform() {
		if((int)(Math.random() * 1000) <= 15) {
			p = new GamePlatform(800, (int)(Math.random() * 8 + 7) * 30, (int)(Math.random() * 500) + 100, 7, "platform", Color.DARKORANGE);
			platforms.add(p);
			root.getChildren().add(p);
		}
		if(platforms.isEmpty()) {
		}
		else if(platforms.get(platforms.size() - 1).getX() <= 300) {
			p = new GamePlatform(800, 400, (int)(Math.random() * 300) + 100, 7, "platform", Color.DARKORANGE);
			platforms.add(p);
			root.getChildren().add(p);
		}


	}

	private void coins() {
		try {
			coin = new ImagePattern(new Image (new FileInputStream ("Resources/Coin 10FPS.gif")));
		}catch (FileNotFoundException e) {

		}

		if((int)(Math.random() * 1000) <= 12) {
			c = new Coins(800, (int)(Math.random() * 10 + 9) * 20, 40, 40, "coin", coin);
			coins.add(c);
			root.getChildren().add(c);
		}

	}

	public static void background() {
		counter++;
		if(counter == 1) {
			GameBackground b = new GameBackground(1723, 0, 3724, 608, "background", backgroundImages[0]);
			b.setFill(backgroundImages[0]);
			backgrounds.add(b);
			root.getChildren().add(0, b);
		}
		if(counter == 2) {
			GameBackground b = new GameBackground(1723, 0, 3724, 608, "background", backgroundImages[1]);
			b.setFill(backgroundImages[1]);
			backgrounds.add(b);
			root.getChildren().add(0, b);
		}
		else{
			GameBackground b = new GameBackground(1710, 0, 3724, 608, "background", backgroundImages[2]);
			b.setFill(backgroundImages[2]);
			backgrounds.add(b);
			root.getChildren().add(0, b);
		}
	}

	public void start (Stage mainWindow) throws Exception {
		stage = mainWindow;

		//load data from file
		try {
			Scanner fscan = new Scanner(file);
			for(int i = 0; i < 5; i++) {
				String[] arr = fscan.nextLine().split("SPLIT");
				leaderboardName.add(arr[0]);
				leaderboardScore.add(Integer.parseInt(arr[1]));
			}	
			fscan.close();
		}
		catch (FileNotFoundException e) {
			System.err.print("Could not find file");
		}	
		catch (NoSuchElementException e) {
			System.err.print("File is empty");
		}

		Button game = new Button();	
		game.setStyle("-fx-background-image: url('main/startaButton.png')");
		game.setMinSize(190,49);

		Button settings = new Button();	
		settings.setStyle("-fx-background-image: url('main/settingsButton.png')");
		settings.setMinSize(190,49);

		Button exit = new Button();	
		exit.setStyle("-fx-background-image: url('main/exitButton.png')");
		exit.setMinSize(190,49);


		//CREATES BUTTON MENU
		//Creates new gridpane and adds the buttons to the pane
		GridPane menu = new GridPane();
		menu.add(game, 0, 0);
		menu.add(settings, 0, 1);
		menu.add(exit, 0, 2);
		//Sets each buttons alignment on the screen
		GridPane.setHalignment(game,HPos.RIGHT);
		GridPane.setValignment(game,VPos.BOTTOM);
		GridPane.setHalignment(settings,HPos.RIGHT);
		GridPane.setValignment(settings,VPos.CENTER);
		GridPane.setHalignment(exit,HPos.RIGHT);
		GridPane.setValignment(exit,VPos.TOP);
		//Creates columns to put the buttons in
		ColumnConstraints column1 = new ColumnConstraints();
		ColumnConstraints column2 = new ColumnConstraints();
		//Sets the space that each button occupy
		column1.setPercentWidth(95);
		column1.setHgrow(Priority.ALWAYS);
		column2.setPercentWidth(5);
		column2.setHgrow(Priority.ALWAYS);
		menu.getColumnConstraints().addAll(column1,column2);
		RowConstraints row1 = new RowConstraints(), row2 = new RowConstraints(), row3 = new RowConstraints();
		row1.setPercentHeight(20);
		row2.setPercentHeight(15);
		row3.setPercentHeight(70);
		row1.setVgrow(Priority.ALWAYS);
		row2.setVgrow(Priority.ALWAYS);
		row3.setVgrow(Priority.ALWAYS);
		menu.getRowConstraints().addAll(row1,row2,row3);

		//MENU BACKGROUND
		Image image1 = null;
		try {
			image1 = new Image (new FileInputStream ("Resources/RRB.png"));
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		//Sets size of background area
		BackgroundSize backSize = new BackgroundSize(820, 520, false, false, false, false);
		//Creates the background image
		BackgroundImage backImage= new BackgroundImage(image1, BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT,
				BackgroundPosition.DEFAULT,
				backSize);
		Background background = new Background(backImage);

		//SETTINGS BACKGROUND
		Image image2 = null;
		try {
			image2 = new Image (new FileInputStream ("Resources/Settings Background(1).png"));
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		//Sets size of background area
		BackgroundSize backSize2 = new BackgroundSize(800, 500, false, false, false, false);
		//Creates the background image
		BackgroundImage backImage2= new BackgroundImage(image2, BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT,
				BackgroundPosition.DEFAULT,
				backSize2);
		Background background2 = new Background(backImage2);

		//LEADERBOARD BACKGROUND
		Image image3 = null;
		try {
			image3 = new Image (new FileInputStream ("Resources/LeaderboardBackground.png"));
		} catch (FileNotFoundException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		//Sets size of background area
		BackgroundSize backSize3 = new BackgroundSize(800, 600, false, false, false, false);
		//Creates the background image
		BackgroundImage backImage3= new BackgroundImage(image3, BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT,
				BackgroundPosition.DEFAULT,
				backSize3);
		Background background4 = new Background(backImage3);

		//CREATES MENU
		BorderPane pane = new BorderPane();
		pane.setCenter(menu);
		pane.setBackground(background);

		//CREATES SETTINGS
		BorderPane pane2 = new BorderPane();
		pane2.setBackground(background2);

		group.getChildren().add(0, root);

		//LEADERBOARD MENU
		pane3.setBackground(background4);


		sceneMainMenu = new Scene(pane,800,500);
		sceneSettings = new Scene(pane2,800,500);
		scene = new Scene(group,800,500);
		sceneLeaderboard = new Scene(pane3,800,500);

		//Changes scene on button cick
		game.setOnAction(e -> {
			try {
				GamePlatform s = new GamePlatform(0, 340, 800, 5,  "platform", Color.BLACK);
				GamePlatform s2 = new GamePlatform(400, 240, 500, 5, "platform", Color.BLACK);
				initGame();
				GameBackground background3 = new GameBackground(0, 0, 3724, 608, "background", backgroundImages[0]);
				root.getChildren().add(background3);
				platforms.add(s);
				platforms.add(s2);
				root.getChildren().addAll(s, s2);
				try {
					playerRun = new ImagePattern(new Image (new FileInputStream ("Resources/Walking15.gif")));
					playerJump = new ImagePattern(new Image (new FileInputStream ("Resources/JumpAnimation (1).gif")));
					backgroundImages[0] = new ImagePattern(new Image (new FileInputStream ("Resources/BachgroundGoodPiece.png")));
					backgroundImages[1] = new ImagePattern(new Image (new FileInputStream ("Resources/Transition.png")));
					backgroundImages[2] = new ImagePattern(new Image (new FileInputStream ("Resources/BadBackgroundGood.png")));
				}catch (FileNotFoundException f) {

				}
				jumpButton = KeyCode.ALT;



				scene.setOnKeyPressed(f -> {
					if(f.getCode() == KeyCode.W || f.getCode() == KeyCode.UP || f.getCode() == KeyCode.SPACE) {
						jumpButton = KeyCode.SPACE;
					}
				});

				sceneLeaderboard.setOnKeyPressed(f -> {
					if(f.getCode() == KeyCode.W || f.getCode() == KeyCode.UP || f.getCode() == KeyCode.SPACE) {
						mainWindow.setScene(sceneMainMenu);
						for(int i = 0; i < 5; i++) {
							pane3.getChildren().removeAll(highScores[i], highScoreNames[i]);
						}
					}
				});
				background3.setFill(backgroundImages[0]);
				backgrounds.add(background3);
				player = new Player(300, 280, 40, 60, "player", playerRun);
				root.getChildren().add(player);
				mainWindow.setScene(scene);
				mainWindow.show();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

		settings.setOnAction(e -> mainWindow.setScene(sceneSettings));
		exit.setOnAction(e ->Platform.exit());

		mainWindow.setTitle("Rooftop Renegade");
		mainWindow.setResizable(false);	

		mainWindow.setScene(sceneMainMenu);
		mainWindow.show();
	}

	public static int linear(ArrayList<Integer> arr, int target) {
		for(int i = 0; i < arr.size(); i++) {
			if(target > arr.get(i)) {
				return i;
			}
		}
		return -1;
	}

	private String toString(String name, int score) {
		String scoreSave = name + "SPLIT" + score;
		return scoreSave;
	}

	public static void main(String[] args) {
		launch(args);

	}
}
