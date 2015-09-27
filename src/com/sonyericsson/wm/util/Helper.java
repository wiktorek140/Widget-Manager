package com.sonyericsson.wm.util;

import com.nokia.mid.ui.DirectGraphics;
import com.nokia.mid.ui.DirectUtils;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public final class Helper
{
   private static final int NON_RUNNING_TRANSPARENCY = 102;

   public static Helper getInstance()
   {
      return Helper.Holder.INSTANCE;
   }
   
   private Helper()
   {
   }
   
   public Image imageGreyScaled(Image image)
   {
      Image greyImage;
      int[] rawInput = new int[image.getHeight() * image.getWidth()];
      image.getRGB(rawInput, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
      int i = 0;
      for (; i < rawInput.length; i++) 
      {
         int argb = rawInput[i];
         int a = argb >> 24 & 0xff;
         int r = argb >> 16 & 0xff;
         int g = argb >> 8 & 0xff;
         int b = argb & 0xff;
         int grey = (int) (0.212671 * (double) r + 0.71516 * (double) g + 0.072169 * (double) b);
         r = grey;
         g = grey;
         b = grey;
         if (a > 102)
         {
            argb = 1711276032;
         }
         else
         {
            argb = a << 24;
         }
         argb += (r & 0xff) << 16;
         argb += (g & 0xff) << 8;
         argb += b & 0xff;
         rawInput[i] = argb;
      }
      try
      {
         greyImage = Image.createRGBImage(rawInput, image.getWidth(), image.getHeight(), true);
      }
      catch (Exception e)
      {
         greyImage = image;
      }
      return greyImage;
   }
   
   public Image scaleImage(Image image, int newWidth, int newHeight)
   {
      int[] rawInput = new int[image.getHeight() * image.getWidth()];
      image.getRGB(rawInput, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
      int[] rawOutput = new int[newWidth * newHeight];
      int yD = image.getHeight() / newHeight * image.getWidth() - image.getWidth();
      int yR = image.getHeight() % newHeight;
      int xD = image.getWidth() / newWidth;
      int xR = image.getWidth() % newWidth;
      int outOffset = 0;
      int inOffset = 0;
      int y = newHeight;
      int yE = 0;
      for (; y > 0; y--) 
      {
         int x = newWidth;
         int xE = 0;
         for (; x > 0; x--) 
         {
            rawOutput[outOffset++] = rawInput[inOffset];
            inOffset += xD;
            xE += xR;
            if (xE >= newWidth)
            {
               xE -= newWidth;
               inOffset++;
            }
         }
         inOffset += yD;
         yE += yR;
         if (yE >= newHeight)
         {
            yE -= newHeight;
            inOffset += image.getWidth();
         }
      }
      return Image.createRGBImage(rawOutput, newWidth, newHeight, false);
   }
   
   public void setAlpha(Graphics g, int a)
   {
      DirectGraphics dg = DirectUtils.getDirectGraphics(g);
      dg.setARGBColor(a << 24 | g.getColor());
   }
   
   public void setAlphaColor(Graphics g, int a, int rgb)
   {
      DirectGraphics dg = DirectUtils.getDirectGraphics(g);
      dg.setARGBColor(a << 24 | rgb);
   }
   
   public void setColor(Graphics g, int rgb)
   {
      DirectGraphics dg = DirectUtils.getDirectGraphics(g);
      dg.setARGBColor(dg.getAlphaComponent() << 24 | rgb);
   }
   
   static final class Holder
   {
      private static final Helper INSTANCE;
   
      static
      {
         INSTANCE = new Helper();
      }
      
      private Holder()
      {
      }
      
   }

}