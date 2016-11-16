package com.seapps.mazeexplorer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.seapps.mazeexplorer.R;

public class MyGridAdapter extends BaseAdapter {

	private Maze maze;
	LayoutInflater inflater;
	Context context;

	public MyGridAdapter(Context context, Maze maze) {
		this.maze = maze;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return maze.numCols * maze.numRows;
	}

	@Override
	public Object getItem(int pos) {
		return null;
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		if (v == null)
			v = inflater.inflate(R.layout.grid_item, parent, false);
		ImageView iv = (ImageView) v.findViewById(R.id.image);
		iv.setImageResource(maze.getResourceImage(position));
		// v.setBackgroundColor(maze.getColor(position));
		return v;
	}

}
