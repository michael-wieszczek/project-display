/**
 * 
 */
package main;

import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * @author mimlo
 *
 */
public class MainApplication extends Application {


	private Pane root = new Pane();

	private Player player = new Player(300, 280, 40, 60, "player", Color.BLUEVIOLET);
	ArrayList<Platform> platforms = new ArrayList<Platform>();
	Platform s = new Platform(0, 340, 800, 5, 340, "platform", Color.BLACK);
	Platform s2 = new Platform(400, 240, 500, 5, 240, "platform", Color.BLACK);
	private Point2D playerVelocity = new Point2D(0, 0);
	private boolean canJump = true;
	private int jump = 25;//Changes the jump height

	private Parent initGame() {
		root.setPrefSize(800, 600);

		root.getChildren().add(player);

		AnimationTimer timer = new AnimationTimer() {

			public void handle(long now) {
				//See's if player is on a platform, and takes gravity into account
				if(player.getBoundsInParent().intersects(s.getBoundsInParent()) || player.getBoundsInParent().intersects(s2.getBoundsInParent())) {
					canJump = true;
				}
				else {
					player.gravity();
				}
				if(canJump == true) {
					//Need to make it when up or space is inputted this will work.
					if(jump >= 0) {
						player.jump(jump);
						jump--;
					}
					else {
						canJump = false;
						jump = 25;
					}
				}
					
				
			}
		};

		timer.start();

		platform();

		return root;
	}

	private void platform() {

		root.getChildren().addAll(s, s2);
	}


	public void start (Stage stage) throws Exception {
		Scene scene = new Scene(initGame());



		scene.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.W || e.getCode() == KeyCode.UP) {
				
			}
			else if(e.getCode() == KeyCode.S || e.getCode() == KeyCode.DOWN) {
				if(player.getBoundsInParent().intersects(s.getBoundsInParent()) || player.getBoundsInParent().intersects(s2.getBoundsInParent())) {
					boolean canJump = true;
				}
				else {
					player.gravity();
				}

			}

		});
		stage.setScene(scene);
		stage.show();

	}


	public static void main(String[] args) {
		launch(args);
	}
}