public class FileEncoder {
    String fileToCompress = "encodeMe.txt";
    String compressedFileName;
    FileHandling inputFileHandler;
    FileHandling outputFileHandler;
    HuffmanTree huffmanTree;

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
        if (!startCompression()) {
            System.out.println("There was a problem compressing "+this.fileToCompress);
            return false;
        }

        System.out.println("File compressed -->> "+this.compressedFileName);
        return true;
    }

    boolean startCompression() {
        int nextChar;

        while ((nextChar = Integer.valueOf(this.inputFileHandler.getNextCharacter())) != -1) {
            String charCode = this.huffmanTree.addCharAndGetCode(nextChar);
            this.outputFileHandler.storeBitsToWrite(charCode);
        }

        this.outputFileHandler.writeBytesToFile();

        this.outputFileHandler.closeOutputWriter();

        return true;
    }
}
