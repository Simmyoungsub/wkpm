package wkpm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.json.simple.JSONObject;

public class GlobalKeyListenerExample implements NativeKeyListener {
	
	public static Map<String,Integer> keyObject;
	
	static {
		GlobalKeyListenerExample.keyObject = new HashMap<String,Integer>();
	}
	
	public void nativeKeyPressed(NativeKeyEvent e) {
		
	}

	public void nativeKeyReleased(NativeKeyEvent e) {
//		System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
		String key = NativeKeyEvent.getKeyText(e.getKeyCode());
		
		if(GlobalKeyListenerExample.keyObject.containsKey(key)) {
			Integer value = GlobalKeyListenerExample.keyObject.get(key);
			GlobalKeyListenerExample.keyObject.put(key, value+1);
		}else {
			GlobalKeyListenerExample.keyObject.put(key, 1);
		}
	}

	public void nativeKeyTyped(NativeKeyEvent e) {
		
	}
	
	public static void main(String[] args) {
		
		System.out.println("wkpm start!");
		
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.WARNING);

		// Don't forget to disable the parent handlers.
		logger.setUseParentHandlers(false);
		
		FileTimer ft = new FileTimer();
		Timer job = new Timer();
		
		Date settings = settingTimes(6,0);
		
		job.schedule(ft, settings);
		
		try {
			GlobalScreen.registerNativeHook();
		}
		catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());
			
			System.exit(1);
		}
		
		GlobalScreen.addNativeKeyListener(new GlobalKeyListenerExample());
	}
	
	public static Date settingTimes(int hour, int minutes) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.AM_PM,Calendar.PM);
		c.set(Calendar.HOUR, hour);
		c.set(Calendar.MINUTE, minutes);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		return new Date(c.getTimeInMillis());
	}
}

class FileTimer extends TimerTask {
	private final String FILEDIR = "";
	
	@Override
	public void run() {
		if(this.makeFile()) {
			System.out.println("file save!!");
			System.out.println("wkpm exit!");
			System.exit(1);
		}else {
			System.out.println("file save failed!!");
		}
	}
	
	private boolean makeFile() {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		Date now = new Date();
		
		String fileName = "clickLog" + format.format(now) + ".json";
		String filePath = this.FILEDIR + fileName;
		
		JSONObject result = new JSONObject();
		
		result.put("result", GlobalKeyListenerExample.keyObject);
		
		try {
			File folder = new File(this.FILEDIR);
			
			if(!folder.exists()) {
				folder.mkdirs();
			}
			
			FileWriter f = new FileWriter(filePath);
			
			f.write(result.toJSONString());
			f.flush();
			f.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}