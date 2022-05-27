//package com.invincible.neuralingo.Caching;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.os.AsyncTask;
//import java.io.File;
//
//import okhttp3.internal.cache.DiskLruCache;
//
//
//public class ImageCaching {
//
//    private DiskLruCache diskLruCache;
//    private final Object diskCacheLock = new Object();
//    private boolean diskCacheStarting = true;
//    Context context;
//    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
//    private static final String DISK_CACHE_SUBDIR = "thumbnails";
//
//    class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
//        @Override
//        protected Void doInBackground(File... params) {
//            synchronized (diskCacheLock) {
//                File cacheDir = params[0];
//                diskCacheStarting = false; // Finished initialization
//                diskCacheLock.notifyAll(); // Wake any waiting threads
//            }
//            return null;
//        }
//    }
//
//    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
//        // Decode image in background.
//        @Override
//        protected Bitmap doInBackground(Integer... params) {
//            final String imageKey = String.valueOf(params[0]);
//
//            // Check disk cache in background thread
//            Bitmap bitmap = getBitmapFromDiskCache(imageKey);
//
//            if (bitmap == null) { // Not found in disk cache
//                // Process as normal
//
//            }
//
//            // Add final bitmap to caches
//            addBitmapToCache(imageKey, bitmap);
//
//            return bitmap;
//        }
//    }
//
//    public void addBitmapToCache(String key, Bitmap bitmap) {
//
//        // Also add to disk cache
//        synchronized (diskCacheLock) {
//            if (diskLruCache != null && diskLruCache.get(key) == null) {
//                diskLruCache.put(key, bitmap);
//            }
//        }
//    }
//
//    public Bitmap getBitmapFromDiskCache(String key) {
//        synchronized (diskCacheLock) {
//            // Wait while disk cache is started from background thread
//            while (diskCacheStarting) {
//                try {
//                    diskCacheLock.wait();
//                } catch (InterruptedException e) {}
//            }
//            if (diskLruCache != null) {
//                return diskLruCache.get(key);
//            }
//        }
//        return null;
//    }
//
//
//    public static File getDiskCacheDir(Context context, String uniqueName) {
//        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
//        // otherwise use internal cache dir
//        final String cachePath = context.getCacheDir().getPath();
//
//        return new File(cachePath + File.separator + uniqueName);
//    }
//}
