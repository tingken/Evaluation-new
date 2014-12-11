package com.evaluation.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
	private int pageNum;
	private int row;
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
			title.setOnClickListener(new MyViewPagerOnClickListener(position));
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
			Intent intent = new Intent(context, InfoDetailActivity.class);
			intent.putExtra("currentItem", annoList.get(item).getId());
			intent.putExtra("account", annoList.get(item).getAccount());
			context.startActivity(intent);
		}
	}
	public void clear() {
		this.annoList.clear();
 	}
	public void addAll(int curPage, List<Announcement> annoList) {
		pageNum = curPage;
		this.annoList = annoList;
	}
}
