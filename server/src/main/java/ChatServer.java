
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatServer {
    private ServerSocket server;
    private ConcurrentLinkedQueue<ClientHandler> queue;
    private boolean isRunning = true;

    public static void main(String[] args) {
        new ChatServer();
    }

    public void stop(){
        isRunning = false;
    }

    public ChatServer(){
        try {
            queue = new ConcurrentLinkedQueue<>();
            server = new ServerSocket(8189);
            System.out.println("Server started on " + server.getLocalPort());

            while (isRunning){
                Socket socket = server.accept();
                ClientHandler client = new ClientHandler(socket, this);
                queue.add(client);
                new Thread(client).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
//    public void broadcast(String msg) throws IOException{
//        for (ClientHandler client: queue) {
//            client.sendMsg(msg);
//        }
//    }

    public void remove(ClientHandler client) {
        queue.remove(client);
    }
}
