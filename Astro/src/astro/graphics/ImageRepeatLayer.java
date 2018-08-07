/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.graphics;

import astro.Astro;
import java.awt.Graphics2D;

/**
 *
 * @author Raymond Nagel
 */
public class ImageRepeatLayer extends ImageLayer
{
    private double myCloseness = 0;

    public ImageRepeatLayer(int imageType, double closeness)
    {
        super(Astro.SCREEN_W, Astro.SCREEN_H, imageType);
        myCloseness = closeness;
    }

    @Override
    public void draw(Graphics2D g, int scrX, int scrY)
    {
        int screenW = Astro.SCREEN_W;
        int screenH = Astro.SCREEN_H;
        int scrollX = Astro.getScrollX();
        int scrollY = Astro.getScrollY();
        int right = (int)(((scrollX*myCloseness) % screenW));
        int bottom = (int)(((scrollY*myCloseness) % screenH));
        int left = (int)((screenW - right));
        int top = (int)((screenH - bottom));
        g.drawImage(this, 0, 0, left, top, right, bottom, screenW, screenH, null);// LEFT-TOP
        g.drawImage(this, left, 0, screenW, top, 0, bottom, right, screenH, null);// RIGHT-TOP
        g.drawImage(this, 0, top, left, screenH, right, 0, screenW, bottom, null); // LEFT-BOTTOM
        g.drawImage(this, left, top, screenW, screenH, 0, 0, right, bottom, null); // RIGHT-BOTTOM
    }
}
