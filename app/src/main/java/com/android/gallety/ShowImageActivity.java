package com.android.gallety;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yktx.group.BaseActivity;
import com.yktx.group.R;
import com.yktx.util.FileURl;


public class ShowImageActivity extends BaseActivity {
	private GridView mGridView;
	private List<String> list;
	private ChildAdapter adapter;
	public static final String SELECTIAMGE = "selectimage";

	private ImageView back;
	private RelativeLayout finish;
	TextView complete;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_image_activity);

		mGridView = (GridView) findViewById(R.id.child_grid);
		list = getIntent().getStringArrayListExtra("data");

		adapter = new ChildAdapter(this, list, mGridView);
		mGridView.setAdapter(adapter);
//
//		back = (ImageView) findViewById(R.id.back);
//		back.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				ShowImageActivity.this.finish();
//			}
//		});
		complete = (TextView)findViewById(R.id.complete);
		complete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ArrayList<String> listreturn = new ArrayList<String>();
				ArrayList<Integer> integers = (ArrayList<Integer>) adapter
						.getSelectItems();
				for (Integer postion : integers) {
					listreturn.add(FileURl.LOAD_FILE + (String) adapter.getItem(postion));
				}
				Intent intentdata = new Intent();
				intentdata.putExtra(SELECTIAMGE, (java.io.Serializable) listreturn);
				setResult(222, intentdata);
				ShowImageActivity.this.finish();
			}
		});
		finish = (RelativeLayout) findViewById(R.id.rl_showimage_back);
		finish.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				ArrayList<String> listreturn = new ArrayList<String>();
//				ArrayList<Integer> integers = (ArrayList<Integer>) adapter
//						.getSelectItems();
//				for (Integer postion : integers) {
//					listreturn.add((String) adapter.getItem(postion));
//				}
//				Intent intentdata = new Intent();
//				intentdata.putExtra(SELECTIAMGE, (java.io.Serializable) listreturn);
//				setResult(222, intentdata);
				finish();
			}
		});
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem complete = menu.add("完成");
		complete.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// switch (item.getTitle().toString()){
		// case "完成":

		ArrayList<String> listreturn = new ArrayList<String>();
		ArrayList<Integer> integers = (ArrayList<Integer>) adapter
				.getSelectItems();
		for (Integer postion : integers) {
			listreturn.add((String) adapter.getItem(postion));
		}
		Intent intentdata = new Intent();
		intentdata.putExtra(SELECTIAMGE, (java.io.Serializable) listreturn);
		setResult(222, intentdata);
		finish();
		// break;
		// }
		return super.onOptionsItemSelected(item);
	}

}
