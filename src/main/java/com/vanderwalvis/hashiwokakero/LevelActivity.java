package com.vanderwalvis.hashiwokakero;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;

import java.io.IOException;
import java.util.Arrays;

public class LevelActivity extends Activity {
	
	public static final int LEVEL_FINISHED = 777;
	
	private String[] levels;
	
	private LevelSelector selector;
	
	private String lastLevel;
	
	private SharedPreferences preferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_level);
		
		selector = findViewById(R.id.selector);
		
		preferences = getPreferences(Context.MODE_PRIVATE);
		
		AssetManager manager = getAssets();
		try {
			levels = manager.list("levels");
			
			int[] temp = new int[levels.length];
			for(int i = 0; i < temp.length; i++) {
				temp[i] = Integer.parseInt(trimExtension(levels[i]));
			}
			
			Arrays.sort(temp);
			
			for(int i = 0; i < levels.length; i++) {
				levels[i] = Integer.toString(temp[i]);
			}
			
			selector.setLevelSet(levels, preferences);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == LEVEL_FINISHED && resultCode == RESULT_OK) {
			
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(lastLevel, true);
			editor.apply();
			
			selector.markAsFinished(lastLevel);
			
			for(int i = 0; i < levels.length - 1; i++) {
				if(levels[i].equals(lastLevel)) {
					startLevel(levels[i + 1]);
					break;
				}
			}
			
		}
	}
	
	public void startLevel(String level) {
		lastLevel = level;
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtra("level", level);
		startActivityForResult(intent, LEVEL_FINISHED);
	}
	
	private String trimExtension(String file) {
		if(file.length() >= 4) return file.substring(0, file.length() - 4);
		return "";
	}
}
