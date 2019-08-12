package com.vanderwalvis.hashiwokakero;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Link {
	
	/** 1 more than the maximum amount of connections between two nodes. */
	public static final int CONNECTION_LIMIT = 3;
	
	private int connections, strokeSize;
	
	private Node from, to;
	
	private Paint paint;
	
	private final Hashiwokakero game;
	
	/**
	 * A link connects two nodes anywhere between 0 and {@value #CONNECTION_LIMIT - 1} times.
	 *
	 * @param from the first node connected with this link
	 * @param to   the second node connected with this link
	 * @param game a reference to the game this link is in
	 */
	Link(Node from, Node to, Hashiwokakero game) {
		this.from = from;
		this.to = to;
		this.game = game;
		
		connections = 1;
		
		recalculateValues();
	}
	
	/**
	 * <p>Increase the amount of connections between <i>from</i> and <i>to</i> by one, or remove all
	 * connections if the limit of {@value #CONNECTION_LIMIT} is reached.</p>
	 */
	public void connect() {
		connections = (connections + 1) % CONNECTION_LIMIT;
	}
	
	/**
	 * <p>Decrease the amount of connections between <i>from</i> and <i>to</i> by one, or make
	 * {@value #CONNECTION_LIMIT - 1} connections if this would result in a negative number.</p>
	 *
	 * <p>This function is only used to undo() made connections (see {@link Hashiwokakero}).</p>
	 */
	public void disconnect() {
		connections = (connections + CONNECTION_LIMIT - 1) % CONNECTION_LIMIT;
	}
	
	/**
	 * Reload colors and positions in case the available screen size/orientation changes.
	 */
	public void recalculateValues() {
		strokeSize = Math.min(game.getViewWidth(), game.getViewHeight()) / game.getFieldSize() / 10;
		
		paint = new Paint();
		paint.setColor(game.getNodeColor());
		paint.setStrokeWidth(strokeSize);
	}
	
	public void draw(Canvas canvas) {
		for(int i = 0; i < connections; i++) {
			int value = +i * strokeSize * 2 - (2 * connections - 2) * strokeSize / 2;
			canvas.drawLine(from.getDrawX() + value, from.getDrawY() + value, to.getDrawX() + value, to.getDrawY() + value, paint);
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if(o != null && o instanceof Link) {
			Link link = (Link) o;
			return link.from == from && link.to == to || link.from == to && link.to == from;
		}
		
		return false;
	}
	
	// .~---------------[ Getters ]---------------~. \\
	
	public int getConnections() {
		return connections;
	}
	
	public Node getFrom() {
		return from;
	}
	
	public Node getTo() {
		return to;
	}
	// '~-----------------------------------------~' \\
}
