package com.example.sudoku.view.buttonsgrid;

import com.example.sudoku.GameEngine;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

public class NumberButton extends androidx.appcompat.widget.AppCompatButton implements OnClickListener{

	private int number;
	
	public NumberButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		GameEngine.getInstance().setNumber(number);
	}
	
	public void setNumber(int number){
		this.number = number;
	}
}
