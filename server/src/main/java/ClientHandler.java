import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientHandler implements Runnable {

    private final DataInputStream in;
    private final DataOutputStream out;
    private final ChatServer server;
    private final Socket socket;
    private static int counter = 0;
    private final String name;
    private String serverStore = "server/ServerStorage/";

    File listFile = new File(serverStore);
    File allFiles[] = listFile.listFiles();
    String[] files = new String[allFiles.length];



    public ClientHandler(Socket socket, ChatServer server) throws IOException {
        this.server = server;
        this.socket = socket;
        counter++;
        name = "user_" + counter;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        System.out.println("Client handler handling: ip = " + socket.getInetAddress());
        System.out.println("Nick: " + name);
    }

//    public void sendMsg(String msg) throws IOException{
//        out.writeUTF(msg);
//        out.flush();
//    }

    public void run() {
        try {
            out.writeUTF("Connected to server... ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true){
            try {
                String msg = in.readUTF();
//                System.out.println("message from " + name +": " + msg);
//                server.broadcast(msg);
                if (msg.equals("/end")) {
//                    server.remove(this);

                    out.writeUTF(name + " disconnected");

                    Thread.sleep(1000);

                    in.close();
                    out.close();
                    socket.close();

                    System.out.println("Client " + name + " disconnected");
                    break;
                }

                if (msg.equals("/upload")){

                    String fileName = in.readUTF();
                    long fileLength = in.readLong();

                    File file = new File(serverStore + fileName);
                    file.createNewFile();

                    try(FileOutputStream copiedFile = new FileOutputStream(file)){

                        int count;
                        byte[] buffer = new byte[256];

//                        if (fileLength < 256)
//                            fileLength += 256;

                        for (int i = 0; i <= fileLength / 256; i++) {
                            count = in.read(buffer);
                            copiedFile.write(buffer, 0, count);
                        }

                        if (file.length() == fileLength) {
                            out.writeUTF("Ok. File no the server.");
                        }
                        else{
                            out.writeUTF("Something went wrong( server took only " + file.length() + " bytes from " + fileLength);
                        }
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
                else if (msg.equals("/download")){

                    String fileName = in.readUTF();
                    File file = new File(serverStore + fileName);

                    if (file.exists()){

                        out.writeUTF("ok");

                        try (FileInputStream fromServerFile= new FileInputStream(file)) {

                            out.writeLong(file.length());

                            int count;
                            byte[] buffer = new byte[256];
                            while ((count = fromServerFile.read(buffer)) != -1)
                                out.write(buffer, 0, count);

                            out.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        out.writeUTF("file not found");
                        out.flush();
                    }
                }
                else if (msg.equals("/show")){

                    String s = "";

                    for(int i = 0; i < files.length; i++)
                        s += allFiles[i].getName() + "\n";

                    out.writeUTF(s);
                    out.flush();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}
