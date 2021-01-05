package com.example.sudoku;

import android.app.Activity;
import android.os.Bundle;

import com.example.sudoku.R;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		
		com.example.sudoku.GameEngine.getInstance().createGrid(this);
	}

	private void printSudoku(int Sudoku[][]) {
		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++) {
				System.out.print(Sudoku[x][y] + "|");
			}
			System.out.println();
		}
	}

}
