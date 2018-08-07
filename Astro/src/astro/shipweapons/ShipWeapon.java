/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.shipweapons;

import astro.objects.spaceships.Ship;

/**
 *
 * @author Raymond
 */
public abstract class ShipWeapon
{
    protected Ship myHost = null;
    protected String myName = "Weapon";
    protected int myRequiredPower = 1;
    protected long myFireDelay = 200;
    private long myLastFire = 0;

    public double getReadyPct()
    {
        return (double)(System.currentTimeMillis()-myLastFire) / (double)myFireDelay;
    }

    public int getRequiredPower()
    {
        return myRequiredPower;
    }

    public String getName()
    {
        return myName;
    }

    public void setHost(Ship host)
    {
        myHost = host;
    }

    public Ship getHost()
    {
        return myHost;
    }

    protected abstract void fire();

    public void tryFire()
    {
        if (System.currentTimeMillis() >= myLastFire+myFireDelay)
        {
            fire();
            myLastFire = System.currentTimeMillis();
        }
    }
}
