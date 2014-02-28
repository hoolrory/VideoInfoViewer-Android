package com.roryhool.videoinfoviewer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

public class ScaledTextureView extends TextureView {

   int mVideoWidth;
   int mVideoHeight;

   public ScaledTextureView( Context context ) {
      super( context );
   }

   public ScaledTextureView( Context context, AttributeSet attrs ) {
      super( context, attrs );
   }

   public ScaledTextureView( Context context, AttributeSet attrs, int defStyle ) {
      super( context, attrs, defStyle );
   }

   public void SetVideoSize( int videoWidth, int videoHeight ) {
      mVideoWidth = videoWidth;
      mVideoHeight = videoHeight;
   }

   @Override
   protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ) {
      int parentSpecifiedWidth = MeasureSpec.getSize( widthMeasureSpec );
      int parentSpecifiedHeight = MeasureSpec.getSize( heightMeasureSpec );

      double heightToWidthRatio = (double) mVideoHeight / (double) mVideoWidth;

      int width;
      int height;

      if ( parentSpecifiedWidth * heightToWidthRatio > parentSpecifiedHeight ) {
         width = (int) ( parentSpecifiedHeight / heightToWidthRatio );
         height = parentSpecifiedHeight;
      } else {
         width = parentSpecifiedWidth;
         height = (int) ( parentSpecifiedWidth * heightToWidthRatio );
      }

      Log.d( "this", String.format( "onMeasure(%d, %d", width, height ) );
      super.onMeasure( MeasureSpec.makeMeasureSpec( width, MeasureSpec.EXACTLY ), MeasureSpec.makeMeasureSpec( height, MeasureSpec.EXACTLY ) );
   }
}
