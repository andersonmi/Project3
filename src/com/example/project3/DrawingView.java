package com.example.project3;

import java.io.Serializable;
import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {
	//guessing mode
	private boolean editable = true;
	
	/**
	 * The current drawing
	 */
	private Drawing drawing;
	
	private static final String STROKE = "stroke";
	
	/**
	 * The current drawing
	 */
	private Stroke stroke;
	
	/**
	 * Paint to use when drawing
	 */
	transient private Paint drawingPaint;

	/**
	 * First touch status
	 */
	private Touch touch1 = new Touch();

	/**
	 * Second touch status
	 */
	private Touch touch2 = new Touch();
	
	private class Touch {
	    /**
	     * Touch id
	     */
	    public int id = -1;
	    
	    /**
	     * Current x location
	     */
	    public float x = 0;
	    
	    /**
	     * Current y location
	     */
	    public float y = 0;  
	    
	    /**
	     * Previous x location
	     */
	    public float lastX = 0;
	    
	    /**
	     * Previous y location
	     */
	    public float lastY = 0;
	    
	    /**
	     * Copy the current values to the previous values
	     */
	    public void copyToLast() {
	        lastX = x;
	        lastY = y;
	    }
	    
	    /**
	     * Change in x value from previous
	     */
	    public float dX = 0;
	    
	    /**
	     * Change in y value from previous
	     */
	    public float dY = 0;
	    
	    /**
	     * Compute the values of dX and dY
	     */
	    public void computeDeltas() {
	        dX = x - lastX;
	        dY = y - lastY;
	    }
	}
	
	/**
     * The actual class that does all the finger hand drawings
     * 
     */
	private static class Drawing implements Serializable{
		
		 /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
         * X location of drawing
         */
        public float X = 0;
        
        /**
         * Y location of drawing
         */
        public float Y = 0;
        
        /**
         * Drawing scale
         */
        public float scale = 1;
        
        /**
         * Drawing angle
         */
        public float angle = 0;
        
        boolean manipulateDrawing = false;
	}
	
	
	
	/**
     * 		Store all the strokes that make up the drawing into a array
     * 		so that I can iterate through them later to draw
     */
	public ArrayList<Stroke> strokeList = new ArrayList<Stroke>();
	
	
	/**
     * 		The stroke class
     * 		
     */
	public class Stroke{


		/**
	     * initial stroke color
	     */
	    public int strokeColor;
	    
	    
	    /**
	     * Initial stroke width
	     */
	    public float strokeWidth;
	    
	    /**
	     * Array to store all the points making up the stroke
	     */
	    
	    public ArrayList<Point> strokePoints = new ArrayList<Point>();
		
	}
	
	/**
     * Point class
     */
	  public class Point {
			float X;
			float Y;
		}

	public DrawingView(Context context) {
		super(context);
		init(context);
		// TODO Auto-generated constructor stub
	}

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		// TODO Auto-generated constructor stub
	}

	public DrawingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		// TODO Auto-generated constructor stub
	}

	/**
     * Initialize the view
     * @param context
     */
    private void init(Context context) {
    	drawing = new Drawing();
    	
    	stroke = new Stroke();
    	stroke.strokeColor = Color.BLACK;
    	stroke.strokeWidth = 8;
    	
        drawingPaint = new Paint();
        drawingPaint.setColor(stroke.strokeColor);
        drawingPaint.setStrokeWidth(stroke.strokeWidth);
    }
    
    public void SetStrokeColor(int color) {
    	//stroke.strokeColor = color;
    	drawingPaint.setColor(color);

       
    }
    
    public void SetStrokeWidth(float width) { 
    	//stroke.strokeWidth = width;
		drawingPaint.setStrokeWidth(width);
    }
    
    @Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();

		canvas.translate(drawing.X, drawing.Y);
		canvas.rotate(drawing.angle);
		canvas.scale(drawing.scale, drawing.scale);
		
	
		//Here, I'll iterate through the stroke, then the points of that stroke in order to daw
		//Right now it's just going through the points, and always drawing line to (400, 100)
		for (Stroke singleStroke : strokeList)
		{
			drawingPaint.setColor(singleStroke.strokeColor);
			drawingPaint.setStrokeWidth(singleStroke.strokeWidth);
			Point prevPoint = null;	
			for (Point singlePoint : singleStroke.strokePoints)
			{
				if(prevPoint == null)
				{
					prevPoint = singlePoint;
					canvas.drawPoint(prevPoint.X,  prevPoint.Y, drawingPaint);
				}
				else
				{
					canvas.drawLine(prevPoint.X,  prevPoint.Y, singlePoint.X, singlePoint.Y, drawingPaint);
					prevPoint = singlePoint;
				}
			}
			
		}

		canvas.restore();
	}
    
    /**
     * Handle a touch event
     * @param event The touch event
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int id = event.getPointerId(event.getActionIndex());
        
        switch(event.getActionMasked()) {
        case MotionEvent.ACTION_DOWN:
        	 touch1.id = id;
             touch2.id = -1;
             stroke = new Stroke();
             stroke.strokeColor = drawingPaint.getColor();
             stroke.strokeWidth = drawingPaint.getStrokeWidth();
             getPositions(event);
             touch1.copyToLast();
             if(stroke.strokePoints.size() > 0)
             {
            	 strokeList.add(stroke);
             }
             return true;
            
        case MotionEvent.ACTION_POINTER_DOWN:
        	if(touch1.id >= 0 && touch2.id < 0) {
                touch2.id = id;
                getPositions(event);
                touch2.copyToLast();
                return true;
            }
            break;
            
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
        	touch1.id = -1;
            touch2.id = -1;
            invalidate();
            return true;
            
        case MotionEvent.ACTION_POINTER_UP:
        	if(id == touch2.id) {
                touch2.id = -1;
            } 
        	else if(id == touch1.id) {
                // Make what was touch2 now be touch1 by 
                // swapping the objects.
                Touch t = touch1;
                touch1 = touch2;
                touch2 = t;
                touch2.id = -1;
            }
            invalidate();
            return true;
            
        case MotionEvent.ACTION_MOVE:
        	getPositions(event);
            if(getManipulation() || !getIsEditable()){
            	rotateScale();
            	move();
            }
            return true;
        }
        
        return super.onTouchEvent(event);
    }
    

	/**
     * Get the positions for the two touches and put them
     * into the appropriate touch objects.
     * @param event the motion event
     */
    private void getPositions(MotionEvent event) {
        for(int i=0;  i<event.getPointerCount();  i++) {
            
            // Get the pointer id
            int id = event.getPointerId(i);
            
            // Get coordinates
            float x = event.getX(i);
            float y = event.getY(i);
            
            if(id == touch1.id) 
            {
            	touch1.copyToLast();
                touch1.x = x;
                touch1.y = y;
                //Store the location of each touch and push it into the array of points
                //making up the stroke
                if (touch2.id < 0 && getIsEditable() && !getManipulation())
                {
	                Point myPoint = new Point();
	                
	                // Compute the radians angle
	                double rAngle = Math.toRadians(-drawing.angle);
	                float ca = (float) Math.cos(rAngle);
	                float sa = (float) Math.sin(rAngle);
	                
	                float tranRotX = (touch1.x - drawing.X)*ca - (touch1.y - drawing.Y)*sa;
	                float tranRotY = (touch1.x - drawing.X)*sa + (touch1.y - drawing.Y)*ca;		
	                
	                myPoint.X =  tranRotX/drawing.scale;
	                myPoint.Y = tranRotY/drawing.scale;
	                
	                stroke.strokePoints.add(myPoint);
                }
                
            } 
            else if(id == touch2.id) 
            {
            	touch2.copyToLast();
                touch2.x = x;
                touch2.y = y;
            }
        }
        
        invalidate();
    }

    private void rotateScale() {
    	boolean stop = false;
    	if(getIsEditable() && !getManipulation())
    	{
    		stop = true;
    	}
    	if(touch2.id < 0 || touch1.id < 0 || stop) { 
            return;
        }
    	else
    	{
    		// Two touches
            
            /*
             * Rotation
             */
            float angle1 = angle(touch1.lastX, touch1.lastY, touch2.lastX, touch2.lastY);
            float angle2 = angle(touch1.x, touch1.y, touch2.x, touch2.y);
            float da = angle2 - angle1;
            rotate(da, touch1.x, touch1.y);
            
            /*
             * Scaling
             */
            float dist1 = distance(touch1.lastX, touch1.lastY, touch2.lastX, touch2.lastY);
            float dist2 = distance(touch1.x, touch1.y, touch2.x, touch2.y);
            drawing.scale *= dist2/dist1;
    	}
		
	}
    
    /**
     * Handle movement of the touches
     */
    private void move() {  
        if(touch1.id >= 0) {
            // 1 touch
            // We are moving
            touch1.computeDeltas();
            
            drawing.X += touch1.dX;
            drawing.Y += touch1.dY;
        }
        else{
        	return;
        }
    }

	/**
     * Rotate the image around the point x1, y1
     * @param dAngle Angle to rotate in degrees
     * @param x1 rotation point x
     * @param y1 rotation point y
     */
    public void rotate(float dAngle, float x1, float y1) {
        drawing.angle += dAngle;
        
        // Compute the radians angle
        double rAngle = Math.toRadians(dAngle);
        float ca = (float) Math.cos(rAngle);
        float sa = (float) Math.sin(rAngle);
        float xp = (drawing.X - x1) * ca - (drawing.Y - y1) * sa + x1;
        float yp = (drawing.X - x1) * sa + (drawing.Y - y1) * ca + y1;

        drawing.X = xp;
        drawing.Y = yp;
    }
    
    /**
     * Determine the angle for two touches
     * @param x1 Touch 1 x
     * @param y1 Touch 1 y
     * @param x2 Touch 2 x
     * @param y2 Touch 2 y
     * @return computed angle in degrees
     */
    private float angle(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
      
        return (float) Math.toDegrees(Math.atan2(dy, dx));
        
    }
    
    /**
     * Determine the angle for two touches
     * @param x1 Touch 1 x
     * @param y1 Touch 1 y
     * @param x2 Touch 2 x
     * @param y2 Touch 2 y
     * @return computed angle in degrees
     */
    private float distance(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }
    
    /**
     * Save the view state to a bundle
     * @param key key name to use in the bundle
     * @param bundle bundle to save to
     */
    public void putToBundle(String key, Bundle bundle) {
    	bundle.putSerializable(key, drawing);
    	int strokeCnt = 0;
    	for(Stroke singleStroke : strokeList){
	    	float[] simpleStroke = new float[singleStroke.strokePoints.size() * 4];
	    	for( int i = 0; i < singleStroke.strokePoints.size(); i++)
	    	{
	    		simpleStroke[i*4] = singleStroke.strokePoints.get(i).X;
	    		simpleStroke[i*4+1] = singleStroke.strokePoints.get(i).Y;
	    		simpleStroke[i*4+2] = singleStroke.strokeColor;
	    		simpleStroke[i*4+3] = singleStroke.strokeWidth;
	    	}
	    	bundle.putFloatArray(STROKE + strokeCnt, simpleStroke);
	    	
	    	strokeCnt++;
    	}
    	bundle.putInt("numberOfStrokes", strokeCnt-1);
    }
    /**
     * Get the view state from a bundle
     * @param key key name to use in the bundle
     * @param bundle bundle to load from
     */
    public void getFromBundle(String key, Bundle bundle) {
    	if(key == "drawing"){
    	drawing = (Drawing)bundle.getSerializable(key);
    	}
    	else{
    		strokeList = new ArrayList<Stroke>();
    		int total = (int)bundle.getInt("numberOfStrokes")+1;
    		for(int y = 0; y< total; y++)
    		{   			
		    	float[] strokeFound = (float[])bundle.getFloatArray(key + y);
		    	if(strokeFound!= null)
		    	{
		    		stroke = new Stroke();
	    			//stroke.strokeColor = drawingPaint.getColor();
	               // stroke.strokeWidth = drawingPaint.getStrokeWidth();
	                
			    	for( int i = 0; i < strokeFound.length/4; i++)
			    	{
			    		Point pointFound = new Point();
			    		pointFound.X = (float)strokeFound[i*4];
			    		pointFound.Y = (float)strokeFound[i*4 +1];
			    		stroke.strokeColor = (int) strokeFound[i*4+2];
			    		stroke.strokeWidth = (float)strokeFound[i*4+3];
			    		stroke.strokePoints.add(pointFound);
			    		
			    	}
			    	if(stroke.strokePoints.size() > 0)
			        {
			    		strokeList.add(stroke);
			        }
		    	}
    		}
    	}
        
    }
    
    /**
     * Save the view state to an intent
     * @param key key name to use in the bundle
     * @param bundle bundle to save to
     */
    public void putExtraToIntent(String key, Intent intent) {
    	intent.putExtra(key, drawing);
    	int strokeCnt = 0;
    	for(Stroke singleStroke : strokeList){
	    	float[] simpleStroke = new float[singleStroke.strokePoints.size() * 4];
	    	for( int i = 0; i < singleStroke.strokePoints.size(); i++)
	    	{
	    		simpleStroke[i*4] = singleStroke.strokePoints.get(i).X;
	    		simpleStroke[i*4+1] = singleStroke.strokePoints.get(i).Y;
	    		simpleStroke[i*4+2] = singleStroke.strokeColor;
	    		simpleStroke[i*4+3] = singleStroke.strokeWidth;
	    	}
	    	intent.putExtra(STROKE + strokeCnt, simpleStroke);
	    	
	    	strokeCnt++;
    	}
    	intent.putExtra("numberOfStrokes", strokeCnt-1);
    }

	public void setEditable(boolean edit) {
		editable = edit;
		
	}
	
	public boolean getIsEditable() {
		return editable;
		
	}

	public void setManipulation(boolean manipulate) {
		drawing.manipulateDrawing = manipulate;
		
		
	}
	
	public boolean getManipulation() {
		return drawing.manipulateDrawing;
		
	}

	public void setAllDrawing(Serializable drawingObj, ArrayList<Stroke> listofStrokes) {
		if(drawingObj != null){
	    	drawing = (Drawing)drawingObj;
	    	}

		strokeList = listofStrokes;
		
	}
	

}