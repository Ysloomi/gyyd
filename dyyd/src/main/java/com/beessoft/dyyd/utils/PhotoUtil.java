package com.beessoft.dyyd.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.util.Base64;


public class PhotoUtil {

	/**
	 * 照片编码
	 *
	 * @param file
	 * @param ifTake 是否拍照
	 * @return
	 */
	public static Bitmap imageEncode(File file, Boolean ifTake){
		// 创建Options对象
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		opts.inJustDecodeBounds = false;
		Bitmap bitmap = null;


//		File mFile=new File(path);
		//若该文件存在
//		if (mFile.exists()) {
		long size = 0;
		try {
			FileInputStream fis = null;
			fis = new FileInputStream(file);
			size = fis.available();
			fis.close();
		}catch (IOException e){
			e.printStackTrace(System.out);
		}
		int sizeKb = (int) (size / (1024));
		if (ifTake) {
			opts.inSampleSize = 5; // width，hight设为原来的五分一
			bitmap = BitmapFactory.decodeFile(file.getPath(), opts);
		} else if (sizeKb > 10000) {
			opts.inSampleSize = 5; // width，hight设为原来的五分一
			bitmap = BitmapFactory.decodeFile(file.getPath(), opts);
		} else {
			bitmap = BitmapFactory.decodeFile(file.getPath(), opts);
		}
		if (bitmap != null) {
			Bitmap bitmapCompress = PhotoUtil.comp(bitmap);
			return bitmapCompress;
		}
		bitmap.recycle();
		System.gc(); // 提醒系统及时回收
		return null;
	}

