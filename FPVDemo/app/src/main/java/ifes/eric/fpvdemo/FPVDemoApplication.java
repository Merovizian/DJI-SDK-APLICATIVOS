package ifes.eric.fpvdemo;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.media.AsyncPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;


import androidx.core.content.ContextCompat;

import org.bouncycastle.jcajce.provider.symmetric.ARC4;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.products.Aircraft;
import dji.sdk.products.HandHeld;
import dji.sdk.sdkmanager.DJISDKInitEvent;
import dji.sdk.sdkmanager.DJISDKManager;

public class FPVDemoApplication extends Application {
    public static final String FLAG_CONNECTION_CHANGE = "fpv_tutorial_connection_change";

    private static BaseProduct mProduct;
    private Handler mHandler;

    private Application instance;


    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(Looper.getMainLooper());

        DJISDKManager.SDKManagerCallback mDJISDKManagerCallback = new DJISDKManager.SDKManagerCallback() {

            //Listens to the SDK registration result
            @Override
            public void onRegister(DJIError djiError) {
                Handler handler = new Handler(Looper.getMainLooper());
                if (djiError == DJISDKError.REGISTRATION_SUCCESS) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "SDK register success", Toast.LENGTH_LONG).show();
                        }
                    });
                    DJISDKManager.getInstance().startConnectionToProduct();
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "SDK register fail", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }

            @Override
            public void onProductDisconnect() {
                notifyStatusChange();
            }

            @Override
            public void onProductConnect(BaseProduct baseProduct) {
                notifyStatusChange();
            }

            @Override
            public void onProductChanged(BaseProduct baseProduct) {
                notifyStatusChange();
            }

            @Override
            public void onComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent oldComponent, BaseComponent newComponent) {
                if (newComponent != null) {
                    newComponent.setComponentListener(new BaseComponent.ComponentListener() {
                        @Override
                        public void onConnectivityChange(boolean isConnected) {
                            notifyStatusChange();
                        }
                    });
                }
            }

            @Override
            public void onInitProcess(DJISDKInitEvent djisdkInitEvent, int i) {

            }

            @Override
            public void onDatabaseDownloadProgress(long l, long l1) {

            }

        };

        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || permissionCheck== 0 && permissionCheck2 ==0  ) {
            DJISDKManager.getInstance().registerApp(getApplicationContext(), mDJISDKManagerCallback);
            Toast.makeText(getApplicationContext(), "registering, pls wait...", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "Please check if the permission is granted.", Toast.LENGTH_LONG).show();

        }

    }


    private void notifyStatusChange(){
        mHandler.removeCallbacks(updateRunnable);
        mHandler.postDelayed(updateRunnable,500);
    }

    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(FLAG_CONNECTION_CHANGE);
            getApplicationContext().sendBroadcast(intent);
        }
    };

    public void setContext(Application application){
        instance = application;
    }
    public FPVDemoApplication(){

    }

    public static synchronized BaseProduct getProductInstance(){
        if (null == mProduct){
            mProduct = DJISDKManager.getInstance().getProduct();
        }
        return mProduct;
    }

    public static synchronized Camera getCameraIntance(){
        if (getProductInstance() == null) return null;
        Camera camera = null;
        if (getProductInstance() instanceof Aircraft){
            camera = getProductInstance().getCamera();
        }else if (getProductInstance() instanceof HandHeld){
            camera = getProductInstance().getCamera();
        }
        return camera;
    }

}
