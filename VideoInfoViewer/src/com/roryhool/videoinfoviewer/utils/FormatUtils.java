package com.roryhool.videoinfoviewer.utils;

import java.util.Locale;

public class FormatUtils {

   public static String FormatTimeForDisplay( long ms ) {

      long s = ( ms / 1000 ) % 60;
      long m = ms / ( 1000 * 60 ) % 60;
      long h = ms / ( 1000 * 60 * 60 );

      String sString = s < 10 ? "0" + s : "" + s;
      String mString = m < 10 ? "0" + m : "" + m;
      String hString = h < 10 ? "0" + h : "" + h;

      String durationString = String.format( Locale.US, "%s:%s", mString, sString );

      if ( h != 0 ) {
         durationString = String.format( Locale.US, "%s:%s:%s", hString, mString, sString );
      }

      return durationString;
   }
}
