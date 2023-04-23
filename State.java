package fifteenpuzzle;

import javax.swing.*;
import java.lang.reflect.Array;
import java.util.*;

class State implements Comparable<State>  {
    public final static int UP = 0;
    public final static int DOWN = 1;
    public final static int LEFT = 2;
    public final static int RIGHT = 3;
    private int[][] board;
    private int emptyRow, emptyCol, numMoves, size, lastMove, lastTile, layer;
    private double h;
    private State prev;
    public int getNumMoves(){return numMoves;}
    public void setNumMoves(int numMoves) {this.numMoves = numMoves;}
    public int[][] getBoard(){return board;}
    public int getLastMove(){return lastMove;}
    public State getPrev(){return prev;}
    public int getR(){return emptyRow;}
    public int getC(){return emptyCol;}
    public int getLayer(){return layer;}
    public double getH(){ return h; }
    public void setH(double h) {this.h = h;}
    public State(int[][] board, int size, int emptyRow, int emptyCol, int numMoves, State prev, int lastMove, int lastTile, int layer) {
        this.board = board;
        this.emptyRow = emptyRow;
        this.emptyCol = emptyCol;
        this.numMoves = numMoves;
        this.prev = prev;
        this.size = size;
        this.lastMove = lastMove;
        this.lastTile = lastTile;
        this.layer = layer;
    }

