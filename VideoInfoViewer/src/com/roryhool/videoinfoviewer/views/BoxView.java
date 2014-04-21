/**
   Copyright (c) 2014 Rory Hool
   
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

package com.roryhool.videoinfoviewer.views;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.coremedia.iso.boxes.Box;
import com.googlecode.mp4parser.AbstractContainerBox;
import com.roryhool.videoinfoviewer.R;
import com.roryhool.videoinfoviewer.analytics.Analytics;
import com.roryhool.videoinfoviewer.utils.AtomHelper;

public class BoxView extends FrameLayout {

   public interface BoxViewOnClickListener {
      public void onClickInfo( Box box );
   }

   public static BoxView CreateBoxViewAndChildren( Context context, BoxViewOnClickListener listener, Box box ) {
      BoxView boxView = new BoxView( context );
      boxView.loadBox( box );
      boxView.setBoxViewOnClickListener( listener );

      if ( box instanceof AbstractContainerBox ) {
         Log.d( "this", "box is instance of AbstractContainerBox" );

         AbstractContainerBox containerBox = (AbstractContainerBox) box;
         for ( Box childBox : containerBox.getBoxes() ) {
            Log.d( "this", "adding child " + childBox.getType() + " - " + childBox.getClass().getName() );
            BoxView childView = BoxView.CreateBoxViewAndChildren( context, listener, childBox );
            // childView.setVisibility( View.GONE );
            boxView.addChildBoxView( childView );
         }
      } else {
         boxView.hideExpandButton();
      }
      return boxView;
   }

   Box mBox;

   RelativeLayout mBoxLayout;

   TextView mTypeView;

   TextView mDescriptionView;

   ToggleButton mExpandButton;

   ImageView mBoxIcon;

   LinearLayout mChildBoxes;

   BoxViewOnClickListener mListener;

   ImageButton mInfoButton;

   public BoxView( Context context ) {
      super( context );

      addView( View.inflate( context, R.layout.box, null ) );

      mTypeView = (TextView) findViewById( R.id.box_type );
      mDescriptionView = (TextView) findViewById( R.id.box_description );
      mExpandButton = (ToggleButton) findViewById( R.id.box_expand_button );
      mBoxIcon = (ImageView) findViewById( R.id.box_icon );
      mChildBoxes = (LinearLayout) findViewById( R.id.child_boxes );
      mInfoButton = (ImageButton) findViewById( R.id.box_info_button );

      mExpandButton.setOnCheckedChangeListener( mExpandClickListener );
      mInfoButton.setOnClickListener( mInfoButtonClickListener );
   }

   public void loadBox( Box box ) {
      mBox = box;
      mTypeView.setText( mBox.getType() );

      String name = AtomHelper.GetNameForType( mBox.getType() );

      if ( name == null ) {
         Analytics.Instance( getContext() ).LogEvent( "Video Info", "Failed to get name for type ", mBox.getType() );
      }
      mDescriptionView.setText( name );
   }

   private void setBoxViewOnClickListener( BoxViewOnClickListener listener ) {
      mListener = listener;
   }

   private void addChildBoxView( BoxView childView ) {
      mChildBoxes.addView( childView );
   }

   private void hideExpandButton() {
      mExpandButton.setClickable( false );
      mBoxIcon.setVisibility( View.INVISIBLE );
   }
   
   private OnCheckedChangeListener mExpandClickListener = new OnCheckedChangeListener() {

      @Override
      public void onCheckedChanged( CompoundButton view, boolean checked ) {

         int from = checked ? 0 : 90;
         int to = checked ? 90 : 0;

         RotateAnimation animation = new RotateAnimation( from, to, mBoxIcon.getWidth() / 2, mBoxIcon.getHeight() / 2 );
         animation.setDuration( 600 );
         animation.setFillAfter( true );
         mBoxIcon.startAnimation( animation );

         int visibility = checked ? View.VISIBLE : View.GONE;
         for ( int i = 0; i < mChildBoxes.getChildCount(); i++ ) {
            View childView = mChildBoxes.getChildAt( i );
            childView.setVisibility( visibility );
         }
      }
   };

   OnClickListener mInfoButtonClickListener = new OnClickListener() {

      @Override
      public void onClick( View view ) {
         if ( mListener != null ) {
            mListener.onClickInfo( mBox );
         }
      }
   };

}
