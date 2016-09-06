package project.esc;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {

	WebView wv;
	private ValueCallback<Uri> mUploadMessage;
	private final static int FILECHOOSER_RESULTCODE = 1;
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	private BackPressCloseHandler handler;
	String SENDER_ID = "954082168178";
	GoogleCloudMessaging gcm;
	Context context;
	String regid;
	static final String TAG = "GCM";

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == FILECHOOSER_RESULTCODE) {
			if (mUploadMessage == null) {
				return;
			}
			Uri result = (intent == null || resultCode != RESULT_OK) ? null
					: intent.getData();

			mUploadMessage.onReceiveValue(result);

			mUploadMessage = null;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		startActivity(new Intent(this, SplashActivity.class));
		wv = (WebView) findViewById(R.id.webview);
		wv.loadUrl("http://esc.kau.ac.kr");
		wv.setWebViewClient(new SiteWebViewClient());
		wv.getSettings().setJavaScriptEnabled(true);
		wv.getSettings().setPluginState(PluginState.ON);
		wv.getSettings().setAppCacheEnabled(true);
		wv.getSettings().setCacheMode(wv.getSettings().LOAD_NO_CACHE);

		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				wv.goBack();
			}
		});

		Button next = (Button) findViewById(R.id.next);
		next.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				wv.goForward();
			}
		});

		Button home = (Button) findViewById(R.id.home);
		home.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				wv.loadUrl("http://esc.kau.ac.kr");
			}
		});

		Button reload = (Button) findViewById(R.id.reload);
		reload.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				wv.reload();
			}
		});

		Button stop = (Button) findViewById(R.id.stop);
		stop.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				wv.stopLoading();
			}
		});

		Button etc = (Button) findViewById(R.id.etc);
		etc.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AlertDialog dialog = create_inputDialog();
				dialog.show();
			}
		});

		handler = new BackPressCloseHandler(this);

		context = getApplicationContext();

		// Check device for Play Services APK. If check succeeds, proceed with
		// GCM registration.

		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(context);
			Log.e("RegID", regid);
			if (regid.isEmpty()) {

				registerInBackground();
			}
		} else {
			Toast.makeText(this, "text", 0).show();
		}

	}

	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		if ((KeyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) {
			wv.goBack();
			return true;
		} else if ((KeyCode == KeyEvent.KEYCODE_BACK)
				&& wv.canGoBack() == false) {
			handler.onBackPressed();
			return true;
		}
		return super.onKeyDown(KeyCode, event);
	}

	private class SiteWebViewClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	private AlertDialog create_inputDialog() {
		AlertDialog dialogBox = new AlertDialog.Builder(this)
				.setTitle("E.S.C.")
				.setIcon(R.drawable.ic_launcher)
				.setMessage(
						Html.fromHtml("COPYRIGHT 2015 E.S.C."
								+ "<br>All rights reserved"
								+ "<br>Contact : zzangakswns@gmail.com"))
				.setPositiveButton("Check",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();

							}
						}).create();

		return dialogBox;
	}

	@Override
	protected void onResume() {

		super.onResume();

		// Check device for Play Services APK.

		checkPlayServices();

	}

	/**
	 * 
	 * Check the device to make sure it has the Google Play Services APK. If
	 * 
	 * it doesn't, display a dialog that allows users to download the APK from
	 * 
	 * the Google Play Store or enable it in the device's system settings.
	 */

	private boolean checkPlayServices() {

		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);

		if (resultCode != ConnectionResult.SUCCESS) {

			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {

				GooglePlayServicesUtil.getErrorDialog(resultCode, this,

				PLAY_SERVICES_RESOLUTION_REQUEST).show();

			} else {

				Log.i(TAG, "This device is not supported.");

				finish();

			}

			return false;

		}

		return true;

	}

	private String getRegistrationId(Context context) {

		final SharedPreferences prefs = getGcmPreferences(context);

		String registrationId = prefs.getString(PROPERTY_REG_ID, "");

		if (registrationId.isEmpty()) {

			Log.i(TAG, "Registration not found.");

			return "";

		}

		// Check if app was updated; if so, it must clear the registration ID

		// since the existing regID is not guaranteed to work with the new

		// app version.

		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);

		int currentVersion = getAppVersion(context);

		if (registeredVersion != currentVersion) {

			Log.i(TAG, "App version changed.");

			return "";

		}

		return registrationId;

	}

	/**
	 * 
	 * @return Application's version code from the {@code PackageManager}.
	 */

	private static int getAppVersion(Context context) {

		try {

			PackageInfo packageInfo = context.getPackageManager()

			.getPackageInfo(context.getPackageName(), 0);

			return packageInfo.versionCode;

		} catch (NameNotFoundException e) {

			// should never happen

			throw new RuntimeException("Could not get package name: " + e);

		}

	}

	/**
	 * 
	 * @return Application's {@code SharedPreferences}.
	 */

	private SharedPreferences getGcmPreferences(Context context) {

		// This sample app persists the registration ID in shared preferences,
		// but

		// how you store the regID in your app is up to you.

		return getSharedPreferences(MainActivity.class.getSimpleName(),

		Context.MODE_PRIVATE);

	}

	private void registerInBackground() {
		new RegistrationThread().start();
	}

	/**
	 * 
	 * Sends the registration ID to your server over HTTP, so it can use
	 * GCM/HTTP or CCS to send
	 * 
	 * messages to your app. Not needed for this demo since the device sends
	 * upstream messages
	 * 
	 * to a server that echoes back the message using the 'from' address in the
	 * message.
	 */

	private void sendRegistrationIdToBackend() {

		// Your implementation here.
		try {

			String addr = "http://esc.kau.ac.kr/register.php?u_id=" + regid
					+ "";
			URL url = new URL(addr);

			url.openStream();
			Log.e("u_id", addr);
		} catch (MalformedURLException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		} catch (IOException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		}
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();

	}

	private void storeRegistrationId(Context context, String regId) {

		final SharedPreferences prefs = getGcmPreferences(context);

		int appVersion = getAppVersion(context);

		Log.i(TAG, "Saving regId on app version " + appVersion);

		SharedPreferences.Editor editor = prefs.edit();

		editor.putString(PROPERTY_REG_ID, regId);

		editor.putInt(PROPERTY_APP_VERSION, appVersion);

		editor.commit();

	}

	class RegistrationThread extends Thread {
		public void run() {
			try {
				if (gcm == null) {
					gcm = GoogleCloudMessaging.getInstance(context);
				}
				regid = gcm.register(SENDER_ID);

				sendRegistrationIdToBackend();
			} catch (Exception e) {
				e.getStackTrace();

			}
		}
	}

}
