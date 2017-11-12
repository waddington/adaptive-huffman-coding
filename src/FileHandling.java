import java.io.*;
import java.util.ArrayList;

enum Type {
    NORMAL,
    ENCODING,
    DECODING
}

public class FileHandling {
    Type type;
    File inputFile;
    InputStream inputInStream;
    Reader inputReader;
    Reader inputBuffer;
    PrintWriter outputWriter;
    OutputStream outputStream;
    ArrayList<Integer> byteBuffer;

    FileHandling(Type type) {
        this.type = type;
    }

    // Check if a inputFile is valid
    boolean isFileValid(String filename) {
        File file = new File(filename);

        // Check if inputFile is valid - https://stackoverflow.com/a/1816676/3259361
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
            this.inputReader = new InputStreamReader(this.inputInStream);
            this.inputBuffer = new BufferedReader(this.inputReader);

        } catch (FileNotFoundException FNFe) {
            System.out.println("Could not find "+this.inputFile);
            return false;
        }

        return true;
    }

    // Get the next character from the inputFile
    String getNextCharacter() {
        if (this.type == Type.NORMAL) {
            try {
                return this.inputBuffer.read()+"";
            } catch (IOException e) {
                System.out.println("Could not read "+this.inputFile);
                return "-1";
            }
        } else if (this.type == Type.DECODING) {
            byte[] b = new byte[1];
            int readBytes = 0;

            try {
                while ((readBytes = this.inputInStream.read(b)) != -1) {

                    char[] rawByteBinaryOut = Integer.toBinaryString((b[0] & 0xFF)).toCharArray();
                    char[] correctedSize = new char[] {'0','0','0','0','0','0','0','0'};

                    int j = 0;
                    for (int i=7; i>=0; i--) {
                        if (rawByteBinaryOut.length-1-j >= 0) {
                            char toAdd = rawByteBinaryOut[rawByteBinaryOut.length-1-j];
                            correctedSize[i] = toAdd;
                            j++;
                        }
                    }

                    String decodedByte = new String(correctedSize);
                    return decodedByte;
                }
            } catch (IOException e) {
                System.out.println("There was an error reading the file.");
            }
        }

        return "-1";
    }

    // Get ready to output to inputFile
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

    // Function to add all bits to an ArrayList
    boolean storeBitsToWrite(String data) {
        if (this.type == Type.NORMAL) {
            this.outputWriter.append(data);
        } else if (this.type == Type.ENCODING) {
            for (int i=0; i<data.length(); i++) {
                int currentInt = Integer.valueOf(""+data.charAt(i));
                this.byteBuffer.add(currentInt);
            }
        }
        flush();
        return true;
    }

    // Function to write data to file
    boolean writeToFile(String data) {
        if (this.type == Type.NORMAL) {
            this.outputWriter.append(data);
        }
        flush();
        return true;
    }

    // Function to create bytes from the stored bits and write them to a file
    void writeBytesToFile() {
        int excessBits = 0;
        while (this.byteBuffer.size() > 0) {
            String bitsForByte = "";
            // Get 8 bits
            for (int i=0; i<8; i++) {
                if (this.byteBuffer.size() > 0) {
                    bitsForByte += this.byteBuffer.get(0);
                    this.byteBuffer.remove(0);
                } else {
                    bitsForByte += "1";
                    excessBits++;
                }
            }

            byte[] b = new byte[1];
            b[0] = ((byte) Integer.parseInt(bitsForByte, 2));

            try {
                this.outputStream.write(b[0]);
            } catch (IOException e) {
                System.out.println("Error writing to file.");
            }
        }

        String bitsForByte = "11111111";
        byte[] b = new byte[1];
        b[0] = ((byte) Integer.parseInt(bitsForByte, 2));

        try {
            this.outputStream.write(b[0]);
        } catch (IOException e) {
            System.out.println("Error writing to file.");
        }

        if (excessBits > 0) {
            for (int j=0; j<excessBits; j++) {
                bitsForByte = "11111111";
                b = new byte[1];
                b[0] = ((byte) Integer.parseInt(bitsForByte, 2));

                try {
                    this.outputStream.write(b[0]);
                } catch (IOException e) {
                    System.out.println("Error writing to file.");
                }
            }
        }
    }

    // Function to close the writer
    void closeOutputWriter() {
        this.outputWriter.close();
    }

    void flush() {
        this.outputWriter.flush();
    }
}
