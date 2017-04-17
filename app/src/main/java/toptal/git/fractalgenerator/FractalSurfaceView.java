////////
// This sample is published as part of the blog article at www.toptal.com/blog
// Visit www.toptal.com/blog and subscribe to our newsletter to read great posts
////////

package toptal.git.fractalgenerator;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class FractalSurfaceView extends GLSurfaceView {

    private final FractalRenderer mRenderer;
    private ScaleGestureDetector mDetector;

    public FractalSurfaceView(Context context){
        super(context);
        setEGLContextClientVersion(2);

        mRenderer = new FractalRenderer();
        mDetector = new ScaleGestureDetector(getContext(), new ScaleListener());

        setRenderer(mRenderer);

        //RENDERMODE_WHEN_DIRTY will only render on creation and with explicit calls to requestRender()
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    // Position represents focus while twoFingers is true and previous position otherwise
    float mPreviousX;
    float mPreviousY;
    int lastNumFingers = 0;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        mDetector.onTouchEvent(e);
        if(e.getActionIndex()>1){
            return true;
        }

        int numFingers = e.getPointerCount();
        switch (e.getAction()&MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                mPreviousX = 0.0f;
                mPreviousY = 0.0f;

                //Get the average of the fingers on the screen as the current position
                for(int i =0;i<numFingers;i++){
                    mPreviousX+=e.getX(i);
                    mPreviousY+=e.getY(i);
                }

                mPreviousX/=numFingers;
                mPreviousY/=numFingers;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mPreviousX = 0.0f;
                mPreviousY = 0.0f;

                //Get the average of the remaining fingers on the screen as the current position
                for(int i =0;i<numFingers;i++){
                    if(i==e.getActionIndex())continue;
                    mPreviousX+=e.getX(i);
                    mPreviousY+=e.getY(i);
                    //Log.d("FractalSurfaceView","Pointer Up: " + String.valueOf(e.get)+ ", " +String.valueOf(e.getActionIndex()));
                }
                numFingers-=1;
                mPreviousX/=numFingers;
                mPreviousY/=numFingers;
                break;

            case MotionEvent.ACTION_MOVE:
                float tempX = 0.0f, tempY=0.0f;

                //Get the average of the fingers on the screen as the current position
                for(int i =0;i<numFingers;i++){
                    tempX+=e.getX(i);
                    tempY+=e.getY(i);
                }

                tempX/=numFingers;
                tempY/=numFingers;

                if(lastNumFingers==numFingers){
                    //Sometimes a third finger doesn't register under point, so track it separately
                    mRenderer.add(tempX - mPreviousX, tempY - mPreviousY);
                }

                mPreviousX=tempX;
                mPreviousY=tempY;

                requestRender();
                break;
        }
        lastNumFingers = numFingers;
        return true;

    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mRenderer.zoom(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());
            return true;
        }
    }

}