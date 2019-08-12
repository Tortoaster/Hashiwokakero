package com.vanderwalvis.hashiwokakero;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

public class Node {
	
	private final int x, y;
	
	private int amount, drawX, drawY, drawSize, textX, textY;
	
	private Paint paint, backPaint, textPaint;
	
	private final Hashiwokakero game;
	
	Node(int x, int y, int amount, Hashiwokakero game) {
		this.x = x;
		this.y = y;
		this.amount = amount;
		this.game = game;
		
		recalculateValues();
	}
	
	public void recalculateColors() {
		int degree = game.getDegree(this);
		
		if(degree < amount) {
			paint.setColor(game.getNodeColor());
		} else if(degree == amount) {
			paint.setColor(game.getValidColor());
		} else {
			paint.setColor(game.getInvalidColor());
		}
	}
	
	public void draw(Canvas canvas) {
		canvas.drawCircle(drawX, drawY, drawSize / 2, backPaint);
		canvas.drawCircle(drawX, drawY, drawSize / 2, paint);
		canvas.drawText(Integer.toString(amount), textX, textY, textPaint);
	}
	
	@Override
	public boolean equals(Object o) {
		if(o != null && o instanceof Node) {
			Node node = (Node) o;
			return node.x == x && node.y == y;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return y * game.getFieldSize() + x;
	}
	
	public boolean isSatiated() {
		return paint.getColor() == game.getValidColor();
	}
	
	public void recalculateValues() {
		drawSize = Math.min(game.getViewWidth(), game.getViewHeight()) / game.getFieldSize();
		
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(game.getNodeColor());
		paint.setStrokeWidth(drawSize / 10);
		paint.setStyle(Paint.Style.STROKE);
		
		backPaint = new Paint();
		backPaint.setColor(game.getBackgroundColor());
		
		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setColor(game.getNodeColor());
		textPaint.setTypeface(Typeface.create("Arial", Typeface.BOLD));
		textPaint.setTextSize(drawSize / 2);
		
		drawX = drawSize * x + drawSize / 2;
		drawY = drawSize * y + drawSize / 2;
		
		int margin = (Math.max(game.getViewWidth(), game.getViewHeight()) - Math.min(game.getViewWidth(), game.getViewHeight())) / 2;
		if(game.getWidth() < game.getHeight()) drawY += margin;
		else drawX += margin;
		
		drawSize *= 0.8;
		
		Rect bounds = new Rect();
		String content = Integer.toString(amount);
		
		textPaint.getTextBounds(content, 0, content.length(), bounds);
		
		textX = drawX - bounds.width() / 2;
		textY = drawY + bounds.height() / 2;
	}
	
	public void increaseAmount(int amount) {
		this.amount += amount;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
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
}
