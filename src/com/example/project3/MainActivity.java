package com.example.project3;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends Activity {

	
	private static final int GOT_COLOR = 1;
	
	private static final String DRAWING = "drawing";
	private static final String STROKE = "stroke";

	private DrawingView drawingView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		
				
		/*
		 * Get Views
		 */
		drawingView = (DrawingView)findViewById(R.id.drawingView1);
        
        /*
         * Make drawing view editable
         */
        drawingView.setEditable(true);
		
		/*
         * Restore any state
         */
        if(savedInstanceState != null) {
        	drawingView.getFromBundle(DRAWING, savedInstanceState);
        	drawingView.getFromBundle(STROKE, savedInstanceState);
        }
		            
		            

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	

	
	 @Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
			super.onActivityResult(requestCode, resultCode, data);
			
		    if(requestCode == GOT_COLOR && resultCode == Activity.RESULT_OK) {
			    // This is a color response
			    int color = data.getIntExtra(ColorSelectActivity.COLOR, Color.BLACK);
			    drawingView.SetStrokeColor(color);
		    }
		}

    /**
     * Handle a Color button press
     * @param view
     */
    public void onColor(View view) {
    	Intent intent = new Intent(this, ColorSelectActivity.class);
        startActivityForResult(intent, GOT_COLOR);
    }
    
	public void onPush(View view) {
		//Push picture
		DrawingView drawView = (DrawingView)findViewById(R.id.drawingView1);
				
		ViewSender sender = new ViewSender();
        sender.sendView(this,  drawView, "Dragons");
	}
    
    
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		
		drawingView.putToBundle(DRAWING, outState);
	}
	

}
