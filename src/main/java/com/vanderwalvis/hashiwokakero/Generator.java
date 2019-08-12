package com.vanderwalvis.hashiwokakero;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Generator {
	
	public static final float DEFAULT_COMPLEXITY = 0.5F;
	
	public static final int DEFAULT_SIZE = 7;
	
	private class Place {
		
		private final int x, y;
		
		private Place(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public boolean equals(Object o) {
			if(o != null && o instanceof Place) {
				Place p = (Place) o;
				return p.x == x && p.y == y;
			}
			
			return false;
		}
	}
	
	private static final int MAX_FAILURES = 10;
	
	private int size, complexity;
	
	private Hashiwokakero game;
	
	private List<Place> closed, collisions;
	
	private Random random;
	
	Generator(float complexity, Hashiwokakero game) {
		this.game = game;
		size = game.getFieldSize();
		this.complexity = Math.max(1, (int) (complexity * size * size));
		
		closed = new ArrayList<>();
		collisions = new ArrayList<>();
		
		random = new Random();
	}
	
	public List<Node> nextLevel() {
		
		List<Node> puzzle = new ArrayList<>();
		
		closed.clear();
		
		{
			int x = random.nextInt(size);
			int y = random.nextInt(size);
			Place p = new Place(x, y);
			Node n = new Node(x, y, 0, game);
			
			puzzle.add(n);
			closed.add(p);
		}
		
		int failures = 0;
		for(int i = 0; i < complexity; i++) {
			Node n1 = puzzle.get(random.nextInt(puzzle.size()));
			
			List<Place> open = getCardinalPlaces(n1);
			List<Node> nodes = new ArrayList<>();
			for(Place p : collisions) {
				int index = puzzle.indexOf(new Node(p.x, p.y, 0, game));
				if(index > -1 && Math.abs(n1.getX() - p.x) + Math.abs(n1.getY() - p.y) > 1) {
					nodes.add(puzzle.get(index));
				}
			}
			
			if(nodes.size() > 0) {
				for(Node n2 : nodes) {
					connect(n1, n2);
				}
			} else if(!open.isEmpty()) {
				failures = 0;
				
				Place p = open.get(random.nextInt(open.size()));
				
				Node n2 = new Node(p.x, p.y, 0, game);
				
				puzzle.add(n2);
				closed.add(p);
				
				connect(n1, n2);
			} else {
				failures++;
				if(failures >= MAX_FAILURES) {
					if(puzzle.size() >= DEFAULT_SIZE) return puzzle;
					return nextLevel();
				} else i--;
			}
		}
		
		return puzzle;
	}
	
	private List<Place> getCardinalPlaces(Node n) {
		List<Place> result = new ArrayList<>();
		collisions.clear();
		
		for(int a = 0; a < 360; a += 90) {
			for(int r = 1; r < size; r++) {
				int x = n.getX() + (int) (r * Math.cos(Math.toRadians(a)));
				int y = n.getY() + (int) (r * Math.sin(Math.toRadians(a)));
				if(x >= 0 && y >= 0 && x < size && y < size) {
					
					Place p = new Place(x, y);
					
					if(closed.contains(p)) {
						collisions.add(p);
						break;
					} else {
						result.add(p);
					}
					
				} else break;
			}
		}
		
		return result;
	}
	
	private void connect(Node n1, Node n2) {
		int amount = random.nextInt(Link.CONNECTION_LIMIT - 1) + 1;
		n2.increaseAmount(amount);
		n1.increaseAmount(amount);
		
		if(n1.getX() == n2.getX()) {
			int x = n1.getX();
			int from = Math.min(n1.getY(), n2.getY());
			int to = Math.max(n1.getY(), n2.getY());
			for(int y = from; y < to; y++) {
				Place p = new Place(x, y);
				closed.add(p);
			}
		} else {
			int y = n1.getY();
			int from = Math.min(n1.getX(), n2.getX());
			int to = Math.max(n1.getX(), n2.getX());
			for(int x = from; x < to; x++) {
				Place p = new Place(x, y);
				closed.add(p);
			}
		}
	}
}
