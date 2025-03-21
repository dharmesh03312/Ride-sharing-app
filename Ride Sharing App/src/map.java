import java.util.*;

// Singly Linked List class
class SLL {
    Node head = null;
    int size = 0;

    class Node {
        String data;
        Node next;

        Node(String data) {
            this.data = data;
            this.next = null;
            size++;
        }
    }

    void add(String data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = newNode;
            return;
        }
        Node currNode = head;
        while (currNode.next != null) {
            currNode = currNode.next;
        }
        currNode.next = newNode;
    }

    void display() {
        if (head == null) {
            System.out.println("List is empty");
            return;
        }
        Node currNode = head;
        while (currNode != null) {
            System.out.print(currNode.data + " --> ");
            currNode = currNode.next;
        }
        System.out.print("NULL");
    }

    // Method to get the list size
    int getSize() {
        return size;
    }

    // Method to get a node data by index
    String getNodeAt(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("Index out of bounds");
        Node currNode = head;
        for (int i = 0; i < index; i++) {
            currNode = currNode.next;
        }
        return currNode.data;
    }
}

// Binary Search Tree class
class map {
    map() {
        String[] cities = { "navsari", "sabarkantha", "panchmahal", "gandhidham", "bharuch", "ahmedabad", "vadodara", "surat", "rajkot", "anand", "nadiad", "vapi", "mehsana", "porbandar", "patan", "gandhinagar", "bhavnagar", "junagadh",
                "jamnagar", "bhuj" };

        for (String city : cities) {
            insert(city);
        }
    }

    class Node {
        String cityName;
        Node left, right, parent;

        Node(String cityName) {
            this.cityName = cityName;
            left = right = parent = null;
        }
    }

    Node root = null;

    // Inserts a city into the BST
    void insert(String cityName) {
        Node newNode = new Node(cityName);
        if (root == null) {
            root = newNode;
            return;
        }

        Node temp = root;
        Node parent = null;

        while (temp != null) {
            parent = temp;
            if (cityName.compareTo(temp.cityName) < 0) {
                temp = temp.left;
            } else {
                temp = temp.right;
            }
        }

        newNode.parent = parent;
        if (cityName.compareTo(parent.cityName) < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }
    }

    // Performs in-order traversal of the BST
    void inorder(Node node) {
        if (node == null) return;
        inorder(node.left);
        System.out.print(node.cityName + " -> ");
        inorder(node.right);
    }

    // Finds the path from root to a given city
    boolean findPath(Node root, String cityName, SLL path) {
        if (root == null) return false;

        path.add(root.cityName);

        if (root.cityName.equals(cityName)) return true;

        if (cityName.compareTo(root.cityName) < 0) {
            return findPath(root.left, cityName, path);
        } else {
            return findPath(root.right, cityName, path);
        }
    }

    // Finds and prints the route between two cities, including start and end cities
    SLL findCitiesBetween(String startCity, String endCity) {
        SLL pathToStart = new SLL();
        SLL pathToEnd = new SLL();

        if (!findPath(root, startCity, pathToStart) || !findPath(root, endCity, pathToEnd)) {
            System.out.println("City not found");
            return null;
        }

        // Find the common ancestor index
        int i = 0;
        while (i < pathToStart.getSize() && i < pathToEnd.getSize() &&
               pathToStart.getNodeAt(i).equals(pathToEnd.getNodeAt(i))) {
            i++;
        }

        // Create a new SLL to store the result path
        SLL resultPath = new SLL();

        // Add the path from startCity to the common ancestor (reversed)
        for (int j = pathToStart.getSize() - 1; j >= i; j--) {
            resultPath.add(pathToStart.getNodeAt(j));
        }

        // Add the path from the common ancestor to endCity
        for (int j = i; j < pathToEnd.getSize(); j++) {
            resultPath.add(pathToEnd.getNodeAt(j));
        }

        return resultPath;
    }

    // Method to check if the tree contains a specific city
    boolean containsNode(String cityName) {
        Node temp = root;
        while (temp != null) {
            if (cityName.equals(temp.cityName)) {
                return true;
            } else if (cityName.compareTo(temp.cityName) < 0) {
                temp = temp.left;
            } else {
                temp = temp.right;
            }
        }
        return false;
    }

    // Main method to test the BST class
    // public static void main(String[] args) {
    //     map bst = new map();
    //     // bst.inorder(bst.root);
    //     SLL s = bst.findCitiesBetween("bharuch", "panchmahal");
    //     if (s != null) {
    //         s.display();
    //     }

    //     // Test the containsNode method
    //     // System.out.println("\nContains 'ahmedabad': " + bst.containsNode("ahmedabad"));
    //     // System.out.println("Contains 'delhi': " + bst.containsNode("delhi"));
    // }
}
