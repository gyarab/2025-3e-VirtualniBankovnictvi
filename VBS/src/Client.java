import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        Scanner input =  new Scanner(System.in);

        System.out.print("Write your username: ");
        String username = input.nextLine();
        while(true) {
            System.out.print("Write your message: ");
            String message = input.nextLine();
            if (message.equals("exit")) {
                break;
            }
            SendMessage(username + ": " + message);
        }
    }

    public static void SendMessage(String message) throws IOException {
        Socket socket = new Socket("localhost", 9999);
        DataOutputStream data = new DataOutputStream(socket.getOutputStream());
        data.writeUTF(message);
        data.flush();
        data.close();
        socket.close();
    }
}
