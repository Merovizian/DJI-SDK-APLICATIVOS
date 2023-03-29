package ifes.eric.importsdkdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import dji.sdk.base.BaseProduct;
import nhf.ghu.ghu.nhf;

public class MainActivity extends AppCompatActivity {
    public TextView versaoTexto;
    private static final String TAG =  MainActivity.class.getName();
    public static final String FLAG_CONNECTION_CHANGE = "dji_sdk_connection_change";
    private static BaseProduct mProduct;
    private Handler mHandler;
    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
            Manifest.permission.VIBRATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
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
        setContentView(R.layout.activity_main);

        versaoTexto = findViewById(R.id.TextoMain);
        versaoTexto.setText(String.valueOf(Build.VERSION.SDK_INT));
        // Build.VERSION.SDK_INT > é a versão do SDK que está rodando o aplicativo.
        if (Build.VERSION.SDK_INT >= 23){
           checkAndRequestPermissions();
        }


        mHandler = new Handler(Looper.getMainLooper());
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
            Toast.makeText(this, "Dê as permissoes", Toast.LENGTH_SHORT).show();
            // Faz o requerimento das permissões essa função sera puxada em onRequestPermissionsResult
            ActivityCompat.requestPermissions(this,
                    //
                    missingPermission.toArray(new String[missingPermission.size()]),
                    REQUEST_PERMISSION_CODE);
        }
    }
    // Retorna o resultado das solicitações de permissçoes
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check for granted permission and remove from missing list
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = grantResults.length - 1; i >= 0; i--) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    missingPermission.remove(permissions[i]);
                }
            }
        }
        // If there is enough permission, we will start the registration
        if (missingPermission.isEmpty()) {
            startSDKRegistration();
        } else {
            showToast("Missing permissions!!!");
        }
    }





}