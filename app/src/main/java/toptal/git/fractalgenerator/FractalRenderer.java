////////
// This sample is published as part of the blog article at www.toptal.com/blog
// Visit www.toptal.com/blog and subscribe to our newsletter to read great posts
////////

package toptal.git.fractalgenerator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import java.lang.Math;

public class FractalRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "FractalRenderer";
    private Fractal mFractal;

    private int mHeight;
    private int mWidth;

    //Store all values as doubles, and truncate for use as floats.
    private double mRatio;
    private double mY=1.0f;
    private double mX=1.0f;
    private double mZoom=0.5f;


    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        mFractal = new Fractal();
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        float[] mMVPMatrix = new float[]{(float)(-1.0/mZoom),                         0.0f,        0.0f,    0.0f,
                                                        0.0f,  (float)(1.0/(mZoom*mRatio)),        0.0f,    0.0f,
                                                        0.0f,                         0.0f,        1.0f,    0.0f,
                                                  (float)-mX,                   (float)-mY,        0.0f,    1.0f};

        mFractal.draw(mMVPMatrix);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {

        mWidth  = width;
        mHeight =  height;

        //Set viewport to fullscreen
        GLES20.glViewport(0, 0, width, height);

        mRatio = (double) width / height;
    }

    /**
     * Utility method from Android Tutorials for compiling a OpenGL shader.
     *
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * Utility method from Android Tutorials for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;

        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }


    public void add(double dx, double dy) {
        //Both are scaled by mHeight, because the ratio is taken into account by the translation matrix
        mX+=dx/(mZoom*mHeight);
        mY+=dy/(mZoom*mHeight);
    }

    private double zoomIncrease = 1.5;

    public void zoom(double scaleFactor, double x, double y) {
        scaleFactor = (scaleFactor-1)*zoomIncrease+1;
        // Default zoom is to top center of the screen. Thus, changes should be zeroed at that point
        x-=mWidth/2;

        //Note that, because mZoom changse in the add method, there is an implicit division by log(2) hidden through limit discrete summation/integration
        double scale = Math.log(scaleFactor);

        //Move towards focus
        add(-scale*x,-scale*y);

        //add(scale)
        mZoom*=scaleFactor;
    }
}