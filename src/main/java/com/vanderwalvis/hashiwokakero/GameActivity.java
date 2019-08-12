package com.vanderwalvis.hashiwokakero;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import static android.view.View.VISIBLE;

public class GameActivity extends Activity {
	
	private Hashiwokakero game;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_game);
		
		game = findViewById(R.id.game);
		
		Intent intent = getIntent();
		String level = intent.getStringExtra("level");
		
		if(level == null)
			game.loadLevel(intent.getIntExtra("size", Generator.DEFAULT_SIZE), intent.getFloatExtra("complexity", Generator.DEFAULT_COMPLEXITY));
		else game.loadLevel(level);
	}
	
	public void undo(View view) {
		game.undo();
	}
	
	public void finish(View view) {
		game.finish();
	}
	
	public void showCheerMessage() {
		Button undo = findViewById(R.id.undo);
		undo.setEnabled(false);
		
		LinearLayout menu = findViewById(R.id.cheer);
		menu.setVisibility(VISIBLE);
		
		int distance = getWindow().getDecorView().getHeight();
		
		menu.setTranslationY(-distance);
		menu.animate().setDuration(distance / 5);
		menu.animate().translationY(0);
	}
}
