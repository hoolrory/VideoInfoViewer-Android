/**
   Copyright (c) 2016 Rory Hool
   
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
   
       http://www.apache.org/licenses/LICENSE-2.0
   
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 **/

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
