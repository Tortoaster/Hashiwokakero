package com.vanderwalvis.hashiwokakero;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Hashiwokakero extends View implements View.OnTouchListener {
	
	boolean editable;
	
	private int width, height, fieldSize, backgroundColor, nodeColor, validColor, invalidColor;
	
	private Node selected;
	
	private List<Node> field;
	private List<Link> links;
	private List<Link> history;
	
	private AssetManager manager;
	
	public Hashiwokakero(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setOnTouchListener(this);
		
		backgroundColor = ContextCompat.getColor(context, R.color.background);
		nodeColor = ContextCompat.getColor(context, R.color.normalNode);
		validColor = ContextCompat.getColor(context, R.color.validNode);
		invalidColor = ContextCompat.getColor(context, R.color.invalidNode);
		
		field = new ArrayList<>();
		links = new ArrayList<>();
		history = new ArrayList<>();
		
		manager = context.getAssets();
		
		editable = true;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for(Link l: links) {
			l.draw(canvas);
		}
		for(Node n: field) {
			n.draw(canvas);
		}
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		width = w - getPaddingLeft() - getPaddingRight();
		height = h - getPaddingTop() - getPaddingBottom();
		
		for(Node n: field) {
			n.recalculateValues();
			n.recalculateColors();
		}
		
		for(Link l: links) {
			l.recalculateValues();
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			selected = getNode(event.getX(), event.getY());
		} else if(event.getAction() == MotionEvent.ACTION_UP) {
			if(selected != null) {
				int hor = (int) event.getX() - selected.getDrawX();
				int ver = (int) event.getY() - selected.getDrawY();
				
				if(Math.max(Math.abs(hor), Math.abs(ver)) >= selected.getDrawSize() / 2) {
					Node n = getNode(selected, hor, ver);
					
					if(n != null && editable) addConnection(selected, n);
					
					invalidate();
				}
			}
		}
		
		return true;
	}
	
	public void loadLevel(int size, float complexity) {
		fieldSize = size;
		
		Generator generator = new Generator(complexity, this);
		
		field = generator.nextLevel();
	}
	
	public void loadLevel(String name) {
		field.clear();
		
		try {
			InputStream in = manager.open("levels" + File.separator + name + ".txt");
			
			String line = "";
			char c;
			while((c = (char) in.read()) != '\n') {
				line += c;
			}
			fieldSize = Integer.parseInt(line.trim());
			
			for(int y = 0; y < fieldSize; y++) {
				for(int x = 0; x < fieldSize; x++) {
					int amount = Character.getNumericValue((char) in.read());
					if(amount > 0) {
						field.add(new Node(x, y, amount, this));
					}
				}
				in.read();
				in.read();
			}
		} catch(IOException e) {
			e.printStackTrace();
			fieldSize = 7;
			loadLevel(Generator.DEFAULT_SIZE, Generator.DEFAULT_COMPLEXITY);
		}
	}
	
	public void undo() {
		if(!history.isEmpty()) {
			int index = history.size() - 1;
			Link link = history.get(index);
			
			link.disconnect();
			link.getFrom().recalculateColors();
			link.getTo().recalculateColors();
			
			history.remove(index);
			
			invalidate();
		}
	}
	
	public void finish() {
		GameActivity activity = (GameActivity) getContext();
		activity.setResult(Activity.RESULT_OK);
		activity.finish();
	}
	
	public boolean won() {
		Set<Node> visited = new HashSet<>();
		List<Node> frontier = new LinkedList<>();
		
		Node initial = field.get(0);
		
		if(!initial.isSatiated()) return false;
		
		frontier.add(initial);
		visited.add(initial);
		
		while(!frontier.isEmpty()) {
			Node node = frontier.remove(0);
			List<Node> neighbors = getNeighbors(node);
			for(Node n: neighbors) {
				if(!n.isSatiated()) return false;
				
				if(!visited.contains(n)) {
					frontier.add(n);
					visited.add(n);
				}
			}
			
			if(visited.size() == field.size()) return true;
		}
		
		return false;
	}
	
	public List<Node> getNeighbors(Node n) {
		List<Node> neighbors = new LinkedList<>();
		
		for(Link l: links) {
			if(l.getConnections() > 0) {
				if(l.getFrom().equals(n))
					neighbors.add(l.getTo());
				else if(l.getTo().equals(n))
					neighbors.add(l.getFrom());
			}
		}
		
		return neighbors;
	}
	
	public void addConnection(Node n1, Node n2) {
		Link link = new Link(n1, n2, this);
		int index = links.indexOf(link);
		
		if(index > -1) {
			Link l = links.get(index);
			l.connect();
			history.add(l);
		} else {
			links.add(link);
			history.add(link);
		}
		
		n1.recalculateColors();
		n2.recalculateColors();
		
		if(n1.isSatiated() && n2.isSatiated() && won()) {
			editable = false;
			((GameActivity) getContext()).showCheerMessage();
		}
	}
	
	public int getDegree(Node n) {
		int degree = 0;
		
		for(Link l: links)
			if(l.getFrom().equals(n) || l.getTo().equals(n))
				degree += l.getConnections();
		
		return degree;
	}
	
	public Link getLink(int x, int y) {
		for(Link l: links) {
			if(l.getConnections() > 0) {
				Node n1 = l.getFrom();
				Node n2 = l.getTo();
				
				if(n1.getX() == n2.getX() && n1.getX() == x) {
					if(y >= Math.min(n1.getY(), n2.getY()) && y < Math.max(n1.getY(), n2.getY())) {
						return l;
					}
				} else if(n1.getY() == y) {
					if(x >= Math.min(n1.getX(), n2.getX()) && x < Math.max(n1.getX(), n2.getX())) {
						return l;
					}
				}
			}
		}
		
		return null;
	}
	
	public Node getNode(int x, int y) {
		for(Node n: field) {
			if(n.getX() == x && n.getY() == y) return n;
		}
		
		return null;
	}
	
	public Node getNode(float x, float y) {
		for(Node n: field) {
			if(Math.abs(n.getDrawX() - x) <= n.getDrawSize() / 2 && Math.abs(n.getDrawY() - y) < n.getDrawSize() / 2) return n;
		}
		
		return null;
	}
	
	public Node getNode(Node n, int hor, int ver) {
		if(Math.abs(hor) > Math.abs(ver)) {
			int dir = Integer.signum(hor);
			int y = n.getY();
			for(int x = n.getX() + dir; x >= 0 && x < fieldSize; x += dir) {
				Node node = getNode(x, y);
				if(node != null) return node;
				Link l = getLink(x, y);
				if(l != null && l.getFrom() != n && l.getTo() != n) return null;
			}
		} else {
			int dir = Integer.signum(ver);
			int x = n.getX();
			for(int y = n.getY() + dir; y >= 0 && y < fieldSize; y += dir) {
				Node node = getNode(x, y);
				if(node != null) return node;
				Link l = getLink(x, y);
				if(l != null && l.getFrom() != n && l.getTo() != n) return null;
			}
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
	
	public int getInvalidColor() {
		return invalidColor;
	}
}
