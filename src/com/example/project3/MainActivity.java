package com.example.project3;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends Activity {

    private LocationManager locationManager = null;
    private ActiveListener activeListener = new ActiveListener();
    
    private double latitude;
    private double longitude;
    
    private double initLat = 0;
    private double initLon = 0;
	
	private static final int GOT_COLOR = 1;
	
	private static final String DRAWING = "drawing";
	private static final String STROKE = "stroke";

	private DrawingView drawingView;
	private TextView currentLocation;
	
	private Sensor accelSensor = null;
	private AccelListener accelListener = null;	
	private SensorManager sensorManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
				
		/*
		 * Get Views
		 */
		drawingView = (DrawingView)findViewById(R.id.drawingView1);
		currentLocation = (TextView)findViewById(R.id.curLocation);
        
        /*
         * Make drawing view editable
         */
        drawingView.setEditable(true);
		
        setUI();
        
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
			    drawingView.setCurrentPaintColor(color);
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
	
	
	/*
	 * GPS STUFF
	 */
	
	private void setUI() {
		Log.i("ONLOCATION", "New location registered");
		currentLocation.setText("(" + latitude + ", " + longitude + ")");
	}
	
	   
   private void onLocation(Location location) {
	   if(location == null) {
           return;
       }
       
       latitude = location.getLatitude();
       longitude = location.getLongitude();
       
       if (initLat == 0) initLat = latitude;
       if (initLon == 0) initLon = longitude;
       
       //Send to drawing to draw
       //drawingView.addStroke(latitude-initLat, longitude-initLon);
       
       setUI();
       
   }
	   

   
   private void unregisterListeners() {
       locationManager.removeUpdates(activeListener);
   }
   
   private void registerListeners() {
       unregisterListeners();
       
       // Create a Criteria object
       Criteria criteria = new Criteria();
       criteria.setAccuracy(Criteria.ACCURACY_FINE);
       criteria.setPowerRequirement(Criteria.POWER_HIGH);
       criteria.setAltitudeRequired(true);
       criteria.setBearingRequired(false);
       criteria.setSpeedRequired(false);
       criteria.setCostAllowed(false);
       
       String bestAvailable = locationManager.getBestProvider(criteria, true);
       
       
       if(bestAvailable != null) {
           locationManager.requestLocationUpdates(bestAvailable, 500, 1, activeListener);
           Location location = locationManager.getLastKnownLocation(bestAvailable);
           onLocation(location);
       }
   }
	

  /**
    * The activity is being paused
    */
   @Override
   protected void onPause() {
       unregisterListeners();
       super.onPause();
       if(accelSensor != null) {
    	   sensorManager.unregisterListener(accelListener);
    	   accelListener = null;
    	   accelSensor = null;
    	   }
   }

   /**
    * The activity is being resumed
    */
   @Override
   protected void onResume() {
       super.onResume();
       registerListeners();
       sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
       accelSensor =
    		   sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    		   if(accelSensor != null) {
    		   accelListener = new AccelListener();
    		   sensorManager.registerListener(accelListener,
    		   accelSensor,
    		   SensorManager.SENSOR_DELAY_FASTEST);
    		   }
   }
   
   
   private class ActiveListener implements LocationListener {

       @Override
       public void onLocationChanged(Location location) {
           onLocation(location);
       }

       @Override
       public void onProviderDisabled(String provider) {
           registerListeners();
       }

       @Override
       public void onProviderEnabled(String provider) {
           
       }

       @Override
       public void onStatusChanged(String provider, int status, Bundle extras) {

       }

       
   };
   
   private class AccelListener implements SensorEventListener {
	   @Override
	   public void onAccuracyChanged(Sensor arg0, int arg1) {
	   }
	   @Override
	   public void onSensorChanged(SensorEvent event) {
		   float y = event.values[1];
		   if(y == 0) {
			   
		   } else {
			   
		   }
	   }	
   }

}
