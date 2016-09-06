package project.esc;

import android.app.Activity;
import android.webkit.WebView;
import android.widget.Toast;

public class BackPressCloseHandler {
	
	private long Time =0;
	private Toast toast;
	private Activity activity;
	
	public BackPressCloseHandler(Activity context){
		this.activity=context;
	}
	
	public void onBackPressed(){
		if(System.currentTimeMillis()>Time+2000){
			Time = System.currentTimeMillis();
			show();
			return;
		}
		else{
			activity.finish();
			toast.cancel();
		}
	}
	
	public void show(){
		toast = Toast.makeText(activity, "\'�ڷ�\'��ư �ѹ� �� �����ø� ����˴ϴ�.", Toast.LENGTH_SHORT);
		toast.show();
	}

}
