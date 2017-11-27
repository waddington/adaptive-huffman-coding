class Entry {
// My original start of the program
// I now just provide a value to this from menu.java

    Entry(String choice) {
        init(choice);
    }

    private void init(String userChoice) {
        // Either encode/decode depending on user's choice
        switch (userChoice) {
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

        Kl.op();
        System.out.println("Program closing.");
    }
}
