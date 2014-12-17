package cn.koolcloud.zbar.client.android;

import java.io.IOException;
import cn.koolcloud.zbar.client.android.camera.CameraManager;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import cn.koolcloud.zbar.client.android.InterfaceBarCode;

public class ZbarScannerRelativeLayout extends RelativeLayout implements InterfaceBarCode,SurfaceHolder.Callback{
	private static final String TAG = "ScannerRelativeLayout";
	  private static int cwidth = 400;
	  private Context context;
	  private static int cheight = 300;
	  private static boolean isInited = false;
	  private CameraManager cameraManager;
	  private boolean hasSurface;
	  private InterfaceBarCode BarcodeInterface = null;
	  private SurfaceView surfaceView;
	  
	public ZbarScannerRelativeLayout(Context paramContext)
	{
		super(paramContext);
		this.context = paramContext;
	}

	public ZbarScannerRelativeLayout(Context paramContext, AttributeSet attrs, int paramInt)
	{
		super(paramContext, attrs, paramInt);
		this.context = paramContext;

	}

	public ZbarScannerRelativeLayout(Context paramContext, AttributeSet attrs)
	{
		super(paramContext, attrs);
		this.context = paramContext;

	}
	
	@Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);  
        Log.d(TAG,"widthMeasureSpec:"+widthMeasureSpec+"  heightMeasureSpec:"+heightMeasureSpec);
    } 
	
	
	  public void setBarCodeCallBack(InterfaceBarCode BarcodeInterface)
	  {
		  this.BarcodeInterface = BarcodeInterface;
	  }
	  
	  
	public void startScan()
	{
		if(isInited)
			return;
		isInited = true;
		cameraManager = new CameraManager(context);
		cameraManager.setInterfaceBarCode(this);
	    this.surfaceView = new SurfaceView(context);
	    addView(this.surfaceView, 
	    		new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	   
	    hasSurface = false;
	    SurfaceHolder surfaceHolder = surfaceView.getHolder();
	    
	    if (hasSurface) {

		      initCamera(surfaceHolder);
		    } else {
		      surfaceHolder.addCallback(this);
		    }

	}


	public void stopScan() {
		if(!isInited)
			return;
		isInited = false;
		cameraManager.stopPreview();
	    cameraManager.closeDriver();
	    if (!hasSurface) {
	      SurfaceHolder surfaceHolder = surfaceView.getHolder();
	      surfaceHolder.removeCallback(this);
	    }

	  }
	  

	  CameraManager getCameraManager() {
	    return cameraManager;
	  }
		  
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	    if (holder == null) {
	        Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
	      }
	      if (!hasSurface) {
	        hasSurface = true;
	        cheight = surfaceView.getHeight();
	        cwidth = surfaceView.getWidth();
	        initCamera(holder);
	      }
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
	
	private void initCamera(SurfaceHolder surfaceHolder) {
	    if (surfaceHolder == null) {
	      throw new IllegalStateException("No SurfaceHolder provided");
	    }
	    if (cameraManager.isOpen()) {
	      Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
	      return;
	    }
	    
	    
	    try {
	      cameraManager.openDriver(surfaceHolder,cwidth,cheight);
	      cameraManager.startPreview();

	    } catch (IOException ioe) {
	      Log.w(TAG, ioe);
	    } catch (RuntimeException e) {
	      Log.w(TAG, "Unexpected error initializing camera", e);
	    }
	  }

	@Override
	public void getBarCodeData(String data) {
		if(BarcodeInterface != null)
			BarcodeInterface.getBarCodeData(data);
		
	}
}
	  
