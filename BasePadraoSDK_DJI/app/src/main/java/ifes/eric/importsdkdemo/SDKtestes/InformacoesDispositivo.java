package ifes.eric.importsdkdemo.SDKtestes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import dji.midware.data.manager.P3.DJIProductManager;
import ifes.eric.importsdkdemo.R;

public class InformacoesDispositivo extends AppCompatActivity {
    public TextView modeloDJI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacoes_dispositivo);

        modeloDJI = findViewById(R.id.text_infDis_Modelo);


;








    }
}