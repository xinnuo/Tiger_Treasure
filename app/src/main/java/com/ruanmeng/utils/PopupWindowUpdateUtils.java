package com.ruanmeng.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unused")
public class PopupWindowUpdateUtils {
	private PopupYearWindowCallBack callBack;
	private Context activity;
	private static PopupWindowUpdateUtils popupWindowPrivinceListUtils;
	public static synchronized PopupWindowUpdateUtils getInstance(){
		if(popupWindowPrivinceListUtils==null){
			popupWindowPrivinceListUtils = new PopupWindowUpdateUtils();
		}
		return popupWindowPrivinceListUtils;
	}
	public void getSmartUpdateDialog(Context context, String s, String content, final PopupYearWindowCallBack callBack){
		activity = context;
		this.callBack = callBack;
		AlertDialog.Builder builer = new AlertDialog.Builder(activity);
		if (true) {
			builer.setTitle(s);
			builer.setMessage(getContent(content));
//			builer.setMessage("新版本上线了，速度下载体验吧！");
			builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
                callBack.doWork();
				}
			});
		} else {
			builer.setTitle("恭喜你！");
			builer.setMessage("当前为最新版本！！");
		}
		AlertDialog dialog = builer.create();
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialogInterface, int keycode, KeyEvent keyEvent) {
				if ((keycode == KeyEvent.KEYCODE_HOME)) {
					return true;
				} else {
					return false;
				}
			}
		});
		dialog.show();
	}

	public void getUpdateDialog(Context context,final PopupYearWindowCallBack callBack){
		activity = context;
		this.callBack = callBack;
		AlertDialog.Builder builer = new AlertDialog.Builder(activity);
		if (true) {
			builer.setTitle("有新版本，是否更新？");
			builer.setMessage("新版本上线了，赶紧下载体验吧！");
			builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
                callBack.doWork();
				}
			});
		} else {
			builer.setTitle("恭喜你！");
			builer.setMessage("当前为最新版本！！");
		}
		builer.setNeutralButton("忽略", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog dialog = builer.create();
		dialog.show();
	}

	public static interface PopupYearWindowCallBack {
		public void doWork();
		public void doBack();
	}
	private String getContent(String content) {
		String text  = "";
		getList(content);
		if(list_BiaoQian.size()>0){
			for(int i =0;i<list_BiaoQian.size();i++){
				text += list_BiaoQian.get(i)+"\n";
			}
		}
		return text;
	}
	private List<String> list_BiaoQian= new ArrayList<>();
	private void getList(String content) {
		list_BiaoQian.clear();
		if(!TextUtils.isEmpty(content)){
			String[]   str_Time = content.split(",");
			for(String str:str_Time){
				list_BiaoQian.add(str);
			}
		}
	}
	}




