import java.lang.*;
import java.io.*;

class menu {
    // Modify the display content to suit your purposes...
    private static final String TITLE =
                    "\n2010226 coursework \n" +
                    "by kai-WADDINGTON\n\n" +
                    "\t********************\n" +
                    "\t1. Encode \"encodeMe.txt\" -->> \"encodeMe.kht\" \n" +
                    "\t2. Decode \"encodeMe.kht\" -->> \"encodeMe_decoded.txt\" \n" +
                    "\t0. Exit \n" +
                    "\t********************\n" +
                    "Please input a single digit (0-2):\n";

    private menu() {
        int selected = -1;
        while (selected != 0) {
            System.out.println(TITLE);
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            try {
                selected = Integer.parseInt(in.readLine());
                switch (selected) {
                    case 1:
                        q1();
                        break;
                    case 2:
                        q2();
                        break;
                }
            } catch (Exception ex) {
                System.out.println("Error: invalid choice.");
            }
        }
        System.out.println("Bye!");
    }

    private void q1() {
        // Modify the types of the methods to suit your purposes...
        System.out.println("Encoding...");
        Kl.reset();
        // Encode
        new Entry("e");
    }

    private void q2() {
        System.out.println("Decoding...");
        Kl.reset();
        // Decode
        new Entry("d");
    }

    public static void main(String[] args) {
        new menu();
    }
}