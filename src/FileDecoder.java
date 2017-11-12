import java.util.ArrayList;
import java.util.LinkedList;

public class  FileDecoder {
    String fileToDecode = "encodeMe.kht";
    String outputFileName;
    FileHandling inputFileHandler;
    FileHandling outputFileHandler;
    HuffmanTree huffmanTree;

    FileDecoder() {
        this.outputFileName = (fileToDecode.split("\\.")[0])+"_decoded.txt";
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
        if (!startDecompression()) {
            System.out.println("There was a problem compressing "+this.fileToDecode);
            return false;
        }

        System.out.println("File decompressed -->> "+this.outputFileName);
        return true;
    }

    boolean startDecompression() {
        ArrayList<Integer> decodingBuffer = new ArrayList<>();
        boolean reachedEndOfEncodedFile = false;
        LimitedQueue<String> bitsQueue = new LimitedQueue<>(10);

        while (true) {
            if (!reachedEndOfEncodedFile) {
                while (decodingBuffer.size() <= 100) {
                    // Get the next byte
                    String nextByteAsString = this.inputFileHandler.getNextCharacter();
                    // If the byte equals -1, the end, then break
                    if (nextByteAsString.equals("-1")) {
                        reachedEndOfEncodedFile = true;
                        // Go backwards through the buffer and count how many bits are all 1s
                        int allOnesBits = 0;
                        for (int i=bitsQueue.size()-1; i>=0; i--) {
                            // count how many are all 1's
                            // remove that many -1  from the decoding buffer
                            String bit = bitsQueue.pollLast();
                            if ("11111111".equals(bit))
                                allOnesBits++;
                        }

                        for (int i=0; i<((allOnesBits * 8) + (allOnesBits - 1)); i++)
                            decodingBuffer.remove(decodingBuffer.size()-1);
                        
                        break;
                    }

                    bitsQueue.add(nextByteAsString);

                    // Add the next byte to the buffer
                    for (int i=0; i<nextByteAsString.length(); i++) {
                        char charOfByte = nextByteAsString.charAt(i);
                        int charOfByteAsInt = Integer.parseInt(charOfByte+"");
                        decodingBuffer.add(charOfByteAsInt);
                    }
                }
            }

            if (decodingBuffer.size() > 0) {
                this.huffmanTree.takeNextBit(decodingBuffer.get(0));
                decodingBuffer.remove(0);

                if (this.huffmanTree.isCharacterMatchFound()) {
                    char currentCharacter = this.huffmanTree.getCurrentCharacter();
                    this.outputFileHandler.writeToFile(""+currentCharacter);
                    this.huffmanTree.addCharAndGetCode((int) currentCharacter);
                } else if (this.huffmanTree.didGetDag()) {
                    String asciiString = "";
                    for (int i=0; i<8; i++) {
                        if (decodingBuffer.size() > 0) {
                            asciiString += decodingBuffer.get(0);
                            decodingBuffer.remove(0);
                        }
                    }
                    int asciiStringToBase10Int = Integer.parseInt(asciiString, 2);
                    char theCharacter = (char) asciiStringToBase10Int;
                    this.outputFileHandler.writeToFile(""+theCharacter);
                    this.huffmanTree.addCharAndGetCode(asciiStringToBase10Int);
                }
            } else {
                break;
            }
        }

        this.outputFileHandler.closeOutputWriter(); // Close the writer

        return true;
    }

    public class LimitedQueue<E> extends LinkedList<E> {
        private int limit;

        public LimitedQueue(int limit) {
            this.limit = limit;
        }

        @Override
        public boolean add(E o) {
            super.add(o);
            while (size() > limit) { super.remove(); }
            return true;
        }
    }
}
