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

package com.roryhool.videoinfoviewer;

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
import com.roryhool.videoinfoviewer.analytics.Analytics;
import com.roryhool.videoinfoviewer.atomfragments.AtomStructureFragment.Atom;
import com.roryhool.videoinfoviewer.utils.AtomHelper;

public class AtomView extends FrameLayout {

   Atom mAtom;

   TextView mTypeView;

   TextView mDescriptionView;

   ToggleButton mExpandButton;

   ImageView mBoxIcon;

   ImageButton mInfoButton;

   public AtomView( Context context ) {
      super( context );

      View view = View.inflate( context, R.layout.atom, null );
      addView( view );

      mTypeView = (TextView) findViewById( R.id.box_type );
      mDescriptionView = (TextView) findViewById( R.id.box_description );
      mExpandButton = (ToggleButton) findViewById( R.id.box_expand_button );
      mBoxIcon = (ImageView) findViewById( R.id.box_icon );
      mInfoButton = (ImageButton) findViewById( R.id.box_info_button );
   }

   public ToggleButton getExpandButton() {
      return mExpandButton;
   }

   public void loadAtom( Atom atom ) {
      mAtom = atom;
      mTypeView.setText( mAtom.getType() );

      String name = mAtom.getName();
      mDescriptionView.setText( name );
   }

   public void hideExpandButton() {
      mExpandButton.setClickable( false );
      mBoxIcon.setVisibility( View.INVISIBLE );
   }

   public void rotateExpandButton( boolean collapsed ) {
      int from = collapsed ? -90 : 0;
      int to = collapsed ? 0 : -90;

      RotateAnimation animation = new RotateAnimation( from, to, mBoxIcon.getWidth() / 2, mBoxIcon.getHeight() / 2 );
      animation.setDuration( 600 );
      animation.setFillAfter( true );
      mBoxIcon.startAnimation( animation );
   }

}
