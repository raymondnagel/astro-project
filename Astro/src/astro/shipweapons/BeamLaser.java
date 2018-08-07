/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.shipweapons;

import astro.Astro;
import astro.objects.Bang;
import astro.objects.Beam;
import astro.objects.projectiles.Laser.SIZES;
import astro.objects.spaceships.Ship;
import astro.sound.Sound;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import java.util.Vector;

/**
 *
 * @author Raymond
 */
public class BeamLaser extends ShipWeapon
{
    protected SIZES  mySize = SIZES.SM;
    protected Color  myColor = Color.BLUE;
    protected int    myMaxDistance = 1000;
    protected float  myBeamWidth = 1.0f;

    public BeamLaser(Color color, SIZES size)
    {
        mySize = size;
        myName = "Beam Laser - " + myColor.getRGB()+ ", " + size.name();
        myColor = color;
        myFireDelay = 300;
        switch (mySize)
        {
            case SM:
                myBeamWidth = 3.0f;
                break;
            case MED:
                myBeamWidth = 5.0f;
                break;
            case LG:
                myBeamWidth = 7.0f;
                break;
        }
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
            int endX = pp.x + (int)(myHost.getXDir()*myMaxDistance);
            int endY = pp.y + (int)(myHost.getYDir()*myMaxDistance);
            
            myHost.decreasePower(getRequiredPower());
            String sizeText = mySize.name().toLowerCase();
            Astro.getSoundManager().makeNoise(myHost, new Sound("extern/sound_fx/laser_" + sizeText + ".wav"));

            Vector<Ship> ships = Astro.getShips();
            Ship target = null;
            double shortestDist = Double.MAX_VALUE;
            for (int s = 0; s < ships.size(); s++)
            {
                if (ships.get(s) != myHost)
                {
                    Line2D.Float line = new Line2D.Float(pp.x, pp.y, endX, endY);
                    Rectangle2D.Float rect = new Rectangle2D.Float(ships.get(s).getX(), ships.get(s).getY(), ships.get(s).getWidth(), ships.get(s).getHeight());
                    if (rect.intersectsLine(line))
                    {
                        double range = myHost.getDistanceFromPoint(ships.get(s).getCenterPoint());
                        if (range <= shortestDist)
                        {
                            shortestDist = range;
                            target = ships.get(s);
                        }
                    }
                }
            }
            if (target==null)
                Astro.addDrawing(new Beam(pp, new Point(endX, endY), myColor, myBeamWidth));
            else
            {
                impact(target);
                Astro.addDrawing(new Beam(pp, target.getCenterPoint(), myColor, myBeamWidth));
            }
        }
    }

    public void impact(Ship ship)
    {
        Astro.getSoundManager().makeNoise(myHost, new Sound("extern/sound_fx/bang.wav"));
        Astro.addSprite(new Bang(ship, ship.getCenterPoint().x, ship.getCenterPoint().y));
        ship.hurt(getDamagePower());
    }

    protected int getDamagePower()
    {
        int base = 0, mult = 0, die = 0;
        switch(mySize)
        {
            case SM:
                base = 50;
                mult = 1;
                die = 50;
                break;
            case MED:
                base = 100;
                mult = 1;
                die = 100;
                break;
            case LG:
                base = 200;
                mult = 1;
                die = 200;
                break;
        }
        return base + (mult * (new Random().nextInt(die)));
    }
}
