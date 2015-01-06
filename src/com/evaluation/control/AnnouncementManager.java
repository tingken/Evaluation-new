package com.evaluation.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.evaluation.model.Announcement;
import com.evaluation.util.Utils;
import com.evaluation.view.BuildConfig;
import com.evaluation.view.MainActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class AnnouncementManager {
	private Announcement announce;
	private String dirPath;
	private Context context;
	private LruCache<String, Bitmap> mMemoryCache;
	// Default memory cache size
    private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 1024 * 2; // 2MB

    // Default disk cache size
    private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB

    // Compression settings when writing images to disk cache
    private static final CompressFormat DEFAULT_COMPRESS_FORMAT = CompressFormat.JPEG;
    private static final int DEFAULT_COMPRESS_QUALITY = 70;

    // Constants to easily toggle various caches
    private static final boolean DEFAULT_MEM_CACHE_ENABLED = true;
    private static final boolean DEFAULT_DISK_CACHE_ENABLED = true;
    private static final boolean DEFAULT_CLEAR_DISK_CACHE_ON_START = false;
	
	//private Bitmap bitmap = null;
	private String TAG = "effort";
	public static int annWidth = 500, annHeight = 300, picWidth = 133, picHeight = 203;
	
	public AnnouncementManager(String dirPath, Context context) {
		this.dirPath = dirPath;
		this.context = context;
		mMemoryCache = new LruCache<String, Bitmap>(DEFAULT_MEM_CACHE_SIZE) {
            /**
             * Measure item size in bytes rather than units which is more practical for a bitmap
             * cache
             */
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return Utils.getBitmapSize(bitmap);
            }
            @Override
			protected void entryRemoved(boolean evicted, String key,
					Bitmap oldValue, Bitmap newValue) {
				if (evicted && oldValue != null && !oldValue.isRecycled()) {
					Log.e(TAG, "recycle bitmap: " + key);
					oldValue.recycle();
					oldValue = null;
				}
			}
        };
	}
	
	public Map<String, String> getContent(String path, int userId) {
		String filePath = path;
		return null;
	}
	
	public void saveStream(InputStream is, String fileName, int width, int height) {
		//ImageView imageView = new ImageView(context);
        		//bitmap = BitmapFactory.decodeStream(inputStream);
        		//bitmap = getThumBitmapFromFile(file.getAbsolutePath());
        		//bitmap = getThumBitmapFromStream(is, width, height);
		Bitmap bm = getThumBitmapFromStream(is, width, height);
		Log.e(TAG, "保存图片");
		File f = new File(dirPath, fileName);
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
			Log.i(TAG, "已经保存");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//    			options.inJustDecodeBounds = false;
    			//imageView.setImageBitmap(bitmap);
	}
	
	public Bitmap getBitmapByName(String fileName) {
		Bitmap bitmap = null;
		File file = new File(dirPath + "/" + fileName);
        	if(file.exists()){
//        		inputStream = context.getAssets().open(fileName);//new FileInputStream(dirPath + "/download/" + fileName);
//        		inputstream2file(inputStream, file);
        		//bitmap = BitmapFactory.decodeStream(inputStream);
        		bitmap = getThumBitmapFromFile(dirPath + "/" + fileName);
        	
//    			options.inJustDecodeBounds = false;
        	}
		return bitmap;
	}
		
	public Bitmap getRoundCornerBitmapByName(int index, String fileName) {
		//ImageView imageView = new ImageView(context);
		Bitmap bitmap = null;
		File file = new File(dirPath + "/" + fileName);
        try {
        	if(file.exists()){
//        		inputStream = context.getAssets().open(fileName);//new FileInputStream(dirPath + "/download/" + fileName);
//        		inputstream2file(inputStream, file);
        		//bitmap = BitmapFactory.decodeStream(inputStream);
        		bitmap = toRoundCorner(getThumBitmapFromFile(dirPath + "/" + fileName), 12);
        		//bitmap = getThumBitmapFromFile(dirPath + "/" + fileName);
//    			options.inJustDecodeBounds = false;
    			
//    			imageView.setImageBitmap(bitmap);
        	}
        } catch (OutOfMemoryError e) {
			if(bitmap != null && !bitmap.isRecycled()){
				bitmap.recycle();   //回收图片所占的内存
	         	System.gc();  //提醒系统及时回收 
			}
        }
        addBitmapToCache(fileName+index, bitmap);
        return bitmap;
	}
	
	public List<ImageView> getImageViews() {
		List<ImageView> imageViews = new ArrayList<ImageView>();
		String[] fileNames = null;
		
		//File fileDir = new File(dirPath);
		try {
			fileNames = context.getAssets().list("");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//fileDir.list();
		for(String fileName : fileNames) {
			Log.e(TAG, fileName);
//			BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inJustDecodeBounds = true; // 设置了此属性一定要记得将值设置为false
			InputStream inputStream = null;
			Bitmap bitmap = null;
			File file = new File(dirPath + "/" + fileName);
            try {
            	if(file.isFile()){
            		inputStream = context.getAssets().open(fileName);//new FileInputStream(dirPath + "/download/" + fileName);
            		inputstream2file(inputStream, fileName);
            		//bitmap = BitmapFactory.decodeStream(inputStream);
            		bitmap = getThumBitmapFromFile(file.getAbsolutePath());
            	
	//    			options.inJustDecodeBounds = false;
	    			ImageView imageView = new ImageView(context);
	    			imageView.setImageBitmap(bitmap);
	    			imageViews.add(imageView);
            	}
            } catch (FileNotFoundException e) {
                e.printStackTrace();  
            } catch (IOException e) {
            	e.printStackTrace();
            } catch (OutOfMemoryError e) {
    			if(bitmap != null && !bitmap.isRecycled()){
					bitmap.recycle();   //回收图片所占的内存
		         	System.gc();  //提醒系统及时回收 
				}
            }
            //Drawable drawable=Drawable.createFromStream(inputStream, "img");
//            BitmapFactory.Options options=new BitmapFactory.Options(); 
//            options.inJustDecodeBounds = false; 
//            options.inSampleSize = 10;   //width，hight设为原来的十分一
            
		}
		return imageViews;
	}

	public Bitmap getThumBitmapFromFile(String imageFile) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true; // 只计算尺寸，不output
		BitmapFactory.decodeFile(imageFile, opts); // 这步的decodeFile只是为了得到opts的原始尺寸
		// opts.inSampleSize = computeSampleSize(opts, -1, 128 * 128);
		// //计算合适的输出尺寸（第三个参数是最大能接受的像素值）
		opts.inSampleSize = getOptionsSampleSize(opts, 405, 670);
		opts.inJustDecodeBounds = false; // output
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeFile(imageFile, opts); // 这步decodeFile才是真的output
			return bitmap;
		} catch (OutOfMemoryError err) {
			Log.e(TAG, "out of memory error.");
			if(bitmap != null && !bitmap.isRecycled()){
				bitmap.recycle();   //回收图片所占的内存
	         	System.gc();  //提醒系统及时回收 
			}
		}
		return null;
	}
	
	private Bitmap getThumBitmapFromStream(InputStream is, int width, int height) {
		Bitmap bm = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		//opts.inJustDecodeBounds = true; // 只计算尺寸，不output
		//BitmapFactory.decodeStream(is, null, opts); // 这步的decodeFile只是为了得到opts的原始尺寸
		// opts.inSampleSize = computeSampleSize(opts, -1, 128 * 128);
		// //计算合适的输出尺寸（第三个参数是最大能接受的像素值）
		//Bitmap bmp = null;
		//opts.inSampleSize = getOptionsSampleSize(opts, width, height);
		opts.inJustDecodeBounds = false; // output
		bm = BitmapFactory.decodeStream(is, null, opts); // 这步decodeFile才是真的output
		//bm = BitmapFactory.decodeStream(is);
		return bm;

	}	
	
	private int getOptionsSampleSize(BitmapFactory.Options options, int newWidth, int newHeight)
	{
		int radioWidth = (int) Math.ceil(options.outWidth / newWidth);
		int radioHeight = (int) Math.ceil(options.outHeight / newHeight);
		if (radioWidth > 1 || radioHeight > 1)
		{
			return radioWidth > radioHeight ? radioWidth : radioHeight;
		}
		else
		{
			return 1;
		}
	}

	public void inputstream2file(InputStream ins, String fileName) {
		if(fileName == null || fileName.trim().equals(""))
			return;
		File file = new File(dirPath, fileName);
		try {
			OutputStream os = new FileOutputStream(file);
			int bytesRead = 0;
			byte[] buffer = new byte[1024];
			while ((bytesRead = ins.read(buffer, 0, buffer.length)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			os.close();
			ins.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveBitmap(Bitmap bm, String picName) {
		Log.e(TAG, "保存图片");
		File f = new File(dirPath, picName);
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
			Log.i(TAG, "已经保存");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		if(bitmap == null)
			return null;
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888); 
		Canvas canvas = new Canvas(output); 
		final int color = 0xff424242; 
		final Paint paint = new Paint(); 
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()); 
		final RectF rectF = new RectF(rect); 
		final float roundPx = pixels; 
		paint.setAntiAlias(true); 
		canvas.drawARGB(0, 0, 0, 0); 
		paint.setColor(color); 
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint); 
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); 
		canvas.drawBitmap(bitmap, rect, rect, paint); 
		return output; 
	}
