package com.beessoft.dyyd.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.beessoft.dyyd.R;

public class PhotoHelper {

	// public static final int PHOTO_CODE = 5;
	// public void photoStart(Context context) {
	// // 调用相机
	// String imgPath = "/sdcard/test/img.jpg";
	// // 必须确保文件夹路径存在，否则拍照后无法完成回调
	// File vFile = new File(imgPath);
	// if (!vFile.exists()) {
	// File vDirPath = vFile.getParentFile(); // new
	// // File(vFile.getParent());
	// vDirPath.mkdirs();
	// }
	// Uri uri = Uri.fromFile(vFile);
	// Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	// intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//
	// // 打开新的activity，这里是系统摄像头
	// context.startActivityForResult(intent, PHOTO_CODE);
	// }
	/**
	 * 压缩图片显示
	 */
	public static Bitmap comp(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 800f;// 这里设置高度为800f
		float ww = 800f;// 这里设置宽度为800f
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/** 打开图片查看对话框 **/
	@SuppressLint("InflateParams")
	public static void openPictureDialog(Context context) {
		final Dialog dialogPic = new Dialog(context, R.style.simple_dialog);
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_picture, null);
		ImageView imgView = (ImageView) view.findViewById(R.id.img_weibo_img);
		Button btnBig = (Button) view.findViewById(R.id.btn_big);
		btnBig.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogPic.dismiss();
			}
		});
		dialogPic.setContentView(view);
		dialogPic.show();
		displayForDlg(imgView, btnBig); // 显示内容到dialog中
	}

	public static void displayForDlg(ImageView imgView, Button btnBig) {
		imgView.setVisibility(View.VISIBLE);
		btnBig.setVisibility(View.VISIBLE);
		String imgPath = "/sdcard/test/img.jpg";
		if (!imgPath.equals("")) {
			// 文件保存目录
			File saveCatalog = null;
			File saveFile = new File(saveCatalog, imgPath);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = false;
			options.inSampleSize = 2; // width，hight设为原来的二分一
			Bitmap btp = BitmapFactory.decodeFile(saveFile.getPath(), options);
			imgView.setImageBitmap(btp);// 显示图片
			imgView.setScaleType(ScaleType.FIT_CENTER);
			imgView.setAdjustViewBounds(true);
		}
	}

	/** 打开图片查看对话框 **/
	@SuppressLint("InflateParams")
	public static void openPictureDialog_down(Context context, Bitmap bitmap) {
		final Dialog dialogPic = new Dialog(context, R.style.simple_dialog);
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_picture, null);
		ImageView imgView = (ImageView) view.findViewById(R.id.img_weibo_img);
		Button btnBig = (Button) view.findViewById(R.id.btn_big);
		btnBig.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogPic.dismiss();
			}
		});
		dialogPic.setContentView(view);
		dialogPic.show();
		displayForDlg_down(imgView, btnBig, bitmap); // 显示内容到dialog中
	}

	public static void displayForDlg_down(ImageView imgView, Button btnBig,
			Bitmap bitmap) {
		imgView.setVisibility(View.VISIBLE);
		btnBig.setVisibility(View.VISIBLE);
		imgView.setImageBitmap(bitmap);// 显示图片
		imgView.setScaleType(ScaleType.FIT_CENTER);
		imgView.setAdjustViewBounds(true);
	}
	
	/** 打开图片查看对话框 **/
	@SuppressLint("InflateParams")
	public static void openPictureDialog(Context context,String path) {
		final Dialog dialogPic = new Dialog(context, R.style.simple_dialog);
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_picture, null);
		ImageView imgView = (ImageView) view.findViewById(R.id.img_weibo_img);
		Button btnBig = (Button) view.findViewById(R.id.btn_big);
		btnBig.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogPic.dismiss();
			}
		});
		dialogPic.setContentView(view);
		dialogPic.show();
		displayForDlg(imgView, btnBig,path); // 显示内容到dialog中
	}

	public static void displayForDlg(ImageView imgView, Button btnBig,String imgPath) {
		imgView.setVisibility(View.VISIBLE);
		btnBig.setVisibility(View.VISIBLE);
		if (!imgPath.equals("")) {
			File saveFile = new File(imgPath);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = false;
			options.inSampleSize = 2; // width，hight设为原来的二分一
			Bitmap btp = BitmapFactory.decodeFile(saveFile.getPath(), options);
			imgView.setImageBitmap(btp);// 显示图片
			imgView.setScaleType(ScaleType.FIT_CENTER);
			imgView.setAdjustViewBounds(true);
		}
	}


}
