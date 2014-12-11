package com.evaluation.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
 
import com.evaluation.model.User;
import com.evaluation.view.R;
 
public class DeletableAdapter extends BaseAdapter {
	private Context context;
	private List<User> userList = new ArrayList<User>();
	private PopupWindow pop;
	private EditText userName;
	private EditText password;

	public DeletableAdapter(Context context, List<User> userList, PopupWindow pop, EditText userName, EditText password) {
		this.context = context;
		this.userList = userList;
		this.pop = pop;
		this.userName = userName;
		this.password = password;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return userList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return userList.get(position).getAccount();
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
			view = inflater.inflate(R.layout.deletable_list_item, null);
		}
		final TextView textView = (TextView) view.findViewById(R.id.simple_item_1);
		textView.setText(userList.get(position).getAccount());
		textView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pop.dismiss();
				userName.setText(userList.get(index).getAccount());
				password.setText(userList.get(index).getPassword());
			}
			
		});
		final ImageView imageView = (ImageView) view
				.findViewById(R.id.simple_item_2);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				userList.remove(index);
				notifyDataSetChanged();
				Toast.makeText(context, textView.getText().toString(),
						Toast.LENGTH_SHORT).show();
			}
		});
		return view;
	}
}