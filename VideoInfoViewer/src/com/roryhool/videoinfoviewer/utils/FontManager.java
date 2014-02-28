package com.roryhool.videoinfoviewer.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

public class FontManager {

   Typeface mRobotoRegular;
   Typeface mRobotoLight;
   Typeface mRobotoBold;

   public FontManager( Context context ) {

      mRobotoRegular = Typeface.createFromAsset( context.getAssets(), "fonts/Roboto-Regular.ttf" );
      mRobotoLight = Typeface.createFromAsset( context.getAssets(), "fonts/Roboto-Light.ttf" );
      mRobotoBold = Typeface.createFromAsset( context.getAssets(), "fonts/Roboto-Bold.ttf" );
   }

   public Typeface getRobotoRegular() {
      return mRobotoRegular;
   }

   public Typeface getRobotoLight() {
      return mRobotoLight;
   }

   public Typeface getRobotoBold() {
      return mRobotoBold;
   }

   public void setRobotoRegular( TextView view ) {
      view.setTypeface( mRobotoRegular );
   }

   public void setRobotoLight( TextView view ) {
      view.setTypeface( mRobotoLight );
   }

   public void setRobotoBold( TextView view ) {
      view.setTypeface( mRobotoBold );
   }

   private static FontManager sManager;

   public static FontManager get( Context context ) {
      if ( sManager == null ) {
         sManager = new FontManager( context );
      }

      return sManager;
   }
}
