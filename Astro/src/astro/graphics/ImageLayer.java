/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.graphics;

import astro.Astro;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Raymond Nagel
 */
public class ImageLayer extends BufferedImage implements SectorLayer
{
    private boolean myVisible = true;

    public ImageLayer(int width, int height, int imageType)
    {
        super(width, height, imageType);
    }

    public boolean isVisible()
    {
        return myVisible;
    }

    public void draw(Graphics2D g, int scrX, int scrY)
    {
        int w = Astro.SCREEN_W;
        int h = Astro.SCREEN_H;
        g.drawImage(this, 0, 0, w, h, scrX, scrY, scrX+w, scrY+h, null);
    }

}
