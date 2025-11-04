import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        System.out.println("Server starting...");
        ServerSocket server = new ServerSocket(9999);
        System.out.println("Server listening on port 9999");

        while (true) {
            Socket socket = server.accept();
            System.out.println(new DataInputStream(socket.getInputStream()).readUTF());
            socket.close();
        }
    }
}
