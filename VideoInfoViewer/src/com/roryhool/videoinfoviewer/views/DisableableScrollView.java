package com.roryhool.videoinfoviewer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class DisableableScrollView extends ScrollView {

   boolean mEnabled = true;

   public DisableableScrollView( Context context ) {
      super( context );

   }

   public DisableableScrollView( Context context, AttributeSet attrs ) {
      super( context, attrs );

   }

   public DisableableScrollView( Context context, AttributeSet attrs, int defStyle ) {
      super( context, attrs, defStyle );

   }

   public void setEnabled( boolean enabled ) {
      mEnabled = enabled;
   }

   @Override
   public boolean onTouchEvent( MotionEvent event ) {
      if ( mEnabled ) {
         return super.onTouchEvent( event );
      } else {
         return false;
      }
   }

   @Override
   public boolean onInterceptTouchEvent( MotionEvent event ) {
      if ( mEnabled ) {
         return super.onInterceptTouchEvent( event );
      } else {
         return false;
      }
   }

}
