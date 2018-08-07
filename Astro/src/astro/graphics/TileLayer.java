/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.graphics;

import astro.Astro;
import java.awt.Dimension;
import java.awt.Graphics2D;

/**
 *
 * @author Raymond Nagel
 */
public class TileLayer implements SectorLayer
{
    private int myColumns = 0;
    private int myRows = 0;
    private Dimension myTileSize = null;
    private boolean myVisible = true;
    private Tile[][] myTiles = null;

    public TileLayer(int rows, int columns, Dimension tileSize)
    {
        myTileSize = tileSize;
        myRows = rows;
        myColumns = columns;
        myTiles = new Tile[columns][rows];
    }

    public int getWidth()
    {
        return myTileSize.width*myColumns;
    }
    public int getHeight()
    {
        return myTileSize.height*myRows;
    }

    public boolean isVisible()
    {
        return myVisible;
    }

    public void setTile(int x, int y, Tile t)
    {
        if (t.getWidth() != myTileSize.width || t.getHeight() != myTileSize.height)
        {
            System.err.println("Tile size mismatch!");
        }
        else
        {
            myTiles[x][y] = t;
        }
    }
    public Tile getTile(int x, int y)
    {
        return myTiles[x][y];
    }

    public void draw(Graphics2D g, int scrX, int scrY)
    {
        int tX = scrX / myTileSize.width;
        int tY = scrY / myTileSize.height;
        int osX = scrX % myTileSize.width;
        int osY = scrY % myTileSize.height;
        int tW = (Astro.SCREEN_W / myTileSize.width) + osX == 0 ? 0 : myTileSize.width;
        int tH = (Astro.SCREEN_H / myTileSize.height) + osY == 0 ? 0 : myTileSize.height;
        int pX, pY;
        for (int y = tY; y < tY+tH; y++)
        {
            for (int x = tX; x < tX+tW; x++)
            {
                pX = (x*myTileSize.width) - osX;
                pY = (y*myTileSize.height) - osY;
                g.drawImage(getTile(x, y).getImage(), pX, pY, null);
            }
        }
    }

}
