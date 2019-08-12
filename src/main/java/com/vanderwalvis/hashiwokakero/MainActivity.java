package com.vanderwalvis.hashiwokakero;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void toLevelSelection(View view) {
		Intent intent = new Intent(this, LevelActivity.class);
		startActivity(intent);
	}
	
	public void toLevelSettings(View view) {
		Intent intent = new Intent(this, RandomActivity.class);
		startActivity(intent);
	}
}
