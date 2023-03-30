package ifes.eric.importsdkdemo.SDKtestes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import dji.common.error.DJIError;
import dji.common.product.Model;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import dji.sdk.sdkmanager.DJISDKManager;
import ifes.eric.importsdkdemo.MainActivity;
import ifes.eric.importsdkdemo.R;

public class InformacoesDispositivo extends AppCompatActivity {
    private static final String TAG =  MainActivity.class.getName();

    public TextView modeloDJI, nomeDJI;
    public DJISDKManager produtoDJI = DJISDKManager.getInstance();
    public BaseProduct baseProduct = produtoDJI.getProduct();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacoes_dispositivo);
        nomeDJI = findViewById(R.id.text_infDis_Nome);
        modeloDJI = findViewById(R.id.text_infDis_Nome);

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

       // modeloDJI.setText(baseProduct.getModel().toString());











    }
}