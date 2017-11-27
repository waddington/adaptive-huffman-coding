import java.util.ArrayList;

class Kl {
    static private ArrayList<String> toPrint;
    static private boolean instantiated = false;
    static private String tempString;

    static private void classCheck() {
        if (!instantiated) {
            toPrint = new ArrayList<>();
            tempString = "";
            instantiated = true;
        }
    }

    static void reset() {
        instantiated = false;
        toPrint = new ArrayList<>();
        tempString = "";
    }

    static void pl(String s) {
        classCheck();

        toPrint.add(s);
    }

    static void p(String s) {
        classCheck();

        tempString += s;
    }

    static void ms() {
        classCheck();

        toPrint.add(tempString);
        tempString = "";
    }

    static void op() {
        classCheck();

        String borderString = "*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*";
        System.out.println(borderString);
        for (String s: toPrint)
            System.out.println(s);
        System.out.println(borderString);

    }
}