//	public void close() {
//		if(bitmap != null && !bitmap.isRecycled()){
//			bitmap.recycle();   //回收图片所占的内存
//         	bitmap = null;
//         	System.gc();  //提醒系统及时回收 
//		}
//	}
	public void addBitmapToCache(String data, Bitmap bitmap) {
		if (data == null || bitmap == null) {
			return;
		}

		// Add to memory cache
		if (mMemoryCache != null && mMemoryCache.get(data) == null) {
			mMemoryCache.put(data, bitmap);
		}
	}
	public Bitmap getBitmapFromMemCache(int index, String key) {
  //      if (mMemoryCache != null) {
            final Bitmap memBitmap = mMemoryCache.get(key+index);
            if (memBitmap != null && !memBitmap.isRecycled()) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Memory cache hit");
                }
                return memBitmap;
            }else {
            	return getRoundCornerBitmapByName(index, key);
            }
 //       }
    }
	/**
     * 移除缓存
     * 
     * @param key
     */
    public synchronized void removeImageCache(int index, String key) {
        if (key != null) {
            if (mMemoryCache != null) {
                Bitmap bm = mMemoryCache.remove(key+index);
                if (bm != null)
                    bm.recycle();
            }
        }
    }
	public void clearCaches() {
		if (mMemoryCache != null) {
            if (mMemoryCache.size() > 0) {
                Log.d("CacheUtils",
                        "mMemoryCache.size() " + mMemoryCache.size());
                mMemoryCache.evictAll();
                Log.d("CacheUtils", "mMemoryCache.size()" + mMemoryCache.size());
            }
            mMemoryCache = null;
        }
    }
	/**
     * A holder class that contains cache parameters.
     */
    public static class ImageCacheParams {
        public String uniqueName;
        public int memCacheSize = DEFAULT_MEM_CACHE_SIZE;
        public int diskCacheSize = DEFAULT_DISK_CACHE_SIZE;
        public CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT;
        public int compressQuality = DEFAULT_COMPRESS_QUALITY;
        public boolean memoryCacheEnabled = DEFAULT_MEM_CACHE_ENABLED;
        public boolean diskCacheEnabled = DEFAULT_DISK_CACHE_ENABLED;
        public boolean clearDiskCacheOnStart = DEFAULT_CLEAR_DISK_CACHE_ON_START;

        public ImageCacheParams(String uniqueName) {
            this.uniqueName = uniqueName;
        }
    }
}
