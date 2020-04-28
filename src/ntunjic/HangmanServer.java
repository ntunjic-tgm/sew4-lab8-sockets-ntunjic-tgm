package ntunjic;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

public class HangmanServer {
    private ServerSocket servS;
    private int portI = 107; //Portnummer

    private List<String> words;

    public HangmanServer(String port) throws IOException {
        try {
            portI = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            System.out.println("Invalid port specified. Using default port (107).");
        }
        this.words = Files.readAllLines(Paths.get("dictionary.txt"));
        this.servS = new ServerSocket(portI); //erstellt einen ServerSocket
    }

    public void start() {
        Random r = new Random();
        System.out.println("Listening on port " + this.portI);
        do {
            try {
                Socket client = servS.accept(); // Mit der accept()-Methode auf neue Verbindungen warten.
                System.out.println("Connection established with " + client.getInetAddress());
                Thread t = new Thread(new HangmanServerConnection(client, this.words));
                t.start();
            } catch (IOException e) {
                System.out.println("An IO issue has occurred. Was the connection closed?. Details:");
                e.printStackTrace(); //Wirft Error aus
                this.close();
            }
        } while(true);
    }

    public void close() {
        try {
            this.servS.close();
        } catch (IOException e) {
            System.out.println("An IO issue has occurred during connection closure. Details:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            HangmanServer hs = new HangmanServer((args.length >= 1)?args[0]:"");
            hs.start();
        } catch (IOException e) {
            System.out.println("Failed to start server.");
            e.printStackTrace();
        }

    }
}
