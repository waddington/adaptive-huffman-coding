import java.util.Scanner;

public class Main {

    private void init() {
        Scanner scanner = new Scanner(System.in);
        String choice = "";
        boolean validChoice = false;

        // Ask user whether we are encoding or decoding a inputFile
        System.out.print("Are you encoding (e) or decoding (d) a inputFile (e/d)? ");

        while (!validChoice) {
            choice = scanner.next();

            if ("e".equals(choice) || "d".equals(choice))
                validChoice = true;
            else
                System.out.print("Invalid choice. Please enter (e/d): ");

        }

        scanner.close();

        // Either encode/decode depending on users' choice
        switch (choice) {
            case "e": {
                closeProgram(new FileEncoder().init());
                break;
            }
            case "d": {
                closeProgram(new FileDecoder().init());
                break;
            }
            default: {
                closeProgram(false);
                break;
            }
        }
    }

    private void closeProgram(boolean success) {
        if (!success)
            System.out.println("\r\nSorry, the task could not be completed at this time.");

        System.out.println("Program closing.");
    }

    public static void main(String[] args) {
	    Main mn = new Main();
        mn.init();
    }
}
