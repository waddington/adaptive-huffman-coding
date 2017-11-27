import java.io.File;

class FileEncoder {
    private final String fileToCompress = "encodeMe.txt";
    private final String compressedFileName;
    private final FileHandling inputFileHandler;
    private final FileHandling outputFileHandler;
    private final HuffmanTree huffmanTree;

    FileEncoder() {
        this.inputFileHandler = new FileHandling(Type.NORMAL); // Create an instance of our inputFile handling class
        this.outputFileHandler = new FileHandling(Type.ENCODING);
        this.huffmanTree = new HuffmanTree(Direction.ENCODING); // Create an instance of our huffman tree class
        this.compressedFileName = this.fileToCompress.split("\\.")[0]+".kht"; // Create the name of the output inputFile
    }

    boolean init() {
        // Check the input inputFile is valid
        if (!this.inputFileHandler.isFileValid(this.fileToCompress)) {
            System.out.println("There is a problem with "+this.fileToCompress);
            return false;
        }

        // Do the footwork to read the inputFile
        if (!this.inputFileHandler.createFileReaders()) {
            System.out.println("There is a problem with "+this.fileToCompress);
            return false;
        }

        // Prepare to output the compressed data
        if (!this.outputFileHandler.prepareOutput(this.compressedFileName)) {
            System.out.println("There is a problem with "+this.compressedFileName);
            return false;
        }

        // Start compressing the inputFile
        startCompression();

        // Display compression information
        displayCompressionInformation();

        System.out.println("File compressed -->> "+this.compressedFileName);
        return true;
    }

    private void startCompression() {
        // Print the original tree structure
        this.huffmanTree.printTree();

        // Getting the value of each character in the file
        int nextChar;
        while ((nextChar = Integer.valueOf(this.inputFileHandler.getNextCharacter())) != -1) {
            // Kl is for displaying the progress of the algorithm
            Kl.p("\"" +(char) nextChar + "\": ");

            // Getting the encoded code for this character and storing it to write to the file
            String charCode = this.huffmanTree.addCharAndGetCode(nextChar);
            this.outputFileHandler.storeBitsToWrite(charCode);

            // Displaying the progress of the algorithm
            Kl.ms();
            this.huffmanTree.printTree();
        }

        // Get the EOF code and send it to be written to the file
        String charCode = this.huffmanTree.getEofCode();
        this.outputFileHandler.storeBitsToWrite(charCode);
        Kl.pl("End-of-file: EOF("+charCode+")");

        // Write all of the bits to file
        this.outputFileHandler.writeBytesToFile();

        this.outputFileHandler.closeOutputWriter();
    }

    private void displayCompressionInformation() {
        long inputFileSize = new File(this.fileToCompress).length();
        long compressedFileSize = new File(this.compressedFileName).length();
        double compressionRatio = inputFileSize / compressedFileSize;
        double spaceSaved = (1 - ((double) compressedFileSize / (double) inputFileSize)) * 100;

        Kl.pl("");
        Kl.pl("Input file- " + humanReadableByteCount(inputFileSize, true));
        Kl.pl("Compressed file- " + humanReadableByteCount(compressedFileSize, true));
        Kl.pl("Compression Ratio- " + String.format("%.0f", compressionRatio) + ":1");
        Kl.pl("Space Saved- " + String.format("%.0f", spaceSaved) + "%");
    }

    // http://programming.guide/java/formatting-byte-size-to-human-readable-format.html
    private String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
