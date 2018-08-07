/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.objects;

import astro.Astro;
import java.awt.Graphics2D;

/**
 *
 * @author Raymond
 */
public abstract class Drawing
{
    public static final int FOREVER = -1;
    protected int myDraws = 0;

    public Drawing(int draws)
    {
        myDraws = draws;
    }

    public void render(Graphics2D g)
    {
        if (myDraws == FOREVER)
            draw(g);
        else
        {
            myDraws--;
            draw(g);
            if (myDraws == 0)
            {
                Astro.removeDrawing(this);
            }
        }
    }

    protected abstract void draw(Graphics2D g);

}
