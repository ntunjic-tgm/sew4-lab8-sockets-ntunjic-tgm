package ntunjic;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Random;

public class HangmanServerConnection implements Runnable {
    private Socket client;
    private HangmanLogic log;
    public boolean stop;

    private List<String> words;
    private Random r;

    public HangmanServerConnection(Socket cl, List<String> words) {
        this.words = words;
        this.r = new Random();

        this.client = cl;
        this.log = new HangmanLogic(this.words.get(r.nextInt(this.words.size())));
        this.stop = false;
    }

    @Override
    public void run() {
        try {
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
            BufferedWriter toClient = new BufferedWriter(new OutputStreamWriter(this.client.getOutputStream()));
            while (!stop && this.log.getTries() > 0) {
                toClient.write("Word: ");
                toClient.write(this.log.getHiddenWord());
                toClient.write("\r\nRemaining tries: ");
                toClient.write(this.log.getTries() + "\r\n");
                toClient.write("-END-\r\n");
                toClient.flush();
                String input = fromClient.readLine();
                switch(input) {
                    case "!stop":
                        toClient.write("Stopping active session...\r\n");
                        toClient.flush();
                        this.stop = true;
                        continue;
                    case "!restart":
                        toClient.write("Restarting active session...\r\n");
                        toClient.write("The word was: ");
                        toClient.write(this.log.getWord());
                        toClient.write("\r\n");
                        toClient.flush();
                        this.log = new HangmanLogic(this.words.get(r.nextInt(this.words.size())));
                        break;
                    default:
                        input = input.toUpperCase();
                        if (input.length() == 1 && input.charAt(0) >= 'A' && input.charAt(0) <= 'Z') {
                            this.log.check(input.charAt(0));
                        } else {
                            toClient.write("Input is invalid. Try again.");
                        }
                }
            }
            if (!stop) {
                if (this.log.getTries() == 0) {
                    toClient.write("You lost!");
                } else {
                    toClient.write("You win!\r\n");
                }
            }
            toClient.write("The word was: ");
            toClient.write(this.log.getWord());
            toClient.write("\r\n");
            toClient.write("GAME OVER\r\n");
            toClient.flush();
            this.client.close();
        } catch (IOException e) {
            if (e instanceof SocketException) System.out.println("Connection reset or closed by client.");
        }
    }
}
