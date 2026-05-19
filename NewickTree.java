// Source - https://stackoverflow.com/a/41418573
// Posted by Adam
// Retrieved 2026-05-19, License - CC BY-SA 3.0

import java.util.ArrayList;

/**
 * Created on 12/18/16
 *
 * @author <a href="mailto:knapp@american.edu">Adam Knapp</a>
 * @version 0.1
 */
public class NewickTree {

    private static int node_uuid = 0;
    ArrayList<Node> nodeList = new ArrayList<>();
    private Node root;

    public static NewickTree readNewickFormat(String newick) {
        return new NewickTree().innerReadNewickFormat(newick);
    }

    private static String[] split(String s) {
        ArrayList<Integer> splitIndices = new ArrayList<>();
        int rightParenCount = 0;
        int leftParenCount = 0;
        for (int i = 0; i < s.length(); i++) {
            switch (s.charAt(i)) {
                case '(':
                    leftParenCount++;
                    break;
                case ')':
                    rightParenCount++;
                    break;
                case ',':
                    if (leftParenCount == rightParenCount) splitIndices.add(i);
                    break;
            }
        }

        int numSplits = splitIndices.size() + 1;
        String[] splits = new String[numSplits];

        if (numSplits == 1) {
            splits[0] = s;
        } else {
            splits[0] = s.substring(0, splitIndices.get(0));
            for (int i = 1; i < splitIndices.size(); i++) {
                splits[i] = s.substring(splitIndices.get(i - 1) + 1, splitIndices.get(i));
            }
            splits[numSplits - 1] = s.substring(splitIndices.get(splitIndices.size() - 1) + 1);
        }
        return splits;
    }

    private NewickTree innerReadNewickFormat(String newick) {
        // Remove trailing semicolon if present
        if (newick.endsWith(";")) {
            newick = newick.substring(0, newick.length() - 1);
        }
        this.root = readSubtree(newick);
        return this;
    }

    private Node readSubtree(String s) {
        int leftParen = s.indexOf('(');
        int rightParen = s.lastIndexOf(')');

        if (leftParen != -1 && rightParen != -1) {
            String name = s.substring(rightParen + 1);
            String[] childrenString = split(s.substring(leftParen + 1, rightParen));

            Node node = new Node(name);
            node.children = new ArrayList<>();
            for (String sub : childrenString) {
                Node child = readSubtree(sub);
                node.children.add(child);
                child.parent = node;
            }
            nodeList.add(node);
            return node;
        } else if (leftParen == rightParen) {
            Node node = new Node(s);
            nodeList.add(node);
            return node;
        } else throw new RuntimeException("unbalanced ()'s");
    }

    static class Node {
        final String name;
        final int weight;
        boolean realName = false;
        ArrayList<Node> children;
        Node parent;

        Node(String name) {
            int colonIndex = name.indexOf(':');
            String actualNameText;
            if (colonIndex == -1) {
                actualNameText = name;
                weight = 0;
            } else {
                actualNameText = name.substring(0, colonIndex);
                // Handle possible decimal or format issues if needed, here assuming int
                weight = Integer.parseInt(name.substring(colonIndex + 1));
            }

            if (actualNameText.equals("")) {
                this.realName = false;
                this.name = "Node_" + node_uuid;
                node_uuid++;
            } else {
                this.realName = true;
                this.name = actualNameText;
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (children != null && children.size() > 0) {
                sb.append("(");
                for (int i = 0; i < children.size() - 1; i++) {
                    sb.append(children.get(i).toString());
                    sb.append(",");
                }
                sb.append(children.get(children.size() - 1).toString());
                sb.append(")");
            }
            sb.append(realName ? name : "");
            if (weight != 0) sb.append(":").append(weight);
            return sb.toString();
        }
    }

    /**
     * Prints a visual text representation of the tree
     */
    public void visualize() {
        if (root != null) {
            printNode(root, "", true);
        }
    }

    private void printNode(Node node, String prefix, boolean isTail) {
        String displayName = node.realName ? node.name : "(internal)";
        System.out.println(prefix + (isTail ? "└── " : "├── ") + displayName + " [len:" + node.weight + "]");
        
        if (node.children != null) {
            for (int i = 0; i < node.children.size() - 1; i++) {
                printNode(node.children.get(i), prefix + (isTail ? "    " : "│   "), false);
            }
            if (node.children.size() > 0) {
                printNode(node.children.get(node.children.size() - 1), prefix + (isTail ? "    " : "│   "), true);
            }
        }
    }

    @Override
    public String toString() {
        return root.toString() + ";";
    }

    // public static void main(String[] args) {
    //     // Example Neighbor Joining result format
    //     String njResult = "(A:10,B:5,(C:2,D:3)Node_0:4)Root;";
        
    //     System.out.println("Processing Newick String: " + njResult);
    //     NewickTree tree = NewickTree.readNewickFormat(njResult);
        
    //     System.out.println("\nTree Visualization:");
    //     tree.visualize();
        
    //     System.out.println("\nSerialized Format:");
    //     System.out.println(tree.toString());
    // }
}