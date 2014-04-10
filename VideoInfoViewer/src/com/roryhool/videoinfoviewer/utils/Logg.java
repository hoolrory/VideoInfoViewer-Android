package com.roryhool.videoinfoviewer.utils;

import android.util.Log;

public class Logg {

   public static void d( String format, Object... args ) {
      Log.d( "Logg", String.format( format, args ) );
   }

}
