import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

enum Direction {
    ENCODING,
    DECODING
}

public class HuffmanTree {
    Direction direction;
    Node rootNode;
    Node DAG;
    HashSet<Character> charactersInTree;
    String currentCharCode;
    boolean firstCharacterAdded;
    ArrayList<Node> nodesInTree;
    Node currentNode;
    Boolean characterMatchFound;
    Boolean gotDAG;
    char currentCharacter;

    HuffmanTree(Direction direction) {
        this.direction = direction;
        this.charactersInTree = new HashSet<>(); // Used for quickly checking whether a character is already in the tree before we search for it
        this.firstCharacterAdded = false;
        this.nodesInTree = new ArrayList<>();
        this.characterMatchFound = false;
        this.gotDAG = false;
        this.currentCharacter = '\0';

        createBaseStructure();
    }

    // Function that will be called to add a new character to the tree and to return the code for that character
    // This should be the only function that needs to be called
    // This function should call any other required functions
    String addCharAndGetCode(int incomingCharAsInt) {
        this.currentCharCode = "";
        char characterToAdd = (char) incomingCharAsInt;
        String codeToWrite = "";

        // Check if character is in tree
        if (isCharacterInTree(characterToAdd)) {
            doTreeTraversal(this.rootNode, "", false, characterToAdd, true, false); // Get the character code and print it, and increment the characters frequency
            codeToWrite += this.currentCharCode;
            updateTreeArrayList(); // Update the ArrayList that is used to build the tree from
        } else {
            doTreeTraversal(this.rootNode, "", true, '\0', false, false); // Find DAG and get the code for it
            codeToWrite += this.currentCharCode; // Get the code of the DAG node
            // TODO: better binary coversion
            codeToWrite += String.format("%8s", Integer.toBinaryString(incomingCharAsInt)).replace(' ', '0'); // Add the binary representation of the ascii character to the string
            addCharacterToTree(characterToAdd); // Add the character to the tree structure
        }

        rebuildHuffmanTree(); // Re-build the tree so that it is correct

        return codeToWrite;
    }

    // Used to create the initial base of the tree
    void createBaseStructure() {
        this.DAG = new Node(NodeType.DAG);
        this.DAG.setValue(0);

        this.rootNode = new Node(NodeType.COMBINATION);
        this.rootNode.setValue(0);
        this.rootNode.setLeftChild(this.DAG);

        this.currentNode = this.rootNode;
    }

    // Method to check if a character is already in the tree
    boolean isCharacterInTree(char c) {
        return this.charactersInTree.contains(c);
    }

    // Method to check for the DAG node
    void doTreeTraversal(Node node, String code, boolean lookingForDag, char character, boolean incrementCharacterFrequency, boolean toArrayList) {
        // Not at a leaf node - go deeper
        if (node.getLeftChild() != null)
            doTreeTraversal(node.getLeftChild(), code+"0", lookingForDag, character, incrementCharacterFrequency, toArrayList);

        // Not at a leaf node - go deeper
        if (node.getRightChild() != null)
            doTreeTraversal(node.getRightChild(), code+"1", lookingForDag, character, incrementCharacterFrequency, toArrayList);

        // If we are at a leaf node
        if (node.getLeftChild() == null && node.getRightChild() == null) {
            if (lookingForDag)
                if (node.getType() == NodeType.DAG)
                    this.currentCharCode = code;

            if (node.getType() == NodeType.SINGLETON && node.getCharacter() == character) {
                this.currentCharCode = code;
                if (incrementCharacterFrequency) node.setValue(node.getValue()+1);
            }

            if (toArrayList) {
                this.nodesInTree.add(node);
            }
        }
    }

    void updateTreeArrayList() {
        this.nodesInTree.clear();
        doTreeTraversal(this.rootNode, "", false, '\0', false, true);
    }

    void addCharacterToTree(char characterToAdd) {
        // If this is the first character added, we can discard the root combination node
        if (!this.firstCharacterAdded) {
            this.rootNode = this.DAG;
            this.firstCharacterAdded = true;
        }

        updateTreeArrayList();

        Node newCharacterNode = new Node(NodeType.SINGLETON);
        newCharacterNode.setValue(1);
        newCharacterNode.setCharacter(characterToAdd);

        this.nodesInTree.add(newCharacterNode);
        this.charactersInTree.add(characterToAdd);
    }

    void rebuildHuffmanTree() {
        // Sort the array list in descending order
        this.nodesInTree.sort(Comparator.comparing(Node::getValue).reversed());

        while (this.nodesInTree.size() > 1) {
            Node newLeftChild;
            Node newRightChild;

            Node lastNode = this.nodesInTree.get(this.nodesInTree.size()-1);
            Node secondLastNode = this.nodesInTree.get(this.nodesInTree.size()-2);
            this.nodesInTree.remove(this.nodesInTree.size()-1);
            this.nodesInTree.remove(this.nodesInTree.size()-1);

            newLeftChild = lastNode;
            newRightChild = secondLastNode;

            Node newCombinationNode = new Node(NodeType.COMBINATION);
            newCombinationNode.setValue(newLeftChild.getValue()+newRightChild.getValue());
            newCombinationNode.setLeftChild(newLeftChild);
            newCombinationNode.setRightChild(newRightChild);

            this.nodesInTree.add(newCombinationNode);
            this.nodesInTree.sort(Comparator.comparing(Node::getValue).reversed());
        }

        this.rootNode = this.nodesInTree.get(0);

        if (this.direction == Direction.DECODING) {
            this.currentNode = this.rootNode;
            this.gotDAG = false;
        }
    }

    boolean isCharacterMatchFound() {
        return this.characterMatchFound;
    }

    boolean didGetDag() {
        return this.gotDAG;
    }

    char getCurrentCharacter() {
        this.characterMatchFound = false;
        this.currentNode = this.rootNode;
        return this.currentCharacter;
    }

    void takeNextBit(int nextBit) {
        if (nextBit == 0)
            this.currentNode = this.currentNode.getLeftChild();
        else if (nextBit == 1)
            this.currentNode = this.currentNode.getRightChild();

        if (this.currentNode.getType() == NodeType.SINGLETON) {
            this.characterMatchFound = true;
            this.currentCharacter = this.currentNode.getCharacter();
        } else if (this.currentNode.getType() == NodeType.DAG) {
            this.gotDAG = true;
        }
    }
}