	/**
	 * bitmap尺寸压缩
	 * 
	 * @param image
	 * @return
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

	/**
	 * bitmap质量压缩
	 * 
	 * @param image
	 * @return
	 */

	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 50) { // 循环判断如果压缩后图片是否大于50kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		// System.out.println("baos.toByteArray().length / 1024>>>"+
		// baos.toByteArray().length / 1024);
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**
	 * bitmap编码成base64
	 * 
	 * @param image
	 * @return
	 */

	public static String encodeTobase64(Bitmap image) {
		Bitmap immagex = image;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

//		Log.e("LOOK", imageEncoded);
		return imageEncoded;
	}

	public static Bitmap decodeBase64(String input) {
		byte[] decodedByte = Base64.decode(input, 0);
		return BitmapFactory
				.decodeByteArray(decodedByte, 0, decodedByte.length);
	}


	/**
	 * 通过降低图片的质量来压缩图片
	 * 
	 * @param bmp
	 *            要压缩的图片位图对象
	 * @param maxSize
	 *            压缩后图片大小的最大值,单位KB
	 * @return 压缩后的图片位图对象
	 */
	public static Bitmap compressByQuality(Bitmap bitmap, int maxSize) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int quality = 100;
		bitmap.compress(CompressFormat.JPEG, quality, baos);
		System.out.println("图片压缩前大小：" + baos.toByteArray().length + "byte");
		boolean isCompressed = false;
		while (baos.toByteArray().length / 1024 > maxSize) {
			quality -= 10;
			baos.reset();
			bitmap.compress(CompressFormat.JPEG, quality, baos);
			System.out.println("质量压缩到原来的" + quality + "%时大小为："
					+ baos.toByteArray().length + "byte");
			isCompressed = true;
		}
		System.out.println("图片压缩后大小：" + baos.toByteArray().length + "byte");
		if (isCompressed) {
			Bitmap compressedBitmap = BitmapFactory.decodeByteArray(
					baos.toByteArray(), 0, baos.toByteArray().length);
			recycleBitmap(bitmap);
			return compressedBitmap;
		} else {
			return bitmap;
		}
	}

	/**
	 * 通过压缩图片的尺寸来压缩图片大小，仅仅做了缩小，如果图片本身小于目标大小，不做放大操作
	 * 
	 * @param pathName
	 *            图片的完整路径
	 * @param targetWidth
	 *            缩放的目标宽度
	 * @param targetHeight
	 *            缩放的目标高度
	 * @return 缩放后的图片
	 */
	public static Bitmap compressBySize(String pathName, int targetWidth,
			int targetHeight) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;// 不去真的解析图片，只是获取图片的头部信息，包含宽高等；
		Bitmap bitmap = BitmapFactory.decodeFile(pathName, opts);
		// 得到图片的宽度、高度；
		int imgWidth = opts.outWidth;
		int imgHeight = opts.outHeight;
		// 分别计算图片宽度、高度与目标宽度、高度的比例；取大于等于该比例的最小整数；
		int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);
		int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);
		if (widthRatio > 1 || heightRatio > 1) {
			if (widthRatio > heightRatio) {
				opts.inSampleSize = widthRatio;
			} else {
				opts.inSampleSize = heightRatio;
			}
		}
		// 设置好缩放比例后，加载图片进内容；
		opts.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(pathName, opts);
		return bitmap;
	}

	/**
	 * 通过压缩图片的尺寸来压缩图片大小
	 * 
	 * @param bitmap
	 *            要压缩图片
	 * @param targetWidth
	 *            缩放的目标宽度
	 * @param targetHeight
	 *            缩放的目标高度
	 * @return 缩放后的图片
	 */
	public static Bitmap compressBySize(Bitmap bitmap, int targetWidth,
			int targetHeight) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100, baos);
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0,
				baos.toByteArray().length, opts);
		// 得到图片的宽度、高度；
		int imgWidth = opts.outWidth;
		int imgHeight = opts.outHeight;
		// 分别计算图片宽度、高度与目标宽度、高度的比例；取大于该比例的最小整数；
		int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);
		int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);
		if (widthRatio > 1 || heightRatio > 1) {
			if (widthRatio > heightRatio) {
				opts.inSampleSize = widthRatio;
			} else {
				opts.inSampleSize = heightRatio;
			}
		}
		// 设置好缩放比例后，加载图片进内存；
		opts.inJustDecodeBounds = false;
		Bitmap compressedBitmap = BitmapFactory.decodeByteArray(
				baos.toByteArray(), 0, baos.toByteArray().length, opts);
		recycleBitmap(bitmap);
		return compressedBitmap;
	}

	/**
	 * 通过压缩图片的尺寸来压缩图片大小，通过读入流的方式，可以有效防止网络图片数据流形成位图对象时内存过大的问题；
	 * 
	 * @param InputStream
	 *            要压缩图片，以流的形式传入
	 * @param targetWidth
	 *            缩放的目标宽度
	 * @param targetHeight
	 *            缩放的目标高度
	 * @return 缩放后的图片
	 * @throws IOException
	 *             读输入流的时候发生异常
	 */
	public static Bitmap compressBySize(InputStream is, int targetWidth,
			int targetHeight) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buff = new byte[1024];
		int len = 0;
		while ((len = is.read(buff)) != -1) {
			baos.write(buff, 0, len);
		}

		byte[] data = baos.toByteArray();
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
				opts);
		// 得到图片的宽度、高度；
		int imgWidth = opts.outWidth;
		int imgHeight = opts.outHeight;
		// 分别计算图片宽度、高度与目标宽度、高度的比例；取大于该比例的最小整数；
		int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);
		int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);
		if (widthRatio > 1 || heightRatio > 1) {
			if (widthRatio > heightRatio) {
				opts.inSampleSize = widthRatio;
			} else {
				opts.inSampleSize = heightRatio;
			}
		}
		// 设置好缩放比例后，加载图片进内存；
		opts.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		return bitmap;
	}

	/**
	 * 旋转图片摆正显示
	 * 
	 * @param srcPath
	 * @param bitmap
	 * @return
	 */
	public static Bitmap rotateBitmapByExif(String srcPath, Bitmap bitmap) {
		ExifInterface exif;
		Bitmap newBitmap = null;
		try {
			exif = new ExifInterface(srcPath);
			if (exif != null) { // 读取图片中相机方向信息
				int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_NORMAL);
				int digree = 0;
				switch (ori) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					digree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					digree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					digree = 270;
					break;
				}
				if (digree != 0) {
					Matrix m = new Matrix();
					m.postRotate(digree);
					newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
							bitmap.getWidth(), bitmap.getHeight(), m, true);
					recycleBitmap(bitmap);
					return newBitmap;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 回收位图对象
	 * 
	 * @param bitmap
	 */
	public static void recycleBitmap(Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			System.gc();
			bitmap = null;
		}
	}

	public static int sizeOf(Bitmap bitmap) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			return bitmap.getAllocationByteCount();
		} else
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			return bitmap.getByteCount();
		} else {
			return bitmap.getRowBytes() * bitmap.getHeight();
		}
	}
	
	/** 
     * Get image from newwork 
     * @param path The path of image 
     * @return byte[] 
     * @throws Exception 
     */  
    public byte[] getImage(String path) throws Exception{  
        URL url = new URL(path);  
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
        conn.setConnectTimeout(5 * 1000);  
        conn.setRequestMethod("GET");  
        InputStream inStream = conn.getInputStream();  
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){  
            return readStream(inStream);  
        }  
        return null;  
    }  
  
    /** 
     * Get image from newwork 
     * @param path The path of image 
     * @return InputStream 
     * @throws Exception 
     */  
    public InputStream getImageStream(String path) throws Exception{  
        URL url = new URL(path);  
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
        conn.setConnectTimeout(5 * 1000);  
        conn.setRequestMethod("GET");  
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){  
            return conn.getInputStream();  
        }  
        return null;  
    }  
    /** 
     * Get data from stream 
     * @param inStream 
     * @return byte[] 
     * @throws Exception 
     */  
    public static byte[] readStream(InputStream inStream) throws Exception{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int len = 0;  
        while( (len=inStream.read(buffer)) != -1){  
            outStream.write(buffer, 0, len);  
        }  
        outStream.close();  
        inStream.close();  
        return outStream.toByteArray();  
    }  
  
    /** 
     * 保存文件 
     * @param bm 
     * @param fileName 
     * @throws IOException 
     */  
