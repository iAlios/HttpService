package com.alios.httpservices;

import com.alios.httpservices.service.ResourceManager;
import com.alios.httpservices.utils.NetUtils;
import com.example.httpservices.R;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		TextView cTextView = (TextView) findViewById(R.id.hello);
		cTextView.setText(NetUtils.getLocalIpAddress() + ":"
				+ ResourceManager.getInstance().getDefaultPort());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ResourceManager.getInstance().exitApplication(this);
	}

}
