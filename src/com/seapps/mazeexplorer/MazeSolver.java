package com.seapps.mazeexplorer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import android.util.Log;
import android.util.Pair;

public class MazeSolver {

	private static final int[] M_R = { 0, 0, -1, 1 };
	private static final int[] M_C = { -1, 1, 0, 0 };

	private Maze maze;
	private int R, C;
	private boolean solved;
	private ArrayList<Pair<Integer, Integer>> exits = new ArrayList<>();
	public String error;

	public MazeSolver(Maze maze) {
		this.maze = maze;
		R = maze.numRows;
		C = maze.numCols;
	}

	public boolean solve() {
		findExits();
		if (exits.size() != 2) {
			error = "maze must have exactly 1 entry point and 1 exit point";
			return false;
		}
		bfs(exits.get(0), exits.get(1));
		return solved;
	}

	private void findExits() {
		for (int c = 0; c < maze.numCols; ++c) {
			if (maze.data[0][c] == '.')
				exits.add(new Pair<Integer, Integer>(0, c));
			if (maze.data[maze.numRows - 1][c] == '.')
				exits.add(new Pair<Integer, Integer>(maze.numRows - 1, c));
		}
		for (int r = 1; r < maze.numRows - 1; ++r) {
			if (maze.data[r][0] == '.')
				exits.add(new Pair<Integer, Integer>(r, 0));
			if (maze.data[r][maze.numCols - 1] == '.')
				exits.add(new Pair<Integer, Integer>(r, maze.numCols - 1));
		}
	}

	private void bfs(Pair<Integer, Integer> start, Pair<Integer, Integer> end) {
		Log.d("BFS", "from (" + start.first + "," + start.second + ") to (" + end.first + "," + end.second + ")");
		int par[][] = new int[maze.numRows][maze.numCols];
		Queue<Pair<Integer, Integer>> q = new LinkedList<Pair<Integer, Integer>>();
		q.add(start);
		while (!q.isEmpty()) {
			if (q.peek().equals(end)) {
				break;
			}
			int r = q.peek().first;
			int c = q.peek().second;
			q.remove();
			for (int i = 0; i < 4; ++i) {
				int nr = r + M_R[i];
				int nc = c + M_C[i];
				if (ok(nr, nc) && par[nr][nc] == 0) {
					par[nr][nc] = i + 1;
					q.add(new Pair<Integer, Integer>(nr, nc));
				}
			}
		}
		if (!q.isEmpty()) {
			solved = true;
			int r = end.first, c = end.second;
			while (r != start.first || c != start.second) {
				maze.data[r][c] = '*';
				int move = par[r][c] - 1;
				int nr = r - M_R[move];
				int nc = c - M_C[move];
				Log.d("BFS", "path: (" + r + "," + c + ") --> (" + nr + "," + nc + ") p=" + move);
				r = nr;
				c = nc;
			}
			maze.data[r][c] = '*';
		} else {
			error = "there is no path connecting start and end";
		}
	}

	private boolean ok(int r, int c) {
		return r >= 0 && r < R && c >= 0 && c < C && maze.data[r][c] != '#';
	}
}
