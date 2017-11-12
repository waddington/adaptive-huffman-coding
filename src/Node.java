enum NodeType {
    SINGLETON,
    COMBINATION,
    DAG
}

public class Node {
    NodeType type;
    int value;
    char character;
    Node leftChild;
    Node rightChild;

    Node(NodeType type) {
        this.type = type;
    }

    NodeType getType() {
        return this.type;
    }

    int getValue() {
        return this.value;
    }
    void setValue(int value) {
        this.value = value;
    }

    char getCharacter() {
        return this.character;
    }
    void setCharacter(char character) {
        this.character = character;
    }

    Node getLeftChild() {
        return this.leftChild;
    }
    void setLeftChild(Node leftChild) {
        this.leftChild = leftChild;
    }

    Node getRightChild() {
        return this.rightChild;
    }
    void setRightChild(Node rightChild) {
        this.rightChild = rightChild;
    }
}
