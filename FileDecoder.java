import java.util.ArrayDeque;

class  FileDecoder {
    private final String fileToDecode = "encodeMe.kht";
    private final String outputFileName;
    private final FileHandling inputFileHandler;
    private final FileHandling outputFileHandler;
    private final HuffmanTree huffmanTree;

    FileDecoder() {
        this.outputFileName = fileToDecode.split("\\.")[0] +"_decoded.txt";
        this.inputFileHandler = new FileHandling(Type.DECODING);
        this.outputFileHandler = new FileHandling(Type.NORMAL);
        this.huffmanTree = new HuffmanTree(Direction.DECODING);
    }

    boolean init() {
        // Check the input inputFile is valid
        if (!this.inputFileHandler.isFileValid(this.fileToDecode)) {
            System.out.println("There is a problem with "+this.fileToDecode);
            return false;
        }

        // Do the footwork to read the inputFile
        if (!this.inputFileHandler.createFileReaders()) {
            System.out.println("There is a problem with "+this.fileToDecode);
            return false;
        }

        // Prepare to output the decompressed data
        if (!this.outputFileHandler.prepareOutput(this.outputFileName)) {
            System.out.println("There is a problem with "+this.outputFileName);
            return false;
        }

        // Start decompressing the inputFile
        startDecompression();

        System.out.println("File decompressed -->> "+this.outputFileName);
        return true;
    }

    private void startDecompression() {
        // Printing the tree
        this.huffmanTree.printTree();
        ArrayDeque<Integer> decodingBuffer = new ArrayDeque<>(); // Using an ArrayDeque because it is fast
        boolean reachedEndOfEncodedFile = false;
        boolean gotEof = false;

        // Loop until we reach the end of the file
        while (!gotEof) {
            // If not at end of encoded file
            if (!reachedEndOfEncodedFile) {
                // Ensure we have 10 bits available in the buffer
                while (decodingBuffer.size() <= 10) {
                    // Get the next byte
                    String nextByteAsString = this.inputFileHandler.getNextCharacter();
                    // If the byte equals -1, the end, then break
                    if (nextByteAsString.equals("-1")) {
                        reachedEndOfEncodedFile = true;
                        break;
                    }

                    // Add the next byte to the buffer as individual bits
                    for (int i=0; i<nextByteAsString.length(); i++) {
                        char charOfByte = nextByteAsString.charAt(i);
                        int charOfByteAsInt = Integer.parseInt(charOfByte+"");
                        decodingBuffer.add(charOfByteAsInt);
                    }
                }
            }

            // If there are bits available
            if (decodingBuffer.size() > 0) {
                // Give the bit to the Huffman tree so that it can traverse the tree correctly
                this.huffmanTree.takeNextBit(decodingBuffer.poll());

                // If we got a character from the tree
                if (this.huffmanTree.isCharacterMatchFound()) {
                    // Get the character
                    char currentCharacter = this.huffmanTree.getCurrentCharacter();
                    // Write the character
                    this.outputFileHandler.writeToFile(""+currentCharacter);
                    Kl.ms();
                    // Update the tree
                    this.huffmanTree.addCharAndGetCode((int) currentCharacter);
                    this.huffmanTree.printTree();
                } else if (this.huffmanTree.didGetDag()) {
                    // If got dagger we need to get the next 8 bits from the buffer
                    StringBuilder asciiString = new StringBuilder();
                    for (int i=0; i<8; i++)
                        if (decodingBuffer.size() > 0)
                            asciiString.append(decodingBuffer.poll());
                    Kl.p(asciiString.toString());
                    // Convert the binary to an ascii character
                    int asciiStringToBase10Int = Integer.parseInt(asciiString.toString(), 2);
                    char theCharacter = (char) asciiStringToBase10Int;
                    this.outputFileHandler.writeToFile(""+theCharacter);
                    Kl.p("(\""+theCharacter+"\")");
                    Kl.ms();
                    // Update the tree
                    this.huffmanTree.addCharAndGetCode(asciiStringToBase10Int);
                    this.huffmanTree.printTree();
                } else if (this.huffmanTree.didGetEof()) {
                    // If we reached the end of the file, set a flag so the loop stops
                    gotEof = true;
                }
            } else {
                // If the buffer is empty stop
                break;
            }
        }

        this.outputFileHandler.closeOutputWriter(); // Close the writer
    }
}
