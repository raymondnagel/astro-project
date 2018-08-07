/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.objects.projectiles;

import astro.objects.*;
import astro.Astro;
import astro.sound.Sound;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 *
 * @author Raymond
 */
public class Laser extends Projectile
{
    public static final double SPEED = 1400;

    public static enum SIZES {SM, MED, LG};
    public static enum COLORS {RED, BLUE, GREEN};
    public static BufferedImage ourRLaserSm = null;
    public static BufferedImage ourGLaserSm = null;
    public static BufferedImage ourBLaserSm = null;
    public static BufferedImage ourRLaserMed = null;
    public static BufferedImage ourGLaserMed = null;
    public static BufferedImage ourBLaserMed = null;
    public static BufferedImage ourRLaserLg = null;
    public static BufferedImage ourGLaserLg = null;
    public static BufferedImage ourBLaserLg = null;

    private SIZES mySize = SIZES.SM;
    private COLORS myColor = COLORS.BLUE;

    static
    {
        ourRLaserSm = Astro.getImageFromFile("sprites/r_laser_sm.png");
        ourGLaserSm = Astro.getImageFromFile("sprites/g_laser_sm.png");
        ourBLaserSm = Astro.getImageFromFile("sprites/b_laser_sm.png");
        ourRLaserMed = Astro.getImageFromFile("sprites/r_laser_med.png");
        ourGLaserMed = Astro.getImageFromFile("sprites/g_laser_med.png");
        ourBLaserMed = Astro.getImageFromFile("sprites/b_laser_med.png");
        ourRLaserLg = Astro.getImageFromFile("sprites/r_laser_lg.png");
        ourGLaserLg = Astro.getImageFromFile("sprites/g_laser_lg.png");
        ourBLaserLg = Astro.getImageFromFile("sprites/b_laser_lg.png");
    }

    public Laser(Sprite owner, SIZES size, COLORS color)
    {
        super(owner);
        mySize = size;
        myColor = color;
        setSpeedFactor(SPEED);        
        if (mySize == SIZES.SM)
        {
            myMaxDistance = 1000;
            if (myColor == COLORS.RED) setImage(ourRLaserSm);
            if (myColor == COLORS.GREEN) setImage(ourGLaserSm);
            if (myColor == COLORS.BLUE) setImage(ourBLaserSm);
        }
        else if (mySize == SIZES.MED)
        {
            myMaxDistance = 1500;
            if (myColor == COLORS.RED) setImage(ourRLaserMed);
            if (myColor == COLORS.GREEN) setImage(ourGLaserMed);
            if (myColor == COLORS.BLUE) setImage(ourBLaserMed);
        }
        else if (mySize == SIZES.LG)
        {
            myMaxDistance = 2000;
            if (myColor == COLORS.RED) setImage(ourRLaserLg);
            if (myColor == COLORS.GREEN) setImage(ourGLaserLg);
            if (myColor == COLORS.BLUE) setImage(ourBLaserLg);
        }
        setAngle(owner.getAngle());
    }


    @Override
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
