package com.vanderwalvis.hashiwokakero;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class LevelSelector extends View implements View.OnTouchListener {
	
	private int width, height, fieldSize, backgroundColor, nodeColor, validColor;
	
	private List<Level> levels;
	
	public LevelSelector(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setOnTouchListener(this);
		
		levels = new ArrayList<>();
		
		backgroundColor = ContextCompat.getColor(context, R.color.background);
		nodeColor = ContextCompat.getColor(context, R.color.normalNode);
		validColor = ContextCompat.getColor(context, R.color.validNode);
	}
	
	public void setLevelSet(String[] set, SharedPreferences preferences) {
		levels.clear();
		fieldSize = (int) Math.ceil(Math.sqrt(set.length));
		
		for(int i = 0; i < set.length; i++) {
			Level level = new Level(i % fieldSize, i / fieldSize, preferences.getBoolean(set[i], false), set[i], this);
			
			levels.add(level);
		}
	}
	
	public void markAsFinished(String level) {
		for(Level l : levels) {
			if(l.getName().equals(level)) {
				l.finish();
			}
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for(Level l : levels) {
			l.draw(canvas);
		}
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		width = w - getPaddingLeft() - getPaddingRight();
		height = h - getPaddingTop() - getPaddingBottom();
		
		for(Level l : levels) {
			l.recalculateValues();
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_UP) {
			Level level = getLevel(event.getX(), event.getY());
			if(level != null) {
				((LevelActivity) getContext()).startLevel(level.getName());
			}
		}
		
		return true;
	}
	
	public Level getLevel(float x, float y) {
		for(Level l : levels) {
			if(Math.abs(l.getDrawX() - x) <= l.getDrawSize() / 2 && Math.abs(l.getDrawY() - y) < l.getDrawSize() / 2)
				return l;
		}
		
		return null;
	}
	
	public int getViewWidth() {
		return width;
	}
	
	public int getViewHeight() {
		return height;
	}
	
	public int getFieldSize() {
		return fieldSize;
	}
	
	public int getBackgroundColor() {
		return backgroundColor;
	}
	
	public int getNodeColor() {
		return nodeColor;
	}
	
	public int getValidColor() {
		return validColor;
	}
}
