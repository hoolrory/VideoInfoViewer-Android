package com.roryhool.videoinfoviewer.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ResizeAnimation extends Animation {

   int mOriginalHeight;
   int mTargetHeight;
   int mOffsetHeight;
   int mAdjacentHeightIncrement;
   View mView;
   View mAdjacentView;
   boolean mDown;

   public ResizeAnimation( View view, int originalHeight, int targetHeight, boolean down ) {
      mView = view;
      mOriginalHeight = originalHeight;
      mTargetHeight = targetHeight;
      mOffsetHeight = targetHeight - originalHeight;
      mDown = down;
   }

   @Override
   protected void applyTransformation( float interpolatedTime, Transformation t ) {
      int newHeight;
      if ( mDown )
         newHeight = (int) ( mOffsetHeight * interpolatedTime );

      else
         newHeight = (int) ( mOffsetHeight * ( 1 - interpolatedTime ) );

      mView.getLayoutParams().height = newHeight + mOriginalHeight;
      mView.requestLayout();

      if ( mAdjacentView != null ) {
         mAdjacentView.getLayoutParams().height = mView.getLayoutParams().height + mAdjacentHeightIncrement;
         mAdjacentView.requestLayout();
      }
   }

   @Override
   public void initialize( int width, int height, int parentWidth, int parentHeight ) {
      super.initialize( width, height, parentWidth, parentHeight );
   }

   @Override
   public boolean willChangeBounds() {
      return true;
   }

   public void setAdjacentView( View adjacentView ) {
      mAdjacentView = adjacentView;
   }

   public void setAdjacentHeightIncrement( int adjacentHeightIncrement ) {
      mAdjacentHeightIncrement = adjacentHeightIncrement;
   }
}
