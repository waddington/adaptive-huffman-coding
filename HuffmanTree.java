import java.util.*;

enum Direction {
    ENCODING,
    DECODING
}

class HuffmanTree {
    private final Direction direction;
    private Node rootNode;
    private ArrayList<Node> huffmanTree;
    private ArrayList<Node> nodesInTree;
    private HashSet<Character> charactersInTree; // Used for quickly checking if a character is in the tree
    private char currentChar;
    private String currentCharCode;
    private Node currentNode;
    private boolean characterMatchFound;
    private boolean gotDag;
    private boolean gotEof;

    HuffmanTree(Direction direction) {
        this.direction = direction;
        this.charactersInTree = new HashSet<>();
        this.currentChar = '\0';
        this.currentCharCode = "";
        this.characterMatchFound = false;
        this.gotDag = false;
        this.gotEof = false;
        this.huffmanTree = new ArrayList<>();

        createBaseStructure();
    }

    private void createBaseStructure() {
        // Set up the original structure with a dagger node and an end-of-file node
        Node DAG = new Node(NodeType.DAG);
        DAG.setValue(0);

        Node EOF = new Node(NodeType.EOF);
        EOF.setValue(0);

        this.rootNode = new Node(NodeType.COMBINATION);
        this.rootNode.setValue(0);
        this.rootNode.setLeftChild(DAG);
        this.rootNode.setRightChild(EOF);

        this.currentNode = this.rootNode;

        this.huffmanTree.add(this.rootNode);
    }

    String addCharAndGetCode(int incomingCharAsInt) {
        String codeToWrite = "";
        char characterToAdd = (char) incomingCharAsInt;

        // Check if character is already in the structure
        if (isCharacterInTree(characterToAdd)) {
            // Get the code for the character
            codeToWrite += getCharacterCode(characterToAdd, true);

            // Kl is my class to print everything - only used for printing the progress of the algorithm
            if (this.direction == Direction.ENCODING) Kl.p(codeToWrite);
        } else {
            // Character not already in tree so need to add it and get dagger code
            codeToWrite += getNodeTypeCode(NodeType.DAG);

            // Get binary representation of ascii character
            String charAsBinary = String.format("%8s", Integer.toBinaryString(incomingCharAsInt)).replace(' ', '0');

            // Printing the progress of the algorithm
            if (this.direction == Direction.ENCODING) Kl.p("DAG(" + codeToWrite + ") ASCII(" + charAsBinary + ")");

            // Add the binary representation of the ascii character to the string
            codeToWrite += charAsBinary;
            addCharToTree(characterToAdd);
        }

        return codeToWrite;
    }

    private boolean isCharacterInTree(char c) {
        return this.charactersInTree.contains(c);
    }

    // Used to get the codeword for the character
    private String getCharacterCode(char c, boolean increaseFreq) {
        this.currentCharCode = "";
        getNodeCode(this.rootNode, c, NodeType.SINGLETON, increaseFreq, "", false);
        return this.currentCharCode;
    }

    // Used to get the codeword for a special node
    private String getNodeTypeCode(NodeType type) {
        this.currentCharCode = "";
        getNodeCode(this.rootNode, '\0', type, false, "", false);
        return this.currentCharCode;
    }

    // Method used to get any node codeword
    private void getNodeCode(Node node, char c, NodeType type, boolean increaseFreq, String code, boolean toList) {
        // Using a DFS as generally quicker to reach all leaf nodes
        if (node.getLeftChild() != null)
            getNodeCode(node.getLeftChild(), c, type, increaseFreq, code + "0", toList);

        if (node.getRightChild() != null)
            getNodeCode(node.getRightChild(), c, type, increaseFreq, code + "1", toList);

        // Double check we are at a leaf node
        if (node.getLeftChild() == null && node.getRightChild() == null) {
            if (toList) this.nodesInTree.add(node); // Add all leaf nodes to an ArrayList, not discriminating against type
            if (node.getType() == type && node.getCharacter() == c) {
                if (increaseFreq && node.getType() == NodeType.SINGLETON) node.setValue(node.getValue()+1);
                this.currentCharCode = code;
            }
        }
    }

    private void addCharToTree(char c) {
        // Insert new node for character after last item in tree
        Node newCharNode = new Node(NodeType.SINGLETON);
        newCharNode.setValue(1);
        newCharNode.setCharacter(c);

        this.charactersInTree.add(c);

        rebuildHuffmanTree(newCharNode);
    }

