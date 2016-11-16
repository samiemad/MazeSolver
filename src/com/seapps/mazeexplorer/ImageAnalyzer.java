package com.seapps.mazeexplorer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

public class ImageAnalyzer extends AsyncTask<String, String, String> {
	private Bitmap img;
	private Uri uri;
	private Context context;
	private ProgressDialog progress;

	private ArrayList<Cluster> columns = new ArrayList<Cluster>();
	private ArrayList<Cluster> rows = new ArrayList<Cluster>();

	private char[][] data;
	private int numRows, numCols;
	private boolean success = true;

	public static int THRESH;

	public ImageAnalyzer(Context context, Uri uri) {
		this.context = context;
		this.uri = uri;
		progress = new ProgressDialog(context);
		progress.setCancelable(false);
		progress.setMessage("Working...");
		progress.show();
	}

	@Override
	protected String doInBackground(String... params) {
		ContentResolver cr = context.getContentResolver();
		try {
			img = MediaStore.Images.Media.getBitmap(cr, uri);
			THRESH = (img.getWidth() + img.getHeight()) / 150;
			process();
			buildMaze();
			return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		success = false;
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		progress.dismiss();
		if (success) {
			Intent i = new Intent(context, MazeActivity.class);
			context.startActivity(i);
		} else {
			Toast.makeText(context, "Invalid Image!", Toast.LENGTH_LONG).show();
		}
	}

	private void process() {
		columns.add(new Cluster(0, 2));
		columns.add(new Cluster(img.getWidth() - 1, 2));
		Log.d("COLS", "" + columns.get(1).getPos());
		rows.add(new Cluster(0, 2));
		rows.add(new Cluster(img.getHeight() - 1, 2));
		Log.d("ROWS", "" + rows.get(1).getPos());

		for (int x = 0; x < img.getWidth(); x++) {
			int R = 0, G = 0, B = 0;
			for (int y = 0; y < img.getHeight(); ++y) {
				int color = img.getPixel(x, y);
				int r = (color >> 16) & 0xff;
				int g = (color >> 8) & 0xff;
				int b = (color >> 0) & 0xff;
				R += r;
				G += g;
				B += b;
			}
			R /= img.getHeight();
			G /= img.getHeight();
			B /= img.getHeight();

			if (R > 200 && G > 200 && B > 200) {
				addColumn(x);
			}
		}
		for (int y = 0; y < img.getHeight(); ++y) {
			int R = 0, G = 0, B = 0;
			for (int x = 0; x < img.getWidth(); x++) {
				int color = img.getPixel(x, y);
				int r = (color >> 16) & 0xff;
				int g = (color >> 8) & 0xff;
				int b = (color >> 0) & 0xff;
				R += r;
				G += g;
				B += b;
			}
			R /= img.getWidth();
			G /= img.getWidth();
			B /= img.getWidth();

			if (R > 200 && G > 200 && B > 200) {
				addRow(y);
			}
		}
	}

	private void buildMaze() {
		Collections.sort(rows);
		Collections.sort(columns);
		ArrayList<Cluster> nRows = new ArrayList<Cluster>();
		ArrayList<Cluster> nColumns = new ArrayList<Cluster>();
		Log.d("BuildMaze:", "img = " + img.getWidth() + " x " + img.getHeight());
		Log.d("BuildMaze:", rows.size() + " rows+1, and " + columns.size() + " columns+1");
		for (Cluster c : rows) {
			Log.d("ROWS", c.getPos() + " x" + c.getNum());
			if (c.getNum() > 1) {
				nRows.add(c);
				Log.d("ROWS", c.getPos() + " x" + c.getNum() + " ADDED!");
			}
		}
		for (Cluster c : columns) {
			Log.d("COLS", c.getPos() + " x" + c.getNum());
			if (c.getNum() > 1) {
				nColumns.add(c);
				Log.d("COLS", c.getPos() + " x" + c.getNum() + " ADDED!");
			}
		}
		rows = nRows;
		columns = nColumns;

		numRows = rows.size() - 1;
		numCols = columns.size() - 1;
		if (numRows < 3 || numCols < 3) {
			success = false;
			return;
		}

		data = new char[numRows][numCols];
		for (int r = 0; r < numRows; ++r) {
			for (int c = 0; c < numCols; ++c) {
				data[r][c] = getCell(columns.get(c).getPos(), rows.get(r).getPos(), columns.get(c + 1).getPos(),
						rows.get(r + 1).getPos());
			}
		}
		MazeActivity.maze = new Maze(numRows, numCols, data);
	}

	private char getCell(int x1, int y1, int x2, int y2) {
		int color = img.getPixel((x1 + x2) / 2, (y1 + y2) / 2);
		int r = (color >> 16) & 0xff;
		int g = (color >> 8) & 0xff;
		int b = (color >> 0) & 0xff;
		if (r < 100 && b < 100) {
			return '#';
		} else {
			return '.';
		}
	}

	// private boolean white(int color) {
	// int r = (color >> 16) & 0xff;
	// int g = (color >> 8) & 0xff;
	// int b = (color >> 0) & 0xff;
	// return r > 200 && g > 200 && b > 200;
	// }

	// private void addCell(int x, int y) {
	// for (int xx : columns) {
	// if (same(xx, x)) {
	// return;
	// }
	// }
	// for (int yy : rows) {
	// if (same(yy, y)) {
	// return;
	// }
	// }
	// Log.d("IA", "Adding " + x + "," + y);
	// columns.add(x);
	// rows.add(y);
	// }

	private void addColumn(int x) {
		// Log.d("Col", "Adding column " + x);
		for (Cluster xx : columns) {
			if (xx.same(x)) {
				xx.add(x);
				return;
			}
		}
		columns.add(new Cluster(x));
	}

	private void addRow(int y) {
		// Log.d("Row", "Adding row " + y);
		for (Cluster yy : rows) {
			if (yy.same(y)) {
				yy.add(y);
				return;
			}
		}
		rows.add(new Cluster(y));
	}
}
