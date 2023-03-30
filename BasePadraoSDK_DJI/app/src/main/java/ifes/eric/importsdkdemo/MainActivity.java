package ifes.eric.importsdkdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.sdkmanager.DJISDKInitEvent;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.thirdparty.afinal.core.AsyncTask;
import ifes.eric.importsdkdemo.SDKtestes.InformacoesDispositivo;

public class MainActivity extends AppCompatActivity {
    // VERSAO ERIC
    public Button informacoesDispositivo;
    public Boolean habilitarBotoes = false;


    private static final String TAG =  MainActivity.class.getName();
    public static final String FLAG_CONNECTION_CHANGE = "dji_sdk_connection_change";
    private static BaseProduct mProduct;
    private Handler mHandler;
    // Aqui eu criei uma instancia do objeto djisdkManagerIniciada que pertence a classe DJISDKManager
    private DJISDKManager djisdkManagerIniciada = DJISDKManager.getInstance();
    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
            Manifest.permission.VIBRATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
    };
    private List<String> missingPermission = new ArrayList<>();
    private AtomicBoolean isRegistrationInProgress = new AtomicBoolean(false);
    private static final int REQUEST_PERMISSION_CODE = 12345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Build.VERSION.SDK_INT > é a versão do SDK que está rodando o aplicativo.
        if (Build.VERSION.SDK_INT >= 23){
            checkAndRequestPermissions();
        }
        setContentView(R.layout.activity_main);

        mHandler = new Handler(Looper.getMainLooper());


        // VERSAO ERIC
        informacoesDispositivo = findViewById(R.id.button_Main_InfDisp); // Sera iniciado por função

    }

    // Verifica o estado das permissoes.
    private void checkAndRequestPermissions(){
        // Laço que cria uma string com o nome de eachPermition que tem o valor de cada uma das strings existentes
        //  na lista "REQUIRED_PERMISSION_LIST"
        for (String eachPermition : REQUIRED_PERMISSION_LIST){
            // cada uma das Strings daquela lista (eachPermition) é comparada com as permissões dadas.
            if(ContextCompat.checkSelfPermission(this, eachPermition) != PackageManager.PERMISSION_GRANTED){
                // Se uma permissão eachPermition não esta elencada nas permissoes dadas, ele adiciona.
                missingPermission.add(eachPermition);
            }
        }
        // Se não há mais permissões a serem dadas, inicia-se a função startSDKRegistration();
        if (missingPermission.isEmpty()){
            startSDKRegistration();
            // Se a versão do celular for maior do que a deste codigo.
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            showToast("Need to grant the permissions!");
            // Faz o requerimento das permissões essa função sera puxada em onRequestPermissionsResult
            ActivityCompat.requestPermissions(this,
                    // Cria uma lista com as permissões não dadas, e cria um codigo para acessalas posIf there is enough permission, we will start the registrationteriormente
                    missingPermission.toArray(new String[missingPermission.size()]),
                    REQUEST_PERMISSION_CODE);
            Toast.makeText(this, missingPermission.get(0).toString(), Toast.LENGTH_SHORT).show();
        }
    }
    // Retorna o resultado das requisições de permissoes
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check for granted permission and remove from missing list
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = grantResults.length - 1; i >= 0; i--) {

                // Verifica se há permissao aceita na lista de permissoes não aceitas e a remove de la
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    missingPermission.remove(permissions[i]);
                }
            }
        }
        // Se finalmente tiver nenhuma permissao pendente, StartSDKRegistration
        if (missingPermission.isEmpty()) {
            startSDKRegistration();
        } else {
            showToast("Missing permissions!!!");
        }
    }

    // Função que faz a registração do SDK
    private void startSDKRegistration() {
        // Utiliza um metodo para comparar boleanos
        if (isRegistrationInProgress.compareAndSet(false, true)) {
            // Executa operações em segundo plano
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    showToast("registering, pls wait...");
                    djisdkManagerIniciada.registerApp(MainActivity.this.getApplicationContext(), new DJISDKManager.SDKManagerCallback() {
                        @Override
                        public void onRegister(DJIError djiError) {
                            if (djiError == DJISDKError.REGISTRATION_SUCCESS) {
                                showToast("Register Success");
                                DJISDKManager.getInstance().startConnectionToProduct();
                            } else {
                                showToast("Register sdk fails, please check the bundle id and network connection!");
                            }
                            Log.v(TAG, djiError.getDescription());
                        }

                        @Override
                        public void onProductDisconnect() {
                            Log.d(TAG, "onProductDisconnect");
                            showToast("Product Disconnected");
                            notifyStatusChange();
                        }

                        @Override
                        public void onProductConnect(BaseProduct baseProduct) {
                            Log.d(TAG, String.format("onProductConnect newProduct:%s", baseProduct));
                            showToast("Product Connected");
                            habilitarBotoes = true;
                            notifyStatusChange();

                        }

                        @Override
                        public void onProductChanged(BaseProduct baseProduct) {

                        }

                        @Override
                        public void onComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent baseComponent, BaseComponent baseComponent1) {
                            if (baseComponent1 != null){
                                baseComponent1.setComponentListener(new BaseComponent.ComponentListener() {
                                    @Override
                                    public void onConnectivityChange(boolean b) {
                                        Log.d(TAG, "onComponentConnectivityChanged: " + b);
                                        notifyStatusChange();
                                    }
                                });
                            }
                            Log.d(TAG, String.format("onComponentChange key:%s, oldComponent:%s, newComponent:%s",
                                    componentKey,
                                    baseComponent,
                                    baseComponent1));

                        }

                        @Override
                        public void onInitProcess(DJISDKInitEvent djisdkInitEvent, int i) {

                        }

                        @Override
                        public void onDatabaseDownloadProgress(long l, long l1) {

                        }
                    });

                }
            });


        }
    }

    private void notifyStatusChange() {
        mHandler.removeCallbacks(updateRunnable);
        mHandler.postDelayed(updateRunnable, 500);
    }

    private Runnable updateRunnable = new Runnable() {

        @Override
        public void run() {
            Intent intent = new Intent(FLAG_CONNECTION_CHANGE);
            sendBroadcast(intent);
        }
    };
    private void showToast(final String toastMsg) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
            }
        });

    }

    // VERSAO ERIC
    public void informacoesDispositivo(View view){
        if (habilitarBotoes) {
            Intent intent = new Intent(getApplicationContext(), InformacoesDispositivo.class);
            startActivity(intent);
        } else{
            Toast.makeText(this, "Espere carregar o drone", Toast.LENGTH_SHORT).show();
        }
    }


}