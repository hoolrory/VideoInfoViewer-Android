package com.roryhool.videoinfoviewer.utils;

import android.content.Context;

public class ViewUtils {

   public static int GetStatusBarHeight( Context context ) {
      int result = 0;
      int resourceId = context.getResources().getIdentifier( "status_bar_height", "dimen", "android" );
      if ( resourceId > 0 ) {
         result = context.getResources().getDimensionPixelSize( resourceId );
      }
      return result;
   }
}
