package FlashGet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/*
* Main class of Multi-thread Downloader
* @author Jakkrathorn Srisawad
* */
public class FlashGetMain extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("flashget-UI.fxml"));
        primaryStage.setTitle("FlashGet-Bouncyyahomie");
        Image icon = new Image(getClass().getResourceAsStream("/image/brainny.png"));
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(new Scene(root, 680, 285));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
