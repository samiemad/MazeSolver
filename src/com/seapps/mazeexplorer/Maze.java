package com.seapps.mazeexplorer;

import android.graphics.Color;

public class Maze {
	public int numCols, numRows;
	public char[][] data;

	public Maze(int numRows, int numCols, char[][] data) {
		this.numCols = numCols;
		this.numRows = numRows;
		this.data = data;
	}

	@Override
	public String toString() {
		String str = "maze: (" + numRows + " x " + numCols + "):\n";
		for (int r = 0; r < numRows; ++r) {
			for (int c = 0; c < numCols; ++c) {
				str += data[r][c];
			}
			str += "\n";
		}
		return str;
	}

	public int getColor(int position) {
		int r = position / numCols;
		int c = position % numCols;
		return getColor(r, c);
	}

	public int getColor(int r, int c) {
		if (data[r][c] == '#')
			return Color.BLACK;
		else if (data[r][c] == '*')
			return Color.GREEN;
		else
			return Color.parseColor("#f3c87d");
	}

	public int getResourceImage(int position) {
		int r = position / numCols;
		int c = position % numCols;
		return getResourceImage(r, c);
	}

	public int getResourceImage(int r, int c) {
		if (data[r][c] == '#')
			return R.drawable.wall;
		else if (data[r][c] == '*')
			return R.drawable.path;
		else
			return R.drawable.empty;
	}

	public void toggle(int position) {
		int r = position / numCols;
		int c = position % numCols;
		toggle(r, c);
	}

	public void toggle(int r, int c) {
		if (data[r][c] == '.')
			data[r][c] = '#';
		else
			data[r][c] = '.';
	}

	public void reset() {
		for (int r = 0; r < numRows; ++r) {
			for (int c = 0; c < numCols; ++c) {
				if (data[r][c] == '*')
					data[r][c] = '.';
			}
		}
	}
}
