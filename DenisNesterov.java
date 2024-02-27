package assignment2.students;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

//Create class Crossword for storing crosswords
class Crossword{
    //Make grid
    public char[][] crossword = new char[20][20];
    //Make constructor for filling crossword with '-' 
    public Crossword(){
        for(int i = 0; i < 20; i++){
            for(int j = 0; j < 20; j++){
                crossword[i][j] = '-';
            }
        }
    }
    //Make function for displaying crosswords
    public void display() {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                System.out.print(crossword[i][j] + " ");
            }
            System.out.println();
        }
    }
}
//Create class for Evolutionary Algorithm
class EvolutionaryAlgorithm {
    //Create class Word for storing word, x, y and direction values
    static class Word{
        String word;
        int x;
        int y;
        int direction;
        public Word(String word, int x, int y, int direction){
            this.word = word;
            this.x = x;
            this.y = y;
            this.direction = direction;
        }
        public Word(){

        }
    } 
    //Size of population
    private static int populationSize = 0;
    //List of words
    private static List<String> input;
    //Number of generations
    private static int numberOfGenerations = 0;
    //Number of parents in new population
    private static int parentsLength = 0;
    //Number of current input file
    private static int fileNumber = 0;
    //Constructor
    public EvolutionaryAlgorithm(int populationSize, List<String> inputList, int numberOfGenerations, int parentsLength, int fileNumber){
        this.populationSize = populationSize;
        this.input = inputList;
        this.numberOfGenerations = numberOfGenerations;
        this.parentsLength = parentsLength;
        this.fileNumber = fileNumber;
    }
    //Function for checking of correctness of the place of the word
    private static boolean is_right_place(Crossword crossword, String word, int x, int y, int direction){
        if (direction == 0) {
            if ((x + word.length()) > 19) {
                return false;
            }
            int numberOfIntersections = 0;
            for(int i = x; i < word.length() + x; i++){
                if (crossword.crossword[i][y] != '-' && crossword.crossword[i][y] != word.charAt(i - x)) {
                    return false;
                }else if (crossword.crossword[i][y] == word.charAt(i - x)) {
                    numberOfIntersections++;
                }
            }
            if (numberOfIntersections <= 1) {
                return true;
            }else {
                return false;
            }
        }else{
            if ((y + word.length()) > 19) {
                return false;
            }
            int numberOfIntersections = 0;
            for(int i = y; i < word.length() + y; i++){
                if (crossword.crossword[x][i] != '-' && crossword.crossword[x][i] != word.charAt(i - y)) {
                    return false;
                }else if (crossword.crossword[x][i] == word.charAt(i - y)) {
                    numberOfIntersections++;
                }
            }
            if (numberOfIntersections <= 1) {
                return true;
            }
            else {
                return false;
            }
        }
    }
    //Population - list of crosswords
    private static List<Crossword> population = new ArrayList<>();
    //Population - list of classes word
    private static List<Word[]> words = new ArrayList<>();
    //Function for initializing population
    private static void initialize_population(){
        for(int k = 0; k < populationSize; k++){
            Crossword newCrossword = new Crossword();
            Word[] newWords = new Word[input.size()];
            words.add(newWords);
            int count = 0;
            for(String word : input){
                newWords[count] = new Word();
                Random rand = new Random();
                int x = rand.nextInt(20);
                int y = rand.nextInt(20);
                int direction = rand.nextInt(2);
                while (!is_right_place(newCrossword, word, x, y, direction)) {
                        x = rand.nextInt(20);
                        y = rand.nextInt(20);
                        direction = rand.nextInt(2);
                }
                if (direction == 0) {
                    for(int i = x; i < word.length() + x; i++){
                        newCrossword.crossword[i][y] = word.charAt(i - x);
                    }
                    newWords[count].x = x;
                    newWords[count].y = y;
                    newWords[count].word = word;
                    newWords[count].direction = direction;
                }else{ 
                    for(int i = y; i < word.length() + y; i++){
                        newCrossword.crossword[x][i] = word.charAt(i - y);
                    }
                    newWords[count].x = x;
                    newWords[count].y = y;
                    newWords[count].word = word;
                    newWords[count].direction = direction;
                
                }
                count++;
            }
            population.add(newCrossword);
        }
    }
    //Function for printing answer to the output file
    private static void print_answer(int i, Word[] word){
            new File("output").mkdir();
            try{
                PrintWriter writer = new PrintWriter("./output/output" + i + ".txt", "UTF-8");
                for(Word el : word){
                    int dir = el.direction == 0 ? 1 : 0;
                    writer.println(el.x + " " + el.y + " " + dir);
                }
                writer.close();
            }catch (FileNotFoundException e){
                System.out.println(e);
            } catch(UnsupportedEncodingException u) {
                System.out.println(u);
            }           
    }
    //Function for checking is words located near of each other
    private static boolean is_neighbors(Word word1, Word word2){
        if (word1.direction != word2.direction) {
            List<int[]> coordinates = new ArrayList<>();
            if (word1.direction == 0) {
                for(int i = word1.x; i < word1.x + word1.word.length(); i++){
                    int[] coor = new int[2];
                    coor[0] = i;
                    coor[1] = word1.y;
                    coordinates.add(coor);
                }
                for(int i = word2.y; i < word2.y + word2.word.length(); i++){
                    for(int[] el : coordinates){
                        if (el[0] == (word2.x - 1) && el[1] == i) {
                            return true;
                        }
                        if (el[0] == (word2.x + 1) && el[1] == i) {
                            return true;
                        }
                    }
                }
                for(int[] el : coordinates){
                    if (el[0] == word2.x && el[1] == (word2.y + 1)) {
                        return true;
                    }
                    if (el[0] == word2.x && el[1] == (word2.y - 1)) {
                        return true;
                    }
                }
            }else{
                for(int i = word1.y; i < word1.y + word1.word.length(); i++){
                    int[] coor = new int[2];
                    coor[0] = word1.x;
                    coor[1] = i;
                    coordinates.add(coor);
                }
                for(int i = word2.x; i < word2.x + word2.word.length(); i++){
                    for(int[] el : coordinates){
                        if (el[0] == i && el[1] == (word2.y - 1)) {
                            return true;
                        }
                        if (el[0] == i && el[1] == (word2.y + 1)) {
                            return true;
                        }
                    }
                }
                for(int[] el : coordinates){
                    if (el[0] == word2.x + 1 && el[1] == word2.y) {
                        return true;
                    }
                    if (el[0] == word2.x - 1 && el[1] == word2.y) {
                        return true;
                    }
                }
            }
        }else{
            List<int[]> coordinates = new ArrayList<>();
            if (word1.direction == 0) {
                for(int i = word1.x; i < word1.x + word1.word.length(); i++){
                    int[] coor = new int[2];
                    coor[0] = i;
                    coor[1] = word1.y;
                    coordinates.add(coor);
                }
                for(int i = word2.x; i < word2.x + word2.word.length(); i++){
                    for(int[] el : coordinates){
                        if (el[0] == i  && el[1] == (word2.y + 1)) {
                            return true;
                        }
                        if (el[0] == i  && el[1] == (word2.y - 1)) {
                            return true;
                        }
                        if (el[0] == i  && el[1] == word2.y) {
                            return true;
                        }
                    }
                }
                for(int[] el : coordinates){
                    if (el[0] == (word2.x + 1) && el[1] == word2.y ) {
                        return true;
                    }
                    if (el[0] == (word2.x - 1) && el[1] == word2.y ) {
                        return true;
                    }
                }
            }else{
                for(int i = word1.y; i < word1.y + word1.word.length(); i++){
                    int[] coor = new int[2];
                    coor[0] = word1.x;
                    coor[1] = i;
                    coordinates.add(coor);
                }
                for(int i = word2.y; i < word2.y + word2.word.length(); i++){
                    for(int[] el : coordinates){
                        if (el[0] == (word2.x + 1)  && el[1] == i) {
                            return true;
                        }
                        if (el[0] == (word2.x - 1)  && el[1] == i) {
                            return true;
                        }
                        if (el[0] == word2.y  && el[1] == i) {
                            return true;
                        }
                    }
                }
                for(int[] el : coordinates){
                    if (el[0] == word2.x && el[1] == (word2.y + 1)) {
                        return true;
                    }
                    if (el[0] == word2.x && el[1] == (word2.y - 1) ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    //Function for selecting parents from the population
    private static int select_parent(int[] fitnessValues){
        Random rand = new Random();
        int[] indecies = new int[3];
        for(int i = 0; i < 3; i++){
            boolean a = true;
            while (a) {
                int index = rand.nextInt(populationSize);
                if (fitnessValues[index] != Integer.MIN_VALUE) {
                    a = false;
                    indecies[i] = index;
                }
            }
        }
        int maxIndex = 0;
        int maxValue = Integer.MIN_VALUE;
        for(int el : indecies){
            if (fitnessValues[el] > maxValue) {
                maxValue = fitnessValues[el];
                maxIndex = el;
            }
            fitnessValues[el] = Integer.MIN_VALUE;
        }
        return maxIndex;
    }
    //Function for checking are two words correctly intersects
    private static int is_intersect(Word word1, Word word2, Crossword crossword){
        int intersection = 0;
        if (word1.direction != word2.direction) {
            if (word1.direction == 0) {
                for(int i = word1.x; i < word1.x + word1.word.length(); i++){
                    if (i == word2.x && word1.y >= word2.y && word1.y <= (word2.y + word2.word.length() - 1)) {
                        if ((word1.word.charAt(i - word1.x) == word2.word.charAt(word1.y - word2.y))) {
                            intersection++;
                        }
                    }
                }
            }else{
                for(int i = word1.y; i < word1.y + word1.word.length(); i++){
                    if (i == word2.y && word1.x >= word2.x && word1.x <= (word2.x+ word2.word.length() - 1)) {
                        if (word1.word.charAt(i - word1.y) == word2.word.charAt(word1.x - word2.x)) {
                            intersection++;
                        }
                    }
                }
            }
        }
        if (intersection != 0) {
            return intersection;
        }else{
            return -1;
        }
    }
    //Function dfs for checking is graph connected
    private static void dfs(int[][] graph, boolean[] visited, int node) {
        Stack<Integer> stack = new Stack<>();
        stack.push(node);
        visited[node] = true;
        while (!stack.isEmpty()) {
            int current = stack.pop();
            for (int i = 0; i < graph.length; i++) {
                if (graph[current][i] == 1 && !visited[i]) {
                    stack.push(i);
                    visited[i] = true;
                }
            }
        }
    }
    //Function for checking is graph connected. Nodes in this graph are words of crossword
    private static boolean isConnected(int[][] graph) {
        int n = graph.length;
        boolean[] visited = new boolean[n];
        dfs(graph, visited, 0);
        for (boolean v : visited) {
            if (!v) {
                return false;
            }
        }
        return true; 
    }
    //Function for checking fitness of the crosword in population
    private static int fitness(Crossword crossword, Word[] words){
        int fitness = 0;
        int neighbours = 0;
            int[][] connected = new int[words.length][words.length];
            for(int[] el : connected){
                Arrays.fill(el, 0);
            }
            for(int i = 0; i < words.length; i++){
                int intersects = 0;
                for(int j = i + 1; j < words.length; j++){
                    if (!words[i].word.equals(words[j].word)) {
                        intersects = is_intersect(words[i], words[j], crossword);
                        if (intersects != -1) {
                            fitness+=1000;
                            connected[i][j] = 1;
                            connected[j][i] = 1;
                        }
                        else{
                            if (!is_neighbors(words[i], words[j])) {
                                fitness+=1;
                            }else{
                                neighbours++;
                            }
                        }
                        
                    }
                }
            }
        
            if (isConnected(connected) && neighbours == 0) {
                return - 1;
            }else{
                return fitness;
            }
    }
    //Function for crossing two selected parents from a population
    private static Word[] crossover(Word[] word1, Word[] word2){
        Word[] child = new Word[word1.length];
        Random rand = new Random();
        int point = rand.nextInt(word1.length);
        for(int i = 0; i < point; i++){
            child[i] = new Word(word1[i].word, word1[i].x, word1[i].y, word1[i].direction);
        }
        for(int i = point; i < word2.length; i++){
            child[i] = new Word(word2[i].word, word2[i].x, word2[i].y, word2[i].direction);
        }
        return child;
    }
    //Function for mutating some word in crossword
    private static void mutate(Word word, Word[] w, Crossword crossword, int index, int wordIndex){
        Random random = new Random();
        int direction = random.nextInt(2);
        int x = random.nextInt(direction == 0 ? 20 - word.word.length() : 20);
        int y = random.nextInt(direction == 1 ? 20 - word.word.length() : 20);
        while (!is_right_place(crossword, word.word, x, y, direction)) {
            direction = random.nextInt(2);
            x = random.nextInt(20);
            y = random.nextInt(20);
        }
        word.x = x;
        word.y = y;
        word.direction = direction;
    }
    //Аunction for initializing a crossword puzzle grid based on an array of Word classes
    private static Crossword initalize_crossword(Word[] word){
        Crossword crossword = new Crossword();
        for(Word el : word){
            if (el.direction == 0) {
                for(int i = el.x; i < el.x + el.word.length(); i++){
                    crossword.crossword[i][el.y] = el.word.charAt(i - el.x);
                }
            }else{
                for(int i = el.y; i < el.y + el.word.length(); i++){
                    crossword.crossword[el.x][i] = el.word.charAt(i - el.y);
                }
            }
        }
        return crossword;
    }
    //Function for running Evolutionary Algorithm
    public void run(){
        Random rand = new Random();
        initialize_population();
        int numberOfMutations = 10;
        int count = 0;
        int avgFitness = 0;
        for(int i = 0; i < numberOfGenerations; i++){
            int[] fitnessValues = new int[populationSize];
            for(int j = 0; j < populationSize; j++){
                int fitnessValue = fitness(population.get(j), words.get(j));
                fitnessValues[j] = fitnessValue;
                if (fitnessValue == -1){
                    print_answer(fileNumber, words.get(j));
                    population.get(j).display();
                    words.clear();
                    population.clear();
                    return;
                }
            }
            List<Word[]> parents = new ArrayList<>();
            for(int j = 0; j < parentsLength; j++){
                int index = select_parent(fitnessValues);
                fitnessValues[index] = Integer.MIN_VALUE;
                parents.add(words.get(index));
            }
            List<Word[]> children = new ArrayList<>();
            for(int j = 0; j < populationSize - parentsLength; j++){
                children.add(crossover(parents.get(rand.nextInt(parentsLength)), parents.get(rand.nextInt(parentsLength))));
            }
            int s = 0;
            for(Word[] el : parents){
                s += fitness(initalize_crossword(el), el);
            }
            if (avgFitness == s/parentsLength) {
                count++;
                if (count % 500 == 0) {
                    numberOfMutations+=5;
                }
            }
            avgFitness = s/parentsLength;
            List<Word[]> newWords = new ArrayList<>();
            for(Word[] word : parents){
                newWords.add(word);
            }
            for(Word[] word : children){
                newWords.add(word);
            }
            words = newWords;
            for(int j = 0; j < numberOfMutations; j++){
                int index = rand.nextInt(populationSize);
                int wordIndex = rand.nextInt(input.size());
                mutate(words.get(index)[wordIndex], words.get(index), population.get(index), index, wordIndex);
            }
            for(int j = 0; j < populationSize; j++){
                population.set(j, initalize_crossword(words.get(j)));
            }
        }
        return ;
    }
}
public class DenisNesterov{
    //Function for finding number of files in input directiry
    private static int find_M(String path){
        int M = 0;
        File file = new File(path);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            if(!files[i].isDirectory()){
                M++;
            }
        }
        return M;
    }
    //Function for parsing input file
    private static List<String> parse_input(String fileName){
        List<String> input = new ArrayList<>();
        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                input.add(line);
            }
            scanner.close();
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        return input;
    }
    public static void main(String[] args) {
        //Finding number of files in input directory
        EvolutionaryAlgorithm evolutionaryAlgorithm;
        int M = find_M("input");
        for (int i = 1; i <= M; i++){
            //Parsing input file
            List<String> input = parse_input("./input/input" + i + ".txt");
            //Create Evolutionary Algorithm
            evolutionaryAlgorithm = new EvolutionaryAlgorithm(100, input, 50000, 10, i);
            //Run evolutionary algorithm
            evolutionaryAlgorithm.run();
        }
    }
}
