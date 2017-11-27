import java.io.*;
import java.util.ArrayList;

// So that I know the use of this class
enum Type {
    NORMAL,
    ENCODING,
    DECODING
}

class FileHandling {
    private final Type type;
    private File inputFile;
    private InputStream inputInStream;
    private Reader inputBuffer;
    private PrintWriter outputWriter;
    private OutputStream outputStream;
    private ArrayList<Integer> byteBuffer;

    FileHandling(Type type) {
        this.type = type;
    }

    // Check if a inputFile is valid
    boolean isFileValid(String filename) {
        File file = new File(filename);

        // Check if inputFile is valid
        // https://stackoverflow.com/a/1816676/3259361
        if ((file.exists() && !file.isDirectory())) {
            this.inputFile = file;
            return true;
        }

        return false;
    }

    // Footwork to get up and running
    boolean createFileReaders() {
        try {
            this.inputInStream = new FileInputStream(this.inputFile);
            Reader inputReader = new InputStreamReader(this.inputInStream);
            this.inputBuffer = new BufferedReader(inputReader);

        } catch (FileNotFoundException FNFe) {
            System.out.println("Could not find "+this.inputFile);
            return false;
        }

        return true;
    }

    // Get the next character from the inputFile
    String getNextCharacter() {
        // If Type.NORMAL then we are dealing with only ascii characters
        if (this.type == Type.NORMAL) {
            // Getting the next character of the file
            try {
                return this.inputBuffer.read()+"";
            } catch (IOException e) {
                System.out.println("Could not read "+this.inputFile);
                return "-1";
            }
        } else if (this.type == Type.DECODING) {
        // If Type.DECODING then we are dealing with bytes and bits
            byte[] b = new byte[1];

            try {
                // Reading 1 byte at a time
                if (this.inputInStream.read(b) != -1) {

                    // Converting the 1's and 0's in the byte to a character array accounting for the sign bit in the byte
                    char[] rawByteBinaryOut = Integer.toBinaryString((b[0] & 0xFF)).toCharArray();

                    // Converting the char[] removes the leading 0's so writing the bits to a new array containing leading zeros
                    char[] correctedSize = new char[] {'0','0','0','0','0','0','0','0'};
                    int j = 0;

                    // Looping over the binary to add the leading zeros
                    for (int i=7; i>=0; i--) {
                        if (rawByteBinaryOut.length-1-j >= 0) {
                            char toAdd = rawByteBinaryOut[rawByteBinaryOut.length-1-j];
                            correctedSize[i] = toAdd;
                            j++;
                        }
                    }

                    // Returning the byte as a string
                    return new String(correctedSize);
                }
            } catch (IOException e) {
                System.out.println("There was an error reading the file.");
            }
        }

        return "-1";
    }

    // Get ready to output to files
    boolean prepareOutput(String filename) {
        try {
            this.outputWriter = new PrintWriter(filename);
            this.outputStream = new FileOutputStream(filename);
            this.byteBuffer = new ArrayList<>();
        } catch (FileNotFoundException FNFe) {
            return false;
        }

        return true;
    }

    // Function to add all bits to write to an ArrayList
    void storeBitsToWrite(String data) {
        for (int i=0; i<data.length(); i++) {
            int currentInt = Integer.valueOf(""+data.charAt(i));
            this.byteBuffer.add(currentInt);
        }
    }

    // Function to write data to file
    void writeToFile(String data) {
        if (this.type == Type.NORMAL) {
            this.outputWriter.append(data);
        }
        flush();
    }

    // Function to create bytes from the stored bits and write them to a file
    void writeBytesToFile() {
        while (this.byteBuffer.size() > 0) {
            StringBuilder bitsForByte = new StringBuilder();
            // Get 8 bits
            for (int i=0; i<8; i++) {
                if (this.byteBuffer.size() > 0) {
                    bitsForByte.append(this.byteBuffer.get(0));
                    this.byteBuffer.remove(0);
                } else {
                    bitsForByte.append("1");
                }
            }

            // Add the bits to a byte
            byte[] b = new byte[1];
            b[0] = ((byte) Integer.parseInt(bitsForByte.toString(), 2));

            // Write the byte to file
            try {
                this.outputStream.write(b[0]);
            } catch (IOException e) {
                System.out.println("Error writing to file.");
            }
        }
    }

    // Function to close the writer
    void closeOutputWriter() {
        this.outputWriter.close();
    }

    // Flushing the writer ensuring the data is written to file
    private void flush() {
        this.outputWriter.flush();
    }
}
