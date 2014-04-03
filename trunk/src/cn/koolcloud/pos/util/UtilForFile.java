package cn.koolcloud.pos.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;

public class UtilForFile {

	public static String readStringFromFile(Context context, String fileName) {
    	InputStream inputStream = null;
    	InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;
        StringBuffer strBuffer = new StringBuffer();
        try {
			inputStream = context.getAssets().open(fileName);
			inputReader = new InputStreamReader(inputStream);
			bufferReader = new BufferedReader(inputReader);
			 
			String line;
			     
			while (null != (line = bufferReader.readLine())) {
			    strBuffer.append(line).append("\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
				inputReader.close();
				bufferReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return strBuffer.toString();
    }
	
}
