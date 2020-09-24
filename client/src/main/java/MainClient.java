import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainClient extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
//        Parent root = loader.load(getClass().getResourceAsStream("resources/sample.fxml"));

        primaryStage.setTitle("Chat");
        primaryStage.setScene(new Scene(root, 350, 375));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            Controller.endConnection();
            Platform.exit();
        });

    }
    public static void main(String[] args) {
        launch(args);
    }
}
