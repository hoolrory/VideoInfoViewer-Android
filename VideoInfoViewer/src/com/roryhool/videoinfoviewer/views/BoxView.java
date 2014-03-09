package com.roryhool.videoinfoviewer.views;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.coremedia.iso.boxes.Box;
import com.googlecode.mp4parser.AbstractContainerBox;
import com.roryhool.videoinfoviewer.R;
import com.roryhool.videoinfoviewer.utils.AtomHelper;

public class BoxView extends FrameLayout {

   public static BoxView CreateBoxViewAndChildren( Context context, Box box ) {
      BoxView boxView = new BoxView( context, box );

      if ( box instanceof AbstractContainerBox ) {
         Log.d( "this", "box is instance of AbstractContainerBox" );

         AbstractContainerBox containerBox = (AbstractContainerBox) box;
         for ( Box childBox : containerBox.getBoxes() ) {
            Log.d( "this", "adding child " + childBox.getType() );
            BoxView childView = BoxView.CreateBoxViewAndChildren( context, childBox );
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

   public BoxView( Context context, Box box ) {
      super( context );

      addView( View.inflate( context, R.layout.box, null ) );

      mTypeView = (TextView) findViewById( R.id.box_type );
      mDescriptionView = (TextView) findViewById( R.id.box_description );
      mExpandButton = (ToggleButton) findViewById( R.id.box_expand_button );
      mBoxIcon = (ImageView) findViewById( R.id.box_icon );
      mChildBoxes = (LinearLayout) findViewById( R.id.child_boxes );

      mExpandButton.setOnCheckedChangeListener( mExpandClickListener );
      loadBox( box );
   }

   private void loadBox( Box box ) {
      mBox = box;
      mTypeView.setText( box.getType() );
      mDescriptionView.setText( AtomHelper.GetNameForType( box.getType() ) );
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

}
