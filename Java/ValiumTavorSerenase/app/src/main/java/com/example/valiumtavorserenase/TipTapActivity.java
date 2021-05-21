package com.example.valiumtavorserenase;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class TipTapActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView img1,img2,img3,img4, imgSetting;
    private Button btnInizia,btnFine, btnRipeti, btnSfida;
    private ArrayList<Giocata> giocate=new ArrayList<>();
    private long start;
    private Boolean iniziasfida=false;
    private int cont=0, indice=0, vite=3;
    private SharedPreferences pref;
    private String stringa; //Stringa per salvare la sequenza

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_tap);
        img1=findViewById(R.id.img1);
        img2=findViewById(R.id.img2);
        img3=findViewById(R.id.img3);
        img4=findViewById(R.id.img4);
        btnRipeti=findViewById(R.id.btnRipeti);
        btnFine=findViewById(R.id.btnFine);
        imgSetting=findViewById(R.id.imgSetting);
        btnInizia=findViewById(R.id.btnInizia);
        btnSfida=findViewById(R.id.btnSfida);
        btnInizia.setOnClickListener(this);
        btnFine.setOnClickListener(this);
        btnRipeti.setOnClickListener(this);
        btnSfida.setOnClickListener(this);
        imgSetting.setOnClickListener(this);
        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);
        img4.setOnClickListener(this);

        imposta(110,false,true,false, false, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        vite = Integer.parseInt(pref.getString("nVite","3"));
    }

    @Override
    public void onClick(View v) {
        int mioId=v.getId();
        switch (mioId){
            case R.id.btnInizia:
                imposta(255,true,false,true, false, false);
                giocate.add(new Giocata(start));
                break;

            case R.id.img1:
            case R.id.img2:
            case R.id.img3:
            case R.id.img4:
                if(iniziasfida) sfida(mioId);
                else immagineCLick(mioId);
                break;

            case R.id.btnFine: //Fine inserimento sequenza
                imposta(110,false,true,false, true, true);
                stringa=giocate.get(indice).toString();
                if(!controlloSequenza()){
                    giocate.remove(indice);
                    if (indice>0) indice--;
                } else{
                    indice++;
                }
                break;

            case R.id.btnRipeti: //ripeti ultima sequenza
                if(controlloSequenza()) {
                    Thread th = new Thread() {
                        @Override
                        public void run() {
                            imposta(255,false,true,false, true, true);
                            impostaPulsanti(false, false, false);
                            giocate.get(indice-1).ripeti(img1,img2,img3,img4,500);
                            imposta(110,false,true,false, true, true);
                            impostaPulsanti(true, true, true);
                        }
                    };
                    th.start();
                }
                break;

            case R.id.btnSfida:
                if(controlloSequenza()) {
                    iniziasfida = true;
                    imposta(255, true, false, false, false, false);
                    cont = 0;
                }
                break;

            case R.id.imgSetting: //Impostazioni
                Intent avviaSetting= new Intent(this,SettingsActivity.class);
                startActivity(avviaSetting);
                break;

        }
    }



    private void immagineCLick(int num){ //aggiunge un valore alla sequenza
        Giocata g=giocate.get(indice);
        g.aggiungi(this,num);
    }

    private void imposta(int trasparenza, boolean clickImg, boolean clickInizia, boolean clickFine, boolean clickRipeti, boolean clickSfida){
        img1.setImageAlpha(trasparenza);
        img1.setClickable(clickImg);
        img2.setImageAlpha(trasparenza);
        img2.setClickable(clickImg);
        img3.setImageAlpha(trasparenza);
        img3.setClickable(clickImg);
        img4.setImageAlpha(trasparenza);
        img4.setClickable(clickImg);
        btnInizia.setEnabled(clickInizia);
        btnFine.setEnabled(clickFine);
        btnRipeti.setEnabled(clickRipeti);
        btnSfida.setEnabled(clickSfida);
    }

    private void impostaPulsanti(boolean inizia, boolean ripeti, boolean sfida){
        btnInizia.setClickable(inizia);
        btnRipeti.setClickable(ripeti);
        btnSfida.setClickable(sfida);
    }

    private void sfida(int n){
        int nVite=Integer.parseInt(pref.getString("nVite","3"));
        if(n!=giocate.get(indice-1).getSeguenza().get(cont).getValore()){
            if(pref.getBoolean("vite",true)) {
                vite--;
                if(vite>=0 && pref.getBoolean("errori",true)) Toast.makeText(this,"ERRORE. "+ vite +" vite",Toast.LENGTH_LONG).show();
            }
        }
        else {
            if (cont==giocate.get(indice - 1).getSeguenza().size()-1){ //Vittoria, imposto il messaggio nell'alert
                vite = nVite;
                stampaAlert("WIN","HAI INDOVINATO LA SEGUENZA",R.drawable.v);
            }
            cont++;
        }
        if(vite==-1){ //Vite finite, imposto il messaggio nell'alert
            stampaAlert("GAME OVER","HAI SBAGLIATO LA SEGUENZA",R.drawable.x);
            vite = nVite;
        }
    }

    private void stampaAlert(String titolo, String messaggio, int icona){
        AlertDialog.Builder finestra = new AlertDialog.Builder(this);
        finestra.setTitle(titolo);
        finestra.setMessage(messaggio);
        finestra.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cont=0;
                iniziasfida=false;
                imposta(255,false,true,false, true, true);
            }
        });
        finestra.setIcon(icona);
        finestra.create();
        finestra.setCancelable(false); //se clicco fuori, l'alert dialog non si cancella
        finestra.show();

    }

    private boolean controlloSequenza(){ //controllo se la sequenza è stata inserita
        if(stringa.isEmpty()){
            Toast.makeText(this, "nessuna sequenza inserita", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}