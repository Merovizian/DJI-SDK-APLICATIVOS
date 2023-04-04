package ifes.eric.importsdkdemo.SDKtestes;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toolbar;


import dji.common.battery.BatteryState;
import dji.common.error.DJIError;
import dji.common.error.DJIFlightControllerError;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.battery.Battery;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.sdkmanager.DJISDKManager;
import ifes.eric.importsdkdemo.MainActivity;
import ifes.eric.importsdkdemo.R;

public class InformacoesDispositivo extends AppCompatActivity {
    private static final String TAG =  MainActivity.class.getName();

    public TextView modeloDJI, nomeDJI, getBateria, getFirmwareVersion;
    public DJISDKManager produtoDJI = DJISDKManager.getInstance();
    public BaseProduct baseProduct = produtoDJI.getProduct();
    public BatteryState batteryState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacoes_dispositivo);
        nomeDJI = findViewById(R.id.text_infDis_Nome);
        modeloDJI = findViewById(R.id.text_infDis_Model);
        getBateria = findViewById(R.id.text_infDis_getBatery);
        getFirmwareVersion = findViewById(R.id.text_infDis_PackageVersion);

        setTitle("Informações Dispositivo");

        try {
            baseProduct.getName(new CommonCallbacks.CompletionCallbackWith<String>() {
                @Override
                public void onSuccess(String s) {
                    nomeDJI.setText(s);
                }

                @Override
                public void onFailure(DJIError djiError) {
                    Log.i(TAG,djiError.getDescription());
                }
            });

            modeloDJI.setText(baseProduct.getModel().toString());
            getFirmwareVersion.setText(baseProduct.getFirmwarePackageVersion());

            // INFORMAÇOES DE BATERIA
            getBateria.setText(String.valueOf(batteryState.getVoltage()));



        } catch (Exception e){
            e.printStackTrace();
        }













    }
}