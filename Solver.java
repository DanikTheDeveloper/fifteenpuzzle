package fifteenpuzzle;

import java.io.*;
import java.util.*;

public class Solver {
    public final static int UP = 0;
    public final static int DOWN = 1;
    public final static int LEFT = 2;
    public final static int RIGHT = 3;
    private int size;
    private State board, goal;
    /**
     * @param fileName
     * @throws FileNotFoundException if file not found
     * @throws BadBoardException     if the board is incorrectly formatted Reads a
     *                               board from file and creates the board
     */
    public void setSize(String fileName) throws IOException, BadBoardException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        int count = br.read();
        int s = br.read();
        if (s < '0' || s > '9')
            count = count - '0';
        else
            count = 10*(count - '0') + (s - '0');
        size = count;
    }
    public Solver(String fileName) throws IOException, BadBoardException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        setSize(fileName);
        int x = 0,y = 0;
        int[][] b = new int[size][size];
        int c1, c2, s;
        br.read();
        if(size > 9)
            br.read();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                s = br.read();
                c1 = br.read();
                c2 = br.read();
                if (s != ' ' && s != '\n') {
                    br.close();
                    throw new BadBoardException("error in line " + i);
                }
                if (c1 == ' ')
                    c1 = '0';
                if (c2 == ' ')
                    c2 = '0';
                b[i][j] = 10 * (c1 - '0') + (c2 - '0');
                if(b[i][j] == 0){
                    x = i;
                    y = j;
                }
            }
        }
        int count = 1;
        int[][] g = new int[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                g[i][j] = count++;
        g[size - 1][size - 1] = 0;
        br.close();
        board = new State(b,size,x,y,0,null,5, 0,0);
        goal = new State(g,size,size - 1,size - 1,0,null,5, 0, 0);
    }
    public static void ans(Solver solve, String fileName) throws BadBoardException, IOException, IllegalMoveException {
        Stack<Integer> moves = solve.solution();
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        while(!moves.isEmpty()) {
            writer.write(moves.pop() + " ");
            switch (moves.pop()) {
                case UP: {
                    writer.write("U");
                    break;
                }
                case DOWN: {
                    writer.write("D");
                    break;
                }
                case RIGHT: {
                    writer.write("R");
                    break;
                }
                case LEFT: {
                    writer.write("L");
                    break;
                }
                default: {
                    writer.write("");
                    break;
                }
            }
            writer.newLine();
        }
        writer.close();
    }

    public Stack<Integer> solution() throws IllegalMoveException {
        PriorityQueue<State> sortedList = new PriorityQueue<>();
        ArrayList<String> checkedList = new ArrayList<>();
        double h;
        boolean check = false;
        if (size > 6 && board.euclidian() >= 120)
            check = true;
        if (check)
            h = Math.ceil(((double) board.manhattan()) * 0.319) + board.euclidian() + board.linearConflict();
        else {
            if (size > 6)
                h = Math.ceil(((double) board.manhattan()) * 0.01) + board.euclidian() + board.linearConflict();
            else
                h = ((double) board.manhattan()) * 0.5 + board.euclidian() + board.linearConflict();
        }
        board.setH(h);
        sortedList.add(board);
        State parent;
        ArrayList<State> children;
        while (!sortedList.isEmpty()) {
            parent = sortedList.poll();
            checkedList.add(parent.toString());
            if (parent.getH() == 0) {
                goal = parent;
                return parent.getFullPath();
            }
            children = parent.neighbours();
            for (int i = 0; i < children.size(); i++) {
                State child = children.get(i);
                if (check)
                    h = Math.ceil(((double) child.manhattan()) * 0.319) + child.euclidian() + child.linearConflict();
                else {
                    if (size > 6)
                        h = Math.ceil(((double) child.manhattan()) * 0.01) + child.euclidian() + child.linearConflict();
                    else
                        h = ((double) child.manhattan()) * 0.5 + child.euclidian() + child.linearConflict();
                }
                child.setH(h);
                if (!checkedList.contains(child.toString()) && !sortedList.contains(child))
                    sortedList.add(child);
            }
        }
        return goal.getFullPath();
    }

    public static void main(String[] args) throws BadBoardException, IOException, IllegalMoveException {
        System.out.println("current dir: " + System.getProperty("user.dir") + "\n");
        Solver solve = new Solver("/Users/dom1k/IdeaProjects/test/src/testcases/" + args[0]);
        ans(solve, "/Users/dom1k/IdeaProjects/test/src/testcases/" + args[1]);
    }
}