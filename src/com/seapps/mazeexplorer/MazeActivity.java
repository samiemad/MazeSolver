package com.seapps.mazeexplorer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.seapps.mazeexplorer.R;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

public class MazeActivity extends AppCompatActivity implements OnItemClickListener {

	private Button bSolve;
	private GridView grid;
	public static Maze maze;
	private MyGridAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maze);

		bSolve = (Button) findViewById(R.id.bSolve);
		Log.d("MazeActivity", "maze = " + maze);

		grid = (GridView) findViewById(R.id.gridMaze);
		grid.setNumColumns(maze.numCols);
		adapter = new MyGridAdapter(this, maze);
		grid.setAdapter(adapter);
		grid.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		clickReset(view);
		maze.toggle(pos);
		adapter.notifyDataSetChanged();
	}

	public void clickSolve(View v) {
		MazeSolver solver = new MazeSolver(maze);
		if (solver.solve()) {
			adapter.notifyDataSetChanged();
			// grid.setOnItemClickListener(null);
			bSolve.setVisibility(View.GONE);
		} else {
			Toast.makeText(this, "Error:" + solver.error, Toast.LENGTH_LONG).show();
		}
	}

	public void clickReset(View v) {
		maze.reset();
		adapter.notifyDataSetChanged();
		// grid.setOnItemClickListener(this);
		bSolve.setVisibility(View.VISIBLE);
	}
}
