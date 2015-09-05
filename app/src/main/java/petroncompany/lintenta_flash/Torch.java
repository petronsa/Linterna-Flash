package petroncompany.lintenta_flash;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;

/**
 * Created by Usuario on 03/09/2015.
 */
public class Torch
{
    private Camera camera;
    private Parameters parameters;
    private boolean on;

    public Torch()
    {
        camera = Camera.open();
        parameters = camera.getParameters();
        camera.startPreview();
        on = false;
    }

    public void on()
    {
        if (!on)
        {
            on = true;
            parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameters);
        }
    }

    public void off()
    {
        if (on)
        {
            on = false;
            parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameters);
        }
    }

    public void release()
    {
        camera.stopPreview();
        camera.release();
    }

    public boolean isOn()
    {
        return on;
    }
}
