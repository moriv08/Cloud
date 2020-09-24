import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class Controller implements Initializable {

    public ListView list;

    @FXML
    TextField textField;
    @FXML
    Button btn1;
    @FXML
    HBox bottomPanel;
    @FXML
    HBox upperPanel;
    @FXML
    TextField loginField;
    @FXML
    PasswordField passwordField;

    public Socket socket = null;
    private static DataInputStream in;
    private static DataOutputStream out;

    public static void endConnection(){
        try {
            out.writeUTF("end");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg() throws IOException{

        out.writeUTF(textField.getText());
        out.flush();
        textField.clear();
        textField.requestFocus();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

//            new Task<String>(){
//                @Override
//                protected String call() throws Exception {
//                    return in.readUTF();
//                }
//
//                @Override
//                protected void succeeded() {
//                    try {
//                        list.getItems().add(get());
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } catch (ExecutionException e) {
//                        e.printStackTrace();
//                    }
//                }
//            };

            new Thread(()->{
                while(true) {
                    try {
                        String msg = in.readUTF();

                        Platform.runLater(()->list.getItems().add(msg));

                        if (msg.equals("end")) break;

//                        textArea.appendText(msg + " ");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}