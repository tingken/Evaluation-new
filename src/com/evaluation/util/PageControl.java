package com.evaluation.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import com.evaluation.view.R;

public class PageControl extends TableLayout implements OnClickListener{  
    private Button firstPage;  
    private Button prePage;  
    private Button nextPage;  
    private Button endPage;  
    private TextView totalPageText;  
    private TextView curPageText;  
    private int numPerPage=10;  
    private int curPage=1;  
    private int count=0;
    private Context context;
    private OnPageChangeListener pageChangeListener;
    //private View view;
    public PageControl(Context context) {  
        super(context);
        this.context = context;
        //initPageComposite(context);  
    }  
    public PageControl(Context context, AttributeSet attrs) {  
        super(context, attrs);
        this.context = context;
        //initPageComposite(context);
    }  
//    public PageControl(Context context, AttributeSet attrs, int defStyle) {  
//        super(context, attrs, defStyle);  
//        initPageComposite(context);  
//    }
    
    private void initPageComposite(Context context){  
//        this.setPadding(5,5,5,5);  
//        firstPage=new Button(context);  
//        firstPage.setId(1);
//        firstPage.setText("|<");
//        //firstImg.setImageResource(R.drawable.first_page); 
//        firstPage.setPadding(0,0,0,0);  
//        LayoutParams layoutParam=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);  
//        layoutParam.setMargins(0,0,5,0);  
//        firstPage.setLayoutParams(layoutParam);
//        firstPage.setOnClickListener(this);  
//        this.addView(firstPage);  
//        prePage=new Button(context);  
//        prePage.setId(2);
//        prePage.setText(" < ");
//        //preImg.setImageResource(R.drawable.prepage);  
//        prePage.setPadding(0,0,0,0);  
//        layoutParam=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);  
//        layoutParam.setMargins(0,0,5,0);  
//        prePage.setLayoutParams(layoutParam);  
//        prePage.setOnClickListener(this);  
//        this.addView(prePage);  
//        nextPage=new Button(context);  
//        nextPage.setId(3);
//        nextPage.setText(" > ");
//        //nextImg.setImageResource(R.drawable.nextpage);  
//        nextPage.setPadding(0,0,0,0);  
//        layoutParam=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);  
//        layoutParam.setMargins(0,0,5,0);  
//        nextPage.setLayoutParams(layoutParam);  
//        nextPage.setOnClickListener(this);  
//        this.addView(nextPage);  
//        endPage=new Button(context);  
//        endPage.setId(4);
//        endPage.setText(">|");
//        //endImg.setImageResource(R.drawable.lastpage);  
//        endPage.setPadding(0,0,0,0);  
//        layoutParam=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);  
//        layoutParam.setMargins(0,0,5,0);  
//        endPage.setLayoutParams(layoutParam);  
//        endPage.setOnClickListener(this);  
//        this.addView(endPage);  
        
//        totalPageText=new TextView(context);  
//        layoutParam=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);  
//        layoutParam.setMargins(5,0,5,0);  
//        totalPageText.setLayoutParams(layoutParam);  
//        totalPageText.setText("总页数");  
//        this.addView(totalPageText);  
//        curPageText=new TextView(context);  
//        layoutParam=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);  
//        layoutParam.setMargins(5,0,5,0);  
//        curPageText.setLayoutParams(layoutParam);  
//        curPageText.setText("当前页");  
//        this.addView(curPageText);  
//    	LayoutInflater inflater = (LayoutInflater) context
//				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		view = inflater.inflate(R.layout.page_button, null);
		firstPage = (Button) findViewById(R.id.first_page);
		firstPage.setOnClickListener(this);
		prePage = (Button) findViewById(R.id.page_up);
		prePage.setOnClickListener(this);
		nextPage = (Button) findViewById(R.id.page_down);
		nextPage.setOnClickListener(this);
		endPage = (Button) findViewById(R.id.last_page);
		endPage.setOnClickListener(this);
		totalPageText = (TextView) findViewById(R.id.total_page);
		curPageText = (TextView) findViewById(R.id.current_page);
    }
    /** 
     * 初始化分页组件的显示状态 
     * @param newCount 
     */  
    public void initPageShow(int newCount, int numPerPage){
    	this.numPerPage = numPerPage;
    	initPageComposite(context);
        count=newCount;  
        int totalPage=count%numPerPage==0?count/numPerPage:count/numPerPage+1;  
        curPage=1;  
        firstPage.setEnabled(false);  
        prePage.setEnabled(false);  
        if(totalPage<=1){  
            endPage.setEnabled(false);  
            nextPage.setEnabled(false);  
        }else{  
            endPage.setEnabled(true);  
            nextPage.setEnabled(true);  
        }  
        totalPageText.setText("总页数 "+totalPage);  
        curPageText.setText("当前页 "+curPage);  
    }  
    /** 
     * 分页按钮被点击时更新状态,该方法要在initPageShow后调用 
     */  
    @Override  
    public void onClick(View view) {  
        if(pageChangeListener==null){  
            return;  
        }  
        int totalPage=count%numPerPage==0?count/numPerPage:count/numPerPage+1;  
        switch(view.getId()){  
        case R.id.first_page:  
            curPage=1;  
            firstPage.setEnabled(false);  
            prePage.setEnabled(false);  
            if(totalPage>1){  
                nextPage.setEnabled(true);  
                endPage.setEnabled(true);  
            }  
            break;  
        case R.id.page_up:  
            curPage--;  
            if(curPage==1){  
                firstPage.setEnabled(false);  
                prePage.setEnabled(false);  
            }  
            if(totalPage>1){  
                nextPage.setEnabled(true);  
                endPage.setEnabled(true);  
            }  
            break;  
        case R.id.page_down:  
            curPage++;  
            if(curPage==totalPage){  
                nextPage.setEnabled(false);  
                endPage.setEnabled(false);  
            }  
            firstPage.setEnabled(true);  
            prePage.setEnabled(true);  
            break;  
        case R.id.last_page:  
            curPage=totalPage;  
            nextPage.setEnabled(false);  
            endPage.setEnabled(false);  
            firstPage.setEnabled(true);  
            prePage.setEnabled(true);  
            break;  
        default:  
            break;  
        }  
        totalPageText.setText("总页数 "+totalPage);
        curPageText.setText("当前页 "+curPage);  
        pageChangeListener.pageChanged(curPage,numPerPage);  
    }  
    public OnPageChangeListener getPageChangeListener() {  
        return pageChangeListener;  
    }  
    /** 
     * 设置分页监听事件 
     * @param pageChangeListener 
     */  
    public void setPageChangeListener(OnPageChangeListener pageChangeListener) {  
        this.pageChangeListener = pageChangeListener;  
    }  
}
