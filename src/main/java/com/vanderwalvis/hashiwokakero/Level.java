package com.vanderwalvis.hashiwokakero;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

public class Level {
	
	private boolean finished;
	
	private final int x, y;
	
	private int drawX, drawY, drawSize, textX, textY;
	
	private Paint paint, backPaint, textPaint;
	
	private String name;
	
	private final LevelSelector selector;
	
	public Level(int x, int y, boolean finished, String name, LevelSelector selector) {
		this.x = x;
		this.y = y;
		this.finished = finished;
		this.name = name;
		this.selector = selector;
		
		recalculateValues();
	}
	
	public void draw(Canvas canvas) {
		canvas.drawCircle(drawX, drawY, drawSize / 2, backPaint);
		canvas.drawCircle(drawX, drawY, drawSize / 2, paint);
		canvas.drawText(name, textX, textY, textPaint);
	}
	
	public void recalculateValues() {
		drawSize = Math.min(selector.getViewWidth(), selector.getViewHeight()) / selector.getFieldSize();
		
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		if(finished) paint.setColor(selector.getValidColor());
		else paint.setColor(selector.getNodeColor());
		paint.setStrokeWidth(drawSize / 10);
		paint.setStyle(Paint.Style.STROKE);
		
		backPaint = new Paint();
		backPaint.setColor(selector.getBackgroundColor());
		
		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setColor(selector.getNodeColor());
		textPaint.setTypeface(Typeface.create("Arial", Typeface.BOLD));
		textPaint.setTextSize(drawSize / 2);
		
		drawX = drawSize * x + drawSize / 2;
		drawY = drawSize * y + drawSize / 2;
		
		int margin = (Math.max(selector.getViewWidth(), selector.getViewHeight()) - Math.min(selector.getViewWidth(), selector.getViewHeight())) / 2;
		if(selector.getWidth() < selector.getHeight()) drawY += margin;
		else drawX += margin;
		
		drawSize *= 0.8;
		
		Rect bounds = new Rect();
		
		textPaint.getTextBounds(name, 0, name.length(), bounds);
		
		textX = drawX - bounds.width() / 2;
		textY = drawY + bounds.height() / 2;
	}
	
	public void finish() {
		finished = true;
		paint.setColor(selector.getValidColor());
	}
	
	public int getDrawX() {
		return drawX;
	}
	
	public int getDrawY() {
		return drawY;
	}
	
	public int getDrawSize() {
		return drawSize;
	}
	
	public String getName() {
		return name;
	}
}
