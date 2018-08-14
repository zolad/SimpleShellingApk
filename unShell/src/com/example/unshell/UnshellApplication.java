package com.example.unshell;

import java.io.BufferedInputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import dalvik.system.DexClassLoader;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;

public class UnshellApplication extends Application {
	 private static final String appkey = "Test_Application";  
	    private String apkFileName;  
	    private String odexPath;  
	    private String libPath;  
	    private String  TAG = "unshell_applicaiton";
	  
	    protected void attachBaseContext(Context base) {  
	        super.attachBaseContext(base);  
	        try {  
	            File odex = this.getDir("odex", MODE_PRIVATE);  
	            File libs = this.getDir("load_lib", MODE_PRIVATE);  
	            odexPath = odex.getAbsolutePath();  
	            libPath = libs.getAbsolutePath();  
	          
	            
	        
	            
	            // 读取程序classes.dex文件  
	            byte[] dexdata = this.readDexFileFromApk();  
	            // 分离出解壳后的apk文件已用于动态加载  
	            apkFileName = this.splitSrcFileFromDex(dexdata); 
	            Log.d(TAG, "文件解密完成"+apkFileName);
	             
	            File dexFile = new File(apkFileName); 
	            if (!dexFile.exists()) 
	            	dexFile.createNewFile();
	               
	            // 配置动态加载环境  
	            Object currentActivityThread = RefInvoke.invokeStaticMethod(  
	                    "android.app.ActivityThread", "currentActivityThread",  
	                    new Class[] {}, new Object[] {});  
	            String packageName = this.getPackageName();  
	            HashMap mPackages = (HashMap) RefInvoke.getFieldOjbect(  
	                    "android.app.ActivityThread", currentActivityThread,  
	                    "mPackages");  
	            WeakReference wr = (WeakReference) mPackages.get(packageName);  
	            DexClassLoader dLoader = new DexClassLoader(apkFileName, odexPath,  
	                    libPath, (ClassLoader) RefInvoke.getFieldOjbect(  
	                            "android.app.LoadedApk", wr.get(), "mClassLoader"));  
	            RefInvoke.setFieldOjbect("android.app.LoadedApk", "mClassLoader",  
	                    wr.get(), dLoader);  
	            Log.d(TAG, "动态加载环境配置完毕");
	  
	        } catch (Exception e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        }  
	    }  
	  
	  
	    public void onCreate() {  
	        {  
	  
	  
	           
	            String appClassName = null;  
	            try {  
	                ApplicationInfo ai = this.getPackageManager()  
	                        .getApplicationInfo(this.getPackageName(),  
	                                PackageManager.GET_META_DATA);  
	                Bundle bundle = ai.metaData;  
	                if (bundle != null  
	                        && bundle.containsKey("Test_Application")) {  
	                    appClassName = bundle.getString("Test_Application");  
	                    Log.d("application_oncreate", appClassName);
	                } else {  
	                    return;  
	                }  
	            } catch (NameNotFoundException e) {  
	                // TODO Auto-generated catch block  
	                e.printStackTrace();  
	            }  
	             
	            Log.d(TAG, "替换APPLICAITON");
	            Object currentActivityThread = RefInvoke.invokeStaticMethod(  
	                    "android.app.ActivityThread", "currentActivityThread",  
	                    new Class[] {}, new Object[] {});  
	            Object mBoundApplication = RefInvoke.getFieldOjbect(  
	                    "android.app.ActivityThread", currentActivityThread,  
	                    "mBoundApplication");  
	            Object loadedApkInfo = RefInvoke.getFieldOjbect(  
	                    "android.app.ActivityThread$AppBindData",  
	                    mBoundApplication, "info");  
	            RefInvoke.setFieldOjbect("android.app.LoadedApk", "mApplication",  
	                    loadedApkInfo, null);  
	            Object oldApplication = RefInvoke.getFieldOjbect(  
	                    "android.app.ActivityThread", currentActivityThread,  
	                    "mInitialApplication");  
	            ArrayList<Application> mAllApplications = (ArrayList<Application>) RefInvoke  
	                    .getFieldOjbect("android.app.ActivityThread",  
	                            currentActivityThread, "mAllApplications");  
	            mAllApplications.remove(oldApplication);  
	            ApplicationInfo appinfo_In_LoadedApk = (ApplicationInfo) RefInvoke  
	                    .getFieldOjbect("android.app.LoadedApk", loadedApkInfo,  
	                            "mApplicationInfo");  
	            ApplicationInfo appinfo_In_AppBindData = (ApplicationInfo) RefInvoke  
	                    .getFieldOjbect("android.app.ActivityThread$AppBindData",  
	                            mBoundApplication, "appInfo");  
	            appinfo_In_LoadedApk.className = appClassName;  
	            appinfo_In_AppBindData.className = appClassName;  
	            Application app = (Application) RefInvoke.invokeMethod(  
	                    "android.app.LoadedApk", "makeApplication", loadedApkInfo,  
	                    new Class[] { boolean.class, Instrumentation.class },  
	                    new Object[] { false, null });  
	            RefInvoke.setFieldOjbect("android.app.ActivityThread",  
	                    "mInitialApplication", currentActivityThread, app);  
	   
	            app.onCreate();  
	        }  
	    }  
	  
	  
	    private String splitSrcFileFromDex(byte[] data) throws IOException {  
	        byte[] apkdata = data;  
	        int ablen = apkdata.length;  
	        byte[] dexlen = new byte[4];  
	        System.arraycopy(apkdata, ablen - 4, dexlen, 0, 4);  
	        ByteArrayInputStream bais = new ByteArrayInputStream(dexlen);  
	        DataInputStream in = new DataInputStream(bais);  
	        int readInt = in.readInt();  
	        System.out.println(Integer.toHexString(readInt));  
	        byte[] newdex = new byte[readInt];  
	        System.arraycopy(apkdata, ablen - 4 - readInt, newdex, 0, readInt);  
	        //调用JNI的方法：解密数据并写APK文件
	        String apkFile = DecryptWrite(newdex,readInt);
	        
	        Log.d(TAG,apkFile);
	        File file = new File(apkFile);  
	        
	        ZipInputStream localZipInputStream = new ZipInputStream(  
	                new BufferedInputStream(new FileInputStream(file)));  
	        while (true) {  
	            ZipEntry localZipEntry = localZipInputStream.getNextEntry();  
	            if (localZipEntry == null) {  
	                localZipInputStream.close();  
	                break;  
	            }  
	            String name = localZipEntry.getName();  
	            if (name.startsWith("lib/") && name.endsWith(".so")) {  
	                File storeFile = new File(libPath + "/"  
	                        + name.substring(name.lastIndexOf('/')));  
	                storeFile.createNewFile();  
	                FileOutputStream fos = new FileOutputStream(storeFile);  
	                byte[] arrayOfByte = new byte[1024];  
	                while (true) {  
	                    int i = localZipInputStream.read(arrayOfByte);  
	                    if (i == -1)  
	                        break;  
	                    fos.write(arrayOfByte, 0, i);  
	                }  
	                fos.flush();  
	                fos.close();  
	            }  
	            localZipInputStream.closeEntry();  
	        }  
	        localZipInputStream.close();  
	  
	         return apkFile;
	    }  
	  
	  
	    private byte[] readDexFileFromApk() throws IOException {  
	        ByteArrayOutputStream dexByteArrayOutputStream = new ByteArrayOutputStream();  
	        ZipInputStream localZipInputStream = new ZipInputStream(  
	                new BufferedInputStream(new FileInputStream(  
	                        this.getApplicationInfo().sourceDir)));  
	        while (true) {  
	            ZipEntry localZipEntry = localZipInputStream.getNextEntry();  
	            if (localZipEntry == null) {  
	                localZipInputStream.close();  
	                break;  
	            }  
	            if (localZipEntry.getName().equals("classes.dex")) {  
	                byte[] arrayOfByte = new byte[1024];  
	                while (true) {  
	                    int i = localZipInputStream.read(arrayOfByte);  
	                    if (i == -1)  
	                        break;  
	                    dexByteArrayOutputStream.write(arrayOfByte, 0, i);  
	                }  
	            }  
	            localZipInputStream.closeEntry();  
	        }  
	        localZipInputStream.close();  
	        return dexByteArrayOutputStream.toByteArray();  
	    }  
	  
	    public native String DecryptWrite(byte[] buf, int size);  
	     
	    static {  
	        System.loadLibrary("decryptWrite");  
	    }
	    
	    
}
