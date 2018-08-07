/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.objects.projectiles;

import astro.objects.*;
import astro.Astro;
import astro.graphics.AnimFrame;
import astro.graphics.Animation;
import java.util.Random;

/**
 *
 * @author Raymond
 */
public class Rocket extends Projectile
{
    public static final double SPEED = 700;
    private int myCores = 1;

    static
    {
        AnimFrame.loadFrame("sprites/rocket_1.png", 2);
        AnimFrame.loadFrame("sprites/rocket_2.png", 2);
    }

    public Rocket(Sprite owner, int cores)
    {
        super(owner);
        setImage(Astro.getImageFromFile("sprites/rocket_1.png"));
        String frames[] = {"sprites/rocket_1.png", "sprites/rocket_2.png"};
        Animation rocket = new Animation("rocket", true, frames);
        this.setAnimation(rocket);
        setSpeedFactor(SPEED);        
        myMaxDistance = 3000;
        myCores = cores;
        setAngle(owner.getAngle());
    }

    @Override
    public void update()
    {
        getCurrentAnimation().advance();
        super.update();
    }



    @Override
    protected int getDamagePower()
    {
        int base = 100, mult = myCores, die = 200;
        return base + (mult * (new Random().nextInt(die)));
    }

}
