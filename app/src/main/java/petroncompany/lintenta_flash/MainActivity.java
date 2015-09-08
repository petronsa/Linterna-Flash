package petroncompany.lintenta_flash;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;


public class MainActivity extends AppCompatActivity implements OnClickListener{
    private static final int NOTIFICATION_ID = 1;
    private Torch torch;
    private WakeLock wakeLock;
    private NotificationManager notificationManager;
    ToggleButton boton;
    TextView pulsar;
    private StartAppAd startAppAd = new StartAppAd(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartAppSDK.init(this, "101423750", "208444535", true);
        setContentView(R.layout.activity_main);

        boton = (ToggleButton) findViewById(R.id.toggleButton);
        boton.setOnClickListener(this);
        pulsar = (TextView)findViewById(R.id.pulsar);



    if (!initTorch())
        {
            return;
        }

        //Encener el flash


        torch.on();
        //imagen boton encendida
        boton.setBackgroundDrawable(getResources().getDrawable(R.drawable.linterna_encendida));
        //Texto pulsar apagar
        this.pulsar.setText((getResources().getString(R.string.pulsar_off)));

        //Adquirir wake lock

        PowerManager powerManager =
                (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"Flash");
        wakeLock.setReferenceCounted(false);
        if (!wakeLock.isHeld())
        {
            wakeLock.acquire();
        }

        //Iniciar el notificationManager
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //Creamos la notificacion
        createNotification();
    }

    private boolean initTorch()
    {
     //Acceder a la camara
        try {
            torch = new Torch();
        }
        catch (Exception e)
        {
            Toast.makeText(this,getResources().getString(R.string.error),Toast.LENGTH_LONG).show();
            finish();
            return false;
        }
        return true;
    }


    public void createNotification()
    {
        Intent intent = new Intent(this,MainActivity.class);

        PendingIntent pedingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(getResources().getString(R.string.notificacion))
                .setOngoing(true)
                .setContentIntent(pedingIntent)
                .build();
        notificationManager.notify(NOTIFICATION_ID,notification);
    }

    public void destruirNotificacion()
    {
        notificationManager.cancel(NOTIFICATION_ID);
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();


        //destruir notificacion

        destruirNotificacion();
        //poner en marcha la publicidad al salir
        MainActivity.this.startAppAd.showAd();
        MainActivity.this.startAppAd.loadAd();

        //Apagar el flash
        torch.release();
        torch = null;

        //Soltar wake lock

        wakeLock.release();
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onClick(View view)
    {

        if (torch.isOn())
        {
            torch.off();
            destruirNotificacion();
            //cambiar imagen del boton
            boton.setBackgroundDrawable(getResources().getDrawable(R.drawable.linterna_apagada));
            //Texto pulsar encender
            this.pulsar.setText((getResources().getString(R.string.pulsar_on)));
        }
        else
        {
            torch.on();
            createNotification();
            //cambiar imagen del boton
            boton.setBackgroundDrawable(getResources().getDrawable(R.drawable.linterna_encendida));
            //Texto pulsar apagar
            this.pulsar.setText((getResources().getString(R.string.pulsar_off)));
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if (torch == null && initTorch())
        {
            wakeLock.acquire();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        if (torch != null && !torch.isOn())
        {
            torch.release();
            torch = null;
            wakeLock.release();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        startAppAd.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        startAppAd.onPause();
    }

}