    private void rebuildHuffmanTree(Node nodeToAdd) {
        // Get list of all singleton nodes
        this.nodesInTree = new ArrayList<>();
        getNodeCode(this.rootNode, '\0', null, false, "", true);

        // Add a new node if required
        if (nodeToAdd != null)
            this.nodesInTree.add(nodeToAdd);


        // I could use a PriorityQueue (which is a heap) for this however it is on average slower than quicksorting a list which is what the default sort() method uses
        this.nodesInTree = sortNodeList(this.nodesInTree);

        // Loop over the list of nodes as long as it contains more than 1 node
        while (this.nodesInTree.size() > 1) {
            // Get the 2 nodes with lowest frequencies
            Node newLeftChild = this.nodesInTree.get(this.nodesInTree.size()-1);
            Node newRightChild = this.nodesInTree.get(this.nodesInTree.size()-2);

            this.nodesInTree.remove(this.nodesInTree.size()-1);
            this.nodesInTree.remove(this.nodesInTree.size()-1);

            // Create the new parent node
            Node newComboNode = new Node(NodeType.COMBINATION);
            newComboNode.setValue(newLeftChild.getValue()+newRightChild.getValue());
            newComboNode.setLeftChild(newLeftChild);
            newComboNode.setRightChild(newRightChild);

            // Add the node back into the list and sortr
            this.nodesInTree.add(newComboNode);
            this.nodesInTree = sortNodeList(this.nodesInTree);
        }

        this.rootNode = this.nodesInTree.get(0);

        // If decoding, reset the current node pointer to the root node
        if (this.direction == Direction.DECODING) {
            this.currentNode = this.rootNode;
            this.gotDag = false;
        }
    }

    // Sort the list of nodes in descending order on the node frequency
    private ArrayList<Node> sortNodeList(ArrayList<Node> nodes) {
        nodes.sort(Comparator.comparing(Node::getValue).reversed());
        return nodes;
    }

    // How to print a tree
    // https://stackoverflow.com/a/8948691/3259361
    void doPrintTree(String prefix, Node node, boolean isTail, String code) {
        // Used to print the line of the tree
        Kl.pl(prefix + (isTail ? "└── " : "├── ") + getLabel(node, code));

        // Not at a leaf node - go deeper
        if (node.getLeftChild() != null)
            doPrintTree(prefix + (isTail ? "    " : "│   "), node.getLeftChild(), false, code + "0");

        // Not at a leaf node - go deeper
        if (node.getRightChild() != null)
            doPrintTree(prefix + (isTail ? "    " : "│   "), node.getRightChild(), true, code + "1");
    }

    // Used for printing the progress of the algorithm
    private String getLabel(Node node, String code) {
        StringBuilder label = new StringBuilder();
        if (node.type == NodeType.DAG)
            label.append("DAG(code:").append(code).append(")");
        else if (node.type == NodeType.EOF)
            label.append("EOF(code:").append(code).append(")");
        else if (node.type == NodeType.COMBINATION)
            label.append("COMBINATION(frequency:").append(node.getValue()).append(")");
        else if (node.type == NodeType.SINGLETON)
            label.append("\"").append(node.getCharacter()).append("\":(code:").append(code).append(", frequency:").append(node.getValue()).append(")");

        return label.toString();
    }

    // Print the tree
    void printTree() {
        Kl.pl("");
        doPrintTree("", this.rootNode, true, "");
        Kl.pl("");
    }

    boolean isCharacterMatchFound() {
        return this.characterMatchFound;
    }

    char getCurrentCharacter() {
        this.characterMatchFound = false;
        this.currentNode = this.rootNode;
        return this.currentChar;
    }

    boolean didGetDag() {
        return this.gotDag;
    }

    boolean didGetEof() {
        return this.gotEof;
    }

    String getEofCode() {
        return getNodeTypeCode(NodeType.EOF);
    }

    // For decoding purposes
    void takeNextBit(int nextBit) {
        Kl.p(nextBit + "");
        // Traversing the tree
        if (nextBit == 0)
            this.currentNode = this.currentNode.getLeftChild();
        else if (nextBit == 1)
            this.currentNode = this.currentNode.getRightChild();

        // Getting the information
        if (this.currentNode.getType() == NodeType.SINGLETON) {
            this.characterMatchFound = true;
            this.currentChar = this.currentNode.getCharacter();
            Kl.p("(\"" + this.currentChar+"\")");
        } else if (this.currentNode.getType() == NodeType.DAG) {
            this.gotDag = true;
            Kl.p("(DAG) ");
        } else if (this.currentNode.getType() == NodeType.EOF) {
            this.gotEof = true;

            Kl.pl(getEofCode()+"(EOF)");
        }
    }
}
