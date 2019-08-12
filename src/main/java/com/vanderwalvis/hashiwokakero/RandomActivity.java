package com.vanderwalvis.hashiwokakero;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class RandomActivity extends Activity {
	
	private TextView sizeTip, complexityTip;
	
	private SeekBar size, complexity;
	
	private String previousSize, previousComplexity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_random);
		
		size = findViewById(R.id.size);
		complexity = findViewById(R.id.complexity);
		
		sizeTip = findViewById(R.id.sizeTip);
		complexityTip = findViewById(R.id.complexityTip);
		
		String startSizeTip = getSizeTip(size.getProgress() + 3);
		String startComplexityTip = getComplexityTip(complexity.getProgress() + 1);
		
		sizeTip.setText((sizeTip.getText() + startSizeTip));
		complexityTip.setText((complexityTip.getText() + startComplexityTip));
		
		previousSize = startSizeTip;
		previousComplexity = startComplexityTip;
		
		size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				String size = getSizeTip(progress + 3);
				sizeTip.setText((sizeTip.getText().subSequence(0, sizeTip.length() - previousSize.length()) + size));
				previousSize = size;
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});
		
		complexity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				String complexity = getComplexityTip(progress + 1);
				complexityTip.setText((complexityTip.getText().subSequence(0, complexityTip.length() - previousComplexity.length()) + complexity));
				previousComplexity = complexity;
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == LevelActivity.LEVEL_FINISHED && resultCode == RESULT_OK) {
			startLevel(null);
		}
	}
	
	public void startLevel(View view) {
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtra("size", size.getProgress() + 3);
		intent.putExtra("complexity", (float) (complexity.getProgress() + 1) / 10);
		startActivityForResult(intent, LevelActivity.LEVEL_FINISHED);
	}
	
	public String getSizeTip(int size) {
		return ": " + size + " x " + size;
	}
	
	public String getComplexityTip(int complexity) {
		return ": " + complexity;
	}
}
