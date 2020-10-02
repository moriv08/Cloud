import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public ListView list;
    @FXML
    TextArea serverMsg;

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
    private static String clientStore = "client/ClientStorage/";

    public static void endConnection(){
        try {
            out.writeUTF("end");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(){

        String message = textField.getText();

        if (message.equals("/end")){
            try {
                out.writeUTF("/end");
                out.flush();
                textField.setText(in.readUTF());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String[] tokens = message.split(" ");

        if (tokens.length == 2) {
            String command = tokens[0];
            String fileName = tokens[1];

            if (command.equals("/upload")) {
//                uploadFile(command, fileName);

                File file = new File(clientStore + fileName);

                try (FileInputStream copyFile = new FileInputStream(file)) {
                    out.writeUTF(command);
                    out.writeUTF(fileName);
                    out.writeLong(file.length());

                    int count;
                    byte[] buffer = new byte[256];
                    while ((count = copyFile.read(buffer)) != -1)
                        out.write(buffer, 0, count);

                    out.flush();

                    serverMsg.appendText(new SimpleDateFormat("dd.MM HH:mm:ss ").format(Calendar.getInstance().getTime()) + "The file was sent to the server" + '\n');

                    String respons = in.readUTF();
                    serverMsg.appendText(new SimpleDateFormat("dd.MM HH:mm:ss ").format(Calendar.getInstance().getTime()) + respons);

                } catch (FileNotFoundException e){
                    serverMsg.appendText(new SimpleDateFormat("dd.MM HH:mm:ss ").format(Calendar.getInstance().getTime()) + "There are no such file on you file store" + '\n');
                    textField.clear();
//                    sendMsg();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (command.equals("/download")) {
//                downloadFile(command, fileName);

                try {
                    out.writeUTF(command);
                    out.writeUTF(fileName);

                    String respons = in.readUTF();

                    if (respons.equals("ok")) {

                        long fileLength = in.readLong();

                        File file = new File(clientStore + fileName);
                        file.createNewFile();

                        try (FileOutputStream fileFromServer = new FileOutputStream(file)) {

//                            if (fileLength < 256)
//                                fileLength += 256;

                            int count;
                            byte[] buffer = new byte[256];

                            for (int i = 0; i <= fileLength / 256; i++) {
                                count = in.read(buffer);
                                fileFromServer.write(buffer, 0, count);
                            }

                            if (file.length() == fileLength){
                                serverMsg.appendText(new SimpleDateFormat("dd.MM HH:mm:ss ").format(Calendar.getInstance().getTime()) + "The file was successfully uploaded from the server" + '\n');
//                                System.out.println("File sucsessfuly downloaded from server");
                            }else {
                                serverMsg.appendText(new SimpleDateFormat("dd.MM HH:mm:ss ").format(Calendar.getInstance().getTime()) + "Something went wrong. the file was not uploaded" + '\n');
//                                System.out.println("Something went wrong. File did not downloaded");
                            }
                        }
                    } else {
                        serverMsg.appendText(new SimpleDateFormat("dd.MM HH:mm:ss ").format(Calendar.getInstance().getTime()) + "file not found" + '\n');
//                        textField.setText("file not found");
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (command.equals("/show") && fileName.equals("files")){

                String files = new SimpleDateFormat("dd.MM HH:mm:ss ").format(Calendar.getInstance().getTime()) + '\n';

                try {
                    out.writeUTF("/show");
                    out.flush();
                    files += in.readUTF();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                serverMsg.appendText(files);
            }
            else {
                serverMsg.appendText(new SimpleDateFormat("dd.MM HH:mm:ss ").format(Calendar.getInstance().getTime()) + "Unknown command " + message + '\n');
            }
        }
        else {
            serverMsg.appendText(new SimpleDateFormat("dd.MM HH:mm:ss ").format(Calendar.getInstance().getTime()) + message + '\n');
        }

        textField.clear();
        textField.requestFocus();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        printFiles();

        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            String serverAnsver = in.readUTF();
            if (!(serverAnsver.equals(null)))
                serverMsg.appendText(new SimpleDateFormat("dd.MM HH:mm:ss ").format(Calendar.getInstance().getTime()) + serverAnsver + '\n');

//            new Task<String>(){
//                @Override
//                protected String call() throws Exception {
//                    return in.readUTF();
//                }

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

//            new Thread(()->{
//                while(true) {
//                    try {
//
//                        String msg = in.readUTF();
//
//                        Platform.runLater(()->list.getItems().add(msg));
//
//                        if (msg.equals("end")) break;
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printFiles(){

        File files = new File(clientStore);
        for (File f: files.listFiles()) {

            String del = "_";

            if (f.getName().length() < 20)
                for (int i = 0; i < (20 - f.getName().length()); i++)
                    del += "_";

            list.getItems().add(f.getName() + del + "|" + del + f.length() + " bytes");
        }
    }

//    private void uploadFile(String command, String fileName){
//
//        File file = new File(clientStore + fileName);
//
//        try (FileInputStream copyFile = new FileInputStream(file)) {
//            out.writeUTF(command);
//            out.writeUTF(fileName);
//            out.writeLong(file.length());
//
//            int count;
//            byte[] buffer = new byte[256];
//            while ((count = copyFile.read()) != -1){
//                out.write(buffer, 0, count);
//            }
//            if (count == -1){
//                count = 0;
//                out.writeInt(-1);
//            }
//            out.flush();
//
//            System.out.println("File went on server");
//            String respons = in.readUTF();
//            System.out.println(respons);
//
////                    textField.setText("File uploaded.");
//
//        } catch (FileNotFoundException e){
//            System.err.println("There are no such file on you file store");
//            textField.clear();
//            sendMsg();
//        }catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    private void downloadFile(String command, String fileName){
//        try {
//            out.writeUTF(command);
//            out.writeUTF(fileName);
//
//            String respons = in.readUTF();
//
//            if (respons.equals("ok")) {
//
//                long fileLength = in.readLong();
//
//                File file = new File(clientStore + fileName);
//                file.createNewFile();
//
//                try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
//
//                    byte[] buffer = new byte[256];
//
//                    if (fileLength < 256)
//                        fileLength += 256;
//
//                    int read;
//                    for (int i = 0; i < fileLength / 256; i++) {
//                        read = in.read(buffer);
//                        fileOutputStream.write(buffer, 0, read);
//                    }
//                }
//
//            } else {
//                textField.setText("file not found");
//            }
//        }catch (FileNotFoundException e){
//            System.err.println("There are no such file on you file store");
//            textField.clear();
//            sendMsg();
//        }catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}