//    public void saveFile(Bitmap bm, String fileName) throws IOException {  
//        File dirFile = new File(ALBUM_PATH);  
//        if(!dirFile.exists()){  
//            dirFile.mkdir();  
//        }  
//        File myCaptureFile = new File(ALBUM_PATH + fileName);  
//        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));  
//        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);  
//        bos.flush();  
//        bos.close();  
//    }  
  
//    private Runnable saveFileRunnable = new Runnable(){  
//        @Override  
//        public void run() {  
//            try {  
//                saveFile(mBitmap, mFileName);  
//                mSaveMessage = "图片保存成功！";  
//            } catch (IOException e) {  
//                mSaveMessage = "图片保存失败！";  
//                e.printStackTrace();  
//            }  
//            messageHandler.sendMessage(messageHandler.obtainMessage());  
//        }  
//    };  
  
//    private Handler messageHandler = new Handler() {  
//        @Override  
//        public void handleMessage(Message msg) {  
//            mSaveDialog.dismiss();  
//            Log.d(TAG, mSaveMessage);  
//            Toast.makeText(IcsTestActivity.this, mSaveMessage, Toast.LENGTH_SHORT).show();  
//        }  
//    };  
  
    /* 
     * 连接网络 
     * 由于在4.0中不允许在主线程中访问网络，所以需要在子线程中访问 
     */  
//    private Runnable connectNet = new Runnable(){  
//        @Override  
//        public void run() {  
//            try {  
//                String filePath = "http://img.my.csdn.net/uploads/201402/24/1393242467_3999.jpg";  
//                mFileName = "test.jpg";  
//  
//                //以下是取得图片的两种方法  
//                //////////////// 方法1：取得的是byte数组, 从byte数组生成bitmap  
//                byte[] data = getImage(filePath);  
//                if(data!=null){  
//                    mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap  
//                }else{  
//                    Toast.makeText(IcsTestActivity.this, "Image error!", 1).show();  
//                }  
//                ////////////////////////////////////////////////////////  
//  
//                //******** 方法2：取得的是InputStream，直接从InputStream生成bitmap ***********/  
//                mBitmap = BitmapFactory.decodeStream(getImageStream(filePath));  
//                //********************************************************************/  
//  
//                // 发送消息，通知handler在主线程中更新UI  
//                connectHanlder.sendEmptyMessage(0);  
//                
//            } catch (Exception e) {  
//                Toast.makeText(IcsTestActivity.this,"无法链接网络！", 1).show();  
//                e.printStackTrace();  
//            }  
//        }  
//    };  
}
