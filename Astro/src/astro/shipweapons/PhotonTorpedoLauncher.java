/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.shipweapons;

import astro.Astro;
import astro.objects.projectiles.PhotonTorpedo;
import astro.sound.Sound;
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 *
 * @author Raymond
 */
public class PhotonTorpedoLauncher extends ShipWeapon
{

    public PhotonTorpedoLauncher()
    {
        myName = "Photon Torpedo Launcher";
        myRequiredPower = 25;
        myFireDelay = 4000;
    }

    @Override
    public void fire()
    {
        if (myHost.getPower() >= getRequiredPower())
        {
            int dist = myHost.getWidth();
            Point sp = myHost.getCenterPoint();
            Point2D dirPoint = myHost.getAdjustedDir();
            Point pp = new Point((int)(sp.x + dirPoint.getX()*dist), (int)(sp.y + dirPoint.getY()*dist));
            PhotonTorpedo torp = new PhotonTorpedo(myHost);
            torp.setCenterPoint(pp);
            Astro.addSprite(torp);
            myHost.decreasePower(getRequiredPower());
            Astro.getSoundManager().makeNoise(myHost, new Sound("extern/sound_fx/photon.wav"));
        }
    }


}
