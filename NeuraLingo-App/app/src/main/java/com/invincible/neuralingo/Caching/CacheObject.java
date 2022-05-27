package com.invincible.neuralingo.Caching;

import android.content.Context;
import android.util.Log;

import com.invincible.neuralingo.Model.ProfileModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class CacheObject{

    public static CacheObject obj;
    public static String PROFILE_OBJ = "profile.data";
    final String LOG_TAG = CacheObject.class.getSimpleName();

    private CacheObject()
    {

    }
    public static void createCacheObject()
    {
        obj = new CacheObject();
    }

    public <T> void saveObject(String name, T obj, Context context)
    {
        try {
            File file = new File(context.getFilesDir(), name);
            ObjectOutputStream objStream = new ObjectOutputStream(new FileOutputStream(file));
            objStream.writeObject(obj);
            objStream.close();
            Log.d(LOG_TAG, name + " Saved successfully");
        } catch (IOException e) {
            Log.d(LOG_TAG, name + " Error Saving");
            e.printStackTrace();
        }
    }

    public Object getSavedObject(String name, Context context)
    {
        try {
            FileInputStream fis = new FileInputStream(context.getFilesDir() + File.separator + name);
            ObjectInputStream is = new ObjectInputStream(fis);
            Log.d(LOG_TAG, name + " retrieved successfully");
            return is.readObject();
        }catch (Exception e)
        {
            e.printStackTrace();
            Log.d(LOG_TAG, name + " Error while Retrieving");
            return new ProfileModel();
        }
    }
}
