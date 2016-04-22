package com.beessoft.dyyd.utils;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.beessoft.dyyd.R;

 public class DialogHelper {
	 	private Context context;
		private Dialog dialogPic;
		private String imgPath;
		//文件保存目录
		private File saveCatalog;
		//保存的文件
		private File saveFile;
	 /**打开图片查看对话框
	 * @param checkInActivity **/
		@SuppressLint("InflateParams")
		public void openPictureDialog(Context context) {
//			this.context = context;			
						
			dialogPic = new Dialog(context,R.style.simple_dialog);
						
//			LayoutInflater inflater = getLayoutInflater();
			View view = LayoutInflater.from(context).inflate(R.layout.dialog_picture, null);
						
			ImageView imgView = (ImageView) view.findViewById(R.id.img_weibo_img);		
			dialogPic.setContentView(view);
			dialogPic.show();
						
			displayForDlg(imgView); //显示内容到dialog中
						
		}
					
		public  void displayForDlg(ImageView imgView) {
			imgView.setVisibility(View.VISIBLE);
//			imgPath = getApplicationContext().getFilesDir()+"/"+imgName;
			imgPath = "/sdcard/test/img.jpg";
			System.out.println("图片文件路径----------》"+imgPath);
			if(!imgPath.equals("")) {
				saveFile=new File(saveCatalog,imgPath);
				BitmapFactory.Options options=new BitmapFactory.Options(); 
				options.inJustDecodeBounds = false; 
				options.inSampleSize = 5;   //width，hight设为原来的五分一 
				Bitmap btp =BitmapFactory.decodeFile(saveFile.getPath(),options); 

				imgView.setImageBitmap(btp);//显示图片	
			}
//						tempBitmap.recycle();
//						 System.gc();  //提醒系统及时回收	   
		}

}
