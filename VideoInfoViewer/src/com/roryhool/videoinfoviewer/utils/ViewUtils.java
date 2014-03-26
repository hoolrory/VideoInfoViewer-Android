package com.roryhool.videoinfoviewer.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

public class ViewUtils {

   public static int GetStatusBarHeight( Context context ) {
      int result = 0;
      int resourceId = context.getResources().getIdentifier( "status_bar_height", "dimen", "android" );
      if ( resourceId > 0 ) {
         result = context.getResources().getDimensionPixelSize( resourceId );
      }
      return result;
   }

   public static int GetActionBarHeight( Context context ) {
      TypedValue typedValue = new TypedValue();
      int[] actionBarSizeAttr = new int[] { android.R.attr.actionBarSize };
      int indexOfAttrTextSize = 0;
      TypedArray a = context.obtainStyledAttributes( typedValue.data, actionBarSizeAttr );
      int actionBarSize = a.getDimensionPixelSize( indexOfAttrTextSize, -1 );
      a.recycle();

      return actionBarSize;
   }
}
