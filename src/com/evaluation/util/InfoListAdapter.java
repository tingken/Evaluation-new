package com.evaluation.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.evaluation.model.Announcement;
import com.evaluation.model.User;
import com.evaluation.view.InfoDetailActivity;
import com.evaluation.view.MainActivity;
import com.evaluation.view.R;

public class InfoListAdapter extends BaseAdapter {
	private Context context;
	private List<TextView> titles = new ArrayList<TextView>();
	private int pageNum;
	private int row;
	private String TAG = "effort";
	private List<Announcement> annoList = new ArrayList<Announcement>();

	public InfoListAdapter(Context context, int pageNum, int row, List<Announcement> annoList) {
		this.context = context;
		this.pageNum = pageNum;
		this.row = row;
		this.annoList = annoList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return annoList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return annoList.get(position).getAccount();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final int index = position;
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.info_list_item, null);
		}
		final TextView title = (TextView) view.findViewById(R.id.title);
		if(annoList.get(position).getTitle() != null && !annoList.get(position).getTitle().equals("null")){
			title.setText(annoList.get(position).getTitle());
//			title.setOnClickListener(new MyViewPagerOnClickListener(position));
//			title.setOnTouchListener(new MyViewOnTouchListener(position));
			titles.add(title);
		}
		final TextView repDate = (TextView) view
				.findViewById(R.id.rep_date);
		if(annoList.get(position).getRepDate() != null && !annoList.get(position).getRepDate().equals("null")){
			repDate.setText(annoList.get(position).getRepDate());
		}
		return view;
	}
	private class MyViewPagerOnClickListener implements OnClickListener {
		private int item;
		public MyViewPagerOnClickListener(int item) {
			this.item = item;
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//v.setBackgroundColor(Color.YELLOW);
			for(TextView view : titles) {
				if(view != v)
					view.setBackgroundColor(Color.TRANSPARENT);
			}
			Intent intent = new Intent(context, InfoDetailActivity.class);
			intent.putExtra("currentItem", annoList.get(item).getId());
			intent.putExtra("account", annoList.get(item).getAccount());
			context.startActivity(intent);
		}
	}
//	private class MyViewOnTouchListener implements OnTouchListener {
//		private int item;
//		public MyViewOnTouchListener(int item) {
//			this.item = item;
//		}
//		@Override
//		public boolean onTouch(View v, MotionEvent event) {
//			// TODO Auto-generated method stub
//			v.setBackgroundColor(Color.YELLOW);
//			Log.e(TAG, "" + event.getAction());
//			for(TextView view : titles) {
//				if(view != v)
//					view.setBackgroundColor(Color.TRANSPARENT);
//			}
//			if (event.getAction() == MotionEvent.ACTION_MOVE) {  
//				System.out.println("MOVE");  //接触到ListView移动时  
//            } else if (event.getAction() == MotionEvent.ACTION_UP) {  
//            	System.out.println("up");   //离开ListView时  
//            	Intent intent = new Intent(context, InfoDetailActivity.class);
//    			intent.putExtra("currentItem", annoList.get(item).getId());
//    			intent.putExtra("account", annoList.get(item).getAccount());
//    			context.startActivity(intent);
//            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {  
//            	System.out.println("down");   //接触到ListView时  
//            } else if(event.getAction() == MotionEvent.ACTION_CANCEL) {
//            	System.out.println("CANCEL");
//            	Intent intent = new Intent(context, InfoDetailActivity.class);
//    			intent.putExtra("currentItem", annoList.get(item).getId());
//    			intent.putExtra("account", annoList.get(item).getAccount());
//    			context.startActivity(intent);
//            }
//			return true;
//		}
//	}
	public void clear() {
		this.annoList.clear();
 	}
	public void addAll(int curPage, List<Announcement> annoList) {
		pageNum = curPage;
		this.annoList = annoList;
	}
}
