/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.koolcloud.zbar.client.android.camera;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.io.IOException;

import cn.koolcloud.zbar.client.android.InterfaceBarCode;
import cn.koolcloud.zbar.client.android.camera.*;
import cn.koolcloud.zbar.client.android.camera.AutoFocusManager;
import cn.koolcloud.zbar.client.android.camera.CameraSettings;


import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

/**
 * This object wraps the Camera service object and expects to be the only one talking to it. The
 * implementation encapsulates the steps needed to take preview-sized images, which are used for
 * both preview and decoding.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CameraManager {

  private static final String TAG = CameraManager.class.getSimpleName();
  private InterfaceBarCode BarcodeInterface = null;
  private final Context context;
  private Camera camera;
  private boolean previewing;
  private BeepManager beepManager;
  private cn.koolcloud.zbar.client.android.camera.AutoFocusManager autoFocusManager;
  
  ImageScanner scanner;
  

	static {
		System.loadLibrary("iconv");
	}
	
  public CameraManager(Context context) {
    this.context = context;
    beepManager = new BeepManager(context);
    
  }

  
  public void setInterfaceBarCode(InterfaceBarCode BarcodeInterface)
  {
	  this.BarcodeInterface = BarcodeInterface;
  }
  
  /**
   * Opens the camera driver and initializes the hardware parameters.
   *
   * @param holder The surface object which the camera will draw preview frames into.
   * @throws java.io.IOException Indicates the camera driver failed to open.
   */
  public synchronized void openDriver(SurfaceHolder holder ,int width,int height) throws IOException {
    Camera theCamera = camera;
    
    if (theCamera == null) {
      theCamera = com.google.zxing.client.android.camera.OpenCameraInterface.open();
      
      if (theCamera == null) {
        throw new IOException();
      }
      camera = theCamera;
    }
    theCamera.setPreviewDisplay(holder);
    
    
    int hversion = SystemProperties.getInt("ro.cynovo.hardware", 5);
    if(hversion == 5 && width < 700 && height < 700)
    	theCamera.setDisplayOrientation(0);
    else {
    	theCamera.setDisplayOrientation(180);
	}
    
    theCamera.startSmoothZoom(1);


    Camera.Parameters parameters = theCamera.getParameters();
    if(cn.koolcloud.zbar.client.android.camera.CameraSettings.isAUTO_FOCUS())
    {
    	parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
    }
    theCamera.setParameters(parameters);

    
	scanner = new ImageScanner();
	scanner.setConfig(0, Config.X_DENSITY, 1);
	scanner.setConfig(0, Config.Y_DENSITY, 1);
	autoFocusManager = new cn.koolcloud.zbar.client.android.camera.AutoFocusManager(context, camera);
    camera.setPreviewCallback(new Camera.PreviewCallback(){

		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			Camera.Parameters parameters = camera.getParameters();
			
			Size size = parameters.getPreviewSize();

			Image barcode = new Image(size.width, size.height, "Y800");
			barcode.setData(data);

			int result = scanner.scanImage(barcode);

			if (result != 0) {
				
				SymbolSet syms = scanner.getResults();

				for (Symbol sym : syms) {
					beepManager.playBeepSound();
					if(BarcodeInterface != null)
						BarcodeInterface.getBarCodeData(sym.getData());
					if(!CameraSettings.isBULKMODE())
						stopPreview();
				}
			}
			
		}
    	
    });
  }

  
  public synchronized boolean isOpen() {
    return camera != null;
  }

  /**
   * Closes the camera driver if still in use.
   */
  public synchronized void closeDriver() {
    if (camera != null) {
      camera.release();
      camera = null;
    }
  }

  /**
   * Asks the camera hardware to begin drawing preview frames to the screen.
   */
  public synchronized void startPreview() {
    Camera theCamera = camera;
    if (theCamera != null && !previewing) {
      theCamera.startPreview();
      
      previewing = true;
      if(autoFocusManager != null)
    	  autoFocusManager.start();
    }
  }

  /**
   * Tells the camera to stop drawing preview frames.
   */
  public synchronized void stopPreview() {

    if (camera != null && previewing) {
        if(autoFocusManager != null)
      	  autoFocusManager.stop();
        camera.setPreviewCallback(null);
      camera.stopPreview();
      previewing = false;
    }
  }
}
