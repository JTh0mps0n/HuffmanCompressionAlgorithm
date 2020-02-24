import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

public class HuffmanCompression {

    public static void main(String[] args) throws IOException, InterruptedException {
        //char endOfFileChar = '§';
        String inputFile = "The Three Musketeers.txt";
        String outputFile = inputFile.substring(0, inputFile.length() - 4) + ".justin";
        Scanner scanner = new Scanner(new File(inputFile));
        HashMap<Character, Integer> frequencyHashMap = new HashMap<>();
        scanner.useDelimiter("");
        ArrayList<Character> keys = new ArrayList<>();
        while (scanner.hasNext()){
            char c = scanner.next().charAt(0);
            if (frequencyHashMap.containsKey(c)){
                frequencyHashMap.put(c, frequencyHashMap.get(c) + 1);
            }
            else{
                frequencyHashMap.put(c, 1);
                keys.add(c);
            }
        }
        //System.out.println(frequencyHashMap);
        ArrayList<Node> huffmanList = new ArrayList<>();
        for (int i = 0; i < keys.size(); i++) {
            huffmanList.add(new Node(keys.get(i), frequencyHashMap.get(keys.get(i)), true));
        }




        while(huffmanList.size() > 1){
            Node lowestA = huffmanList.get(0);
            Node lowestB = huffmanList.get(1);
            for (int i = 2; i < huffmanList.size(); i++) {
                Node currentNode = huffmanList.get(i);
                if(currentNode.value < lowestA.value){
                    lowestB = lowestA;
                    lowestA = currentNode;
                }
                else if (currentNode.value < lowestB.value){
                    lowestB = currentNode;
                }
            }
            huffmanList.remove(lowestA);
            huffmanList.remove(lowestB);

            Node newTree = new Node(null, lowestA.value + lowestB.value, false);
            lowestA.updateBinary("0");
            newTree.left = lowestA;
            lowestB.updateBinary("1");
            newTree.right = lowestB;
            huffmanList.add(newTree);
        }

        Node root = huffmanList.get(0);

        HashMap<Character, Tuple> encodingTable = new HashMap<>();


        Stack<Node> nodeStack = new Stack<>();
        nodeStack.push(root);
        while (!nodeStack.isEmpty()){
            Node currentNode = nodeStack.pop();
            if(currentNode.isReal){
                //System.out.println(currentNode.binary);
                int binaryValue = 0;
                int count = 0;
                for (int i = currentNode.binary.length() -1; i >= 0; i--) {
                    if(currentNode.binary.charAt(i) == '1'){
                        binaryValue += Math.pow(2, count);
                    }
                    count++;
                }
                //System.out.println(currentNode.character +  " " + currentNode.binary.length() + " " +  binaryValue);
                encodingTable.put(currentNode.character, new Tuple(currentNode.binary.length(), binaryValue));
            }
            else{
                nodeStack.push(currentNode.left);
                nodeStack.push(currentNode.right);
            }

        }




        BitOutputStream writer = new BitOutputStream(outputFile);
        Scanner compressionScanner = new Scanner(new File(inputFile));
        compressionScanner.useDelimiter("");

        while(compressionScanner.hasNext()){
            char c = compressionScanner.next().charAt(0);
            writer.writeBits(encodingTable.get(c).a, encodingTable.get(c).b);
        }
        writer.close();






        System.out.println("File successfully compressed.");
        System.out.print("Decompress?");
        Scanner pause = new Scanner(System.in);
        pause.nextLine();

        BitInputStream reader = new BitInputStream(new File(outputFile));
        BufferedWriter decompressionWriter = new BufferedWriter(new FileWriter("decompressedFile.txt"));
        Node currentNode = root;
        boolean keepRunning = true;
        while(keepRunning){
            int bit = reader.readBits(1);
            if(bit == -1){
                keepRunning = false;
                break;
            }
            else if(bit == 1){
                currentNode = currentNode.right;
            }
            else if (bit == 0){
                currentNode = currentNode.left;
            }

            if(currentNode.isReal){
                decompressionWriter.write(currentNode.character);
                currentNode = root;
            }
        }
        decompressionWriter.close();





    }



    private static class Tuple{
        public int a;
        public int b;
        public Tuple(int a, int b){
            this.a = a;
            this.b = b;
        }
    }

    private static class Node
    {
        public String binary;
        public Character character;
        public Integer value;
        public boolean isReal;
        public Node left;
        public Node right;

        public Node(Character character, Integer value, Boolean isReal){
            binary = "";
            this.value = value;
            this.character = character;
            this.isReal = isReal;
            left = null;
            right = null;
        }

        public void updateBinary(String newDigit){
            Stack<Node> nodeStack = new Stack<>();
            nodeStack.push(this);
            while (!nodeStack.isEmpty()){
                Node currentNode = nodeStack.pop();
                if(currentNode.isReal){
                    currentNode.binary = newDigit + currentNode.binary;
                }
                else{
                    nodeStack.push(currentNode.left);
                    nodeStack.push(currentNode.right);
                }

            }
        }

        public boolean hasLeft(){
            return left != null;
        }
        public boolean hasRight(){
            return right != null;
        }


    }
}

