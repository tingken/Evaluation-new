package com.evaluation.util;

public interface OnPageChangeListener {
	/** 
     * 点击分页按钮时触发此操作 
     * @param curPage 当前页 
     * @param numPerPage 每页显示个数 
     */  
    public void pageChanged(int curPage,int numPerPage);
}
