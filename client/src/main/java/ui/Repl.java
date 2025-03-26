package ui;

import java.util.Scanner;
import static com.sun.org.apache.xalan.internal.xsltc.compiler.Constants.RESET;
import static java.awt.Color.*;

public class Repl {

    private final ChessClient client;

    public Repl(String serverurl) {
       client = new ChessClient(serverurl);

    }

    public void run() {
        System.out.println("Welcome to the best game ever: CHESS");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            //printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + GREEN);
    }

}
