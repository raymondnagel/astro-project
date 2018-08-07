/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.shipweapons;

import astro.Astro;
import astro.objects.projectiles.IonRay;
import astro.sound.Sound;
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 *
 * @author Raymond
 */
public class IonCannon extends ShipWeapon
{

    public IonCannon()
    {
        myName = "Ion Cannon";
        myRequiredPower = 20;
        myFireDelay = 1200;
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
            IonRay ray = new IonRay(myHost);
            ray.setCenterPoint(pp);
            Astro.addSprite(ray);
            myHost.decreasePower(getRequiredPower());
            Astro.getSoundManager().makeNoise(myHost, new Sound("extern/sound_fx/warble.wav"));
        }
    }


}