    public int euclidian() {
        int euclidian = 0;
        int num, x, y;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                num = board[i][j];
                if (num != 0) {
                    x = (num - 1) / size;
                    y = (num - 1) % size;
                    euclidian += Math.sqrt(Math.pow(i - x, 2) + Math.pow(j - y, 2));
                }
            }
        }
        return euclidian;
    }

    public int manhattan() {
        int manhattan = 0;
        int correct = 1;
        int num;
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) {
                num = board[i][j];
                if (num != 0 && num != correct)
                    manhattan += Math.abs(i - ((num - 1) / size)) + Math.abs(j - ((num - 1) % size));
                correct++;
            }
        return manhattan;
    }

    public int linearConflict() {
        int linearConflict = 0;
        int[][] rows = new int[size][size];
        int[][] columns = new int[size][size];
        int num;
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) {
                num = board[i][j];
                if (num != 0) {
                    rows[i][j] = (num - 1) / size;
                    columns[i][j] = (num - 1) % size;
                }
                else {
                    rows[i][j] = -1;
                    columns[i][j] = -1;
                }
            }
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) {
                if (rows[i][j] == i)
                    for (int l = j + 1; l < size; l++)
                        if (rows[i][l] == i && board[i][j] > board[i][l])
                            linearConflict += 2;
                if (columns[i][j] == j)
                    for (int l = i + 1; l < size; l++)
                        if (columns[l][j] == j && board[i][j] > board[l][j])
                            linearConflict += 2;
            }
        return linearConflict;
    }
    public int[][] makeMove(int direction) throws IllegalMoveException {
        int[][] s = this.cloneBoard();
        switch (direction) {
            case UP: {
                s[emptyRow][emptyCol] = board[emptyRow + 1][emptyCol];
                s[emptyRow + 1][emptyCol] = 0;
                break;
            }
            case DOWN: {
                s[emptyRow][emptyCol] = board[emptyRow - 1][emptyCol];
                s[emptyRow - 1][emptyCol] = 0;
                break;
            }
            case RIGHT: {
                s[emptyRow][emptyCol] = board[emptyRow][emptyCol - 1];
                s[emptyRow][emptyCol - 1] = 0;
                break;
            }
            case LEFT: {
                s[emptyRow][emptyCol] = board[emptyRow][emptyCol + 1];
                s[emptyRow][emptyCol + 1] = 0;
                break;
            }
            default:
                throw new IllegalMoveException("Unexpected direction: " + direction);
        }
        return s;
    }
    public ArrayList<State> neighbours() throws IllegalMoveException {
        ArrayList<State> neighbours = new ArrayList<>();
        if (emptyRow == 0) {
            if (emptyCol == 0) {
                neighbours.add(new State(makeMove(UP), size, emptyRow + 1, emptyCol, numMoves + 1, this, UP, board[emptyRow + 1][emptyCol], layer));
                neighbours.add(new State(makeMove(LEFT), size, emptyRow, emptyCol + 1, numMoves + 1, this, LEFT, board[emptyRow][emptyCol + 1], layer));
            }
            else if (emptyCol == size - 1) {
                neighbours.add(new State(makeMove(UP), size, emptyRow + 1, emptyCol, numMoves + 1, this, UP, board[emptyRow + 1][emptyCol], layer));
                neighbours.add(new State(makeMove(RIGHT), size, emptyRow, emptyCol - 1, numMoves + 1, this, RIGHT, board[emptyRow][emptyCol - 1], layer));
            }
            else {
                neighbours.add(new State(makeMove(UP), size, emptyRow + 1, emptyCol, numMoves + 1, this, UP, board[emptyRow + 1][emptyCol], layer));
                neighbours.add(new State(makeMove(LEFT), size, emptyRow, emptyCol + 1, numMoves + 1, this, LEFT, board[emptyRow][emptyCol + 1], layer));
                neighbours.add(new State(makeMove(RIGHT), size, emptyRow, emptyCol - 1, numMoves + 1, this, RIGHT, board[emptyRow][emptyCol - 1], layer));
            }
        }
        else if (emptyRow == size - 1) {
            if (emptyCol == 0) {
                neighbours.add(new State(makeMove(DOWN), size, emptyRow - 1, emptyCol, numMoves + 1, this, DOWN, board[emptyRow - 1][emptyCol], layer));
                neighbours.add(new State(makeMove(LEFT), size, emptyRow, emptyCol + 1, numMoves + 1, this, LEFT, board[emptyRow][emptyCol + 1], layer));
            }
            else if (emptyCol == size - 1) {
                neighbours.add(new State(makeMove(RIGHT), size, emptyRow, emptyCol - 1, numMoves + 1, this, RIGHT, board[emptyRow][emptyCol - 1], layer));
                neighbours.add(new State(makeMove(DOWN), size, emptyRow - 1, emptyCol, numMoves + 1, this, DOWN, board[emptyRow - 1][emptyCol], layer));
            }
            else {
                neighbours.add(new State(makeMove(DOWN), size, emptyRow - 1, emptyCol, numMoves + 1, this, DOWN, board[emptyRow - 1][emptyCol], layer));
                neighbours.add(new State(makeMove(LEFT), size, emptyRow, emptyCol + 1, numMoves + 1, this, LEFT, board[emptyRow][emptyCol + 1], layer));
                neighbours.add(new State(makeMove(RIGHT), size, emptyRow, emptyCol - 1, numMoves + 1, this, RIGHT, board[emptyRow][emptyCol - 1], layer));
            }
        }
        else if (emptyCol == 0) {
            neighbours.add(new State(makeMove(DOWN), size, emptyRow - 1, emptyCol, numMoves + 1, this, DOWN, board[emptyRow - 1][emptyCol], layer));
            neighbours.add(new State(makeMove(LEFT), size, emptyRow, emptyCol + 1, numMoves + 1, this, LEFT, board[emptyRow][emptyCol + 1], layer));
            neighbours.add(new State(makeMove(UP), size, emptyRow + 1, emptyCol, numMoves + 1, this, UP, board[emptyRow + 1][emptyCol], layer));
        }
        else if (emptyCol == size - 1) {
            neighbours.add(new State(makeMove(DOWN), size, emptyRow - 1, emptyCol, numMoves + 1, this, DOWN, board[emptyRow - 1][emptyCol], layer));
            neighbours.add(new State(makeMove(RIGHT), size, emptyRow, emptyCol - 1, numMoves + 1, this, RIGHT, board[emptyRow][emptyCol - 1], layer));
            neighbours.add(new State(makeMove(UP), size, emptyRow + 1, emptyCol, numMoves + 1, this, UP, board[emptyRow + 1][emptyCol], layer));
        }
        else {
            neighbours.add(new State(makeMove(DOWN), size, emptyRow - 1, emptyCol, numMoves + 1, this, DOWN, board[emptyRow - 1][emptyCol], layer));
            neighbours.add(new State(makeMove(RIGHT), size, emptyRow, emptyCol - 1, numMoves + 1, this, RIGHT, board[emptyRow][emptyCol - 1], layer));
            neighbours.add(new State(makeMove(LEFT), size, emptyRow, emptyCol + 1, numMoves + 1, this, LEFT, board[emptyRow][emptyCol + 1], layer));
            neighbours.add(new State(makeMove(UP), size, emptyRow + 1, emptyCol, numMoves + 1, this, UP, board[emptyRow + 1][emptyCol], layer));
        }
        return neighbours;
    }

    private String num2str(int i) {
        if (i == 0)
            return "  ";
        else if (i < 10)
            return " " + Integer.toString(i);
        else
            return Integer.toString(i);
    }
    public String toString() {
        String ans = "";
        for (int i = 0; i < size; i++) {
            ans += num2str(board[i][0]);
            for (int j = 1; j < size; j++)
                ans += " " + num2str(board[i][j]);
            ans += "\n";
        }
        ans = ans.substring(0, ans.length() - 1);
        return ans;
    }
    public int[][] cloneBoard() {
        int[][] newBoard = new int[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                newBoard[i][j] = board[i][j];
        return newBoard;
    }
    public Stack<Integer> getPath(){
        Stack<Integer> path = new Stack<Integer>();
        State current = this;
        while(current.prev != null) {
            path.push(current.lastMove);
            current = current.getPrev();
        }
        return path;
    }
    public Stack<Integer> getFullPath(){
        Stack<Integer> path = new Stack<Integer>();
        State current = this;
        while(current.prev != null) {
            path.push(current.lastMove);
            path.push(current.lastTile);
            current = current.getPrev();
        }
        return path;
    }
    @Override
    public int compareTo(State state) {
        if (this.h > state.h)
            return 1;
        else if (this.h < state.h)
            return -1;
        return 0;
    }
}
