import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final DataInputStream in;
    private final DataOutputStream out;
    private final ChatServer server;
    private final Socket socket;
    private static int counter = 0;
    private final String name;

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

    public void run() {
        while (true){
            try {
                String msg = in.readUTF();
                System.out.println("message from " + name +": " + msg);
                server.broadcast(msg);

                if (msg.equals("end")) {
                    server.remove(this);

                    in.close();
                    out.close();
                    socket.close();

                    System.out.println("Client " + name + " disconnected");
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void sendMsg(String msg) throws IOException{
        out.writeUTF(msg);
        out.flush();
    }
}
