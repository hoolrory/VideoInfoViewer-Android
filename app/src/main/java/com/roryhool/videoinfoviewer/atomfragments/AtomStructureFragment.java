/**
 * Copyright (c) 2014 Rory Hool
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package com.roryhool.videoinfoviewer.atomfragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.googlecode.mp4parser.AbstractContainerBox;
import com.roryhool.videoinfoviewer.AtomActivity;
import com.roryhool.videoinfoviewer.Extras;
import com.roryhool.videoinfoviewer.R;
import com.roryhool.videoinfoviewer.data.Video;
import com.roryhool.videoinfoviewer.utils.AtomHelper;
import com.roryhool.videoinfoviewer.utils.IsoFileCache;
import com.roryhool.videoinfoviewer.utils.Logg;

import rx.Observable;
import rx.android.app.AppObservable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class AtomStructureFragment extends Fragment {

   protected Video   mVideo;
   protected IsoFile mIsoFile;

   protected RecyclerView mRecycler;
   protected ProgressBar  mProgress;

   protected AtomAdapter mAdapter;

   protected List<Atom> mAtoms = new ArrayList<>();

   protected CompositeSubscription mSubscription = new CompositeSubscription();

   @Override
   public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
      View v = inflater.inflate( R.layout.fragment_atom_structure, container, false );
      mRecycler = (RecyclerView) v.findViewById( R.id.recycler );
      mProgress = (ProgressBar) v.findViewById( R.id.progress );
      mIsoFile = mVideo.getIsoFile();

      mAdapter = new AtomAdapter();
      mRecycler.setLayoutManager( new LinearLayoutManager( getActivity(), LinearLayoutManager.VERTICAL, false ) );
      mRecycler.setAdapter( mAdapter );

      mSubscription.add(
              AppObservable.bindFragment( this, Observable.from( mIsoFile.getBoxes() ) )
                           .subscribe(
                                   new Action1<Box>() {

                                      @Override
                                      public void call( Box box ) {
                                         Activity activity = getActivity();
                                         if ( activity != null ) {
                                            AtomHelper.logEventsForBox( activity, box );
                                         }
                                         Atom atom = new Atom( box, 0 );
                                         mAtoms.add( atom );
                                         atom.addChildren( mAtoms );
                                      }

                                   }, new Action1<Throwable>() {
                                      @Override
                                      public void call( Throwable throwable ) {
                                         throwable.printStackTrace();
                                      }
                                   }, new Action0() {
                                      @Override
                                      public void call() {
                                         mAdapter.setAtoms( mAtoms );
                                         mProgress.setVisibility( View.GONE );
                                      }
                                   } )
      );

      return v;
   }

   public void setVideo( Video video ) {
      mVideo = video;
   }

   public static class Atom {

      protected Box    mBox;
      protected String mName;
      protected int    mDepth;

      protected List<Atom> mChildAtoms = new ArrayList<>();

      protected boolean mExpanded = true;

      public Atom( Box box, int depth ) {
         mBox = box;
         mDepth = depth;
      }

      public void addChildren( List<Atom> atoms ) {
         if ( mBox instanceof AbstractContainerBox ) {
            Log.d( "this", "box is instance of AbstractContainerBox" );

            AbstractContainerBox containerBox = (AbstractContainerBox) mBox;
            for ( Box childBox : containerBox.getBoxes() ) {
               Atom childAtom = new Atom( childBox, mDepth + 1 );
               atoms.add( childAtom );
               childAtom.addChildren( atoms );
               mChildAtoms.add( childAtom );
            }
         }
      }

      public void toggleExpansion() {
         mExpanded = !mExpanded;
      }

      public int getVisibleChildCount() {
         int visibleChildCount = 0;
         if ( mExpanded ) {
            for ( Atom childAtom : mChildAtoms ) {
               visibleChildCount += 1;
               visibleChildCount += childAtom.getVisibleChildCount();
            }
         }

         return visibleChildCount;
      }

      public String getType() {
         return mBox.getType();
      }

      public String getName() {
         if ( mName == null ) {
            mName = AtomHelper.getNameForType( mBox.getType() );
         }

         return mName;
      }

      protected boolean isExpanded() {
         return mExpanded;
      }

      protected int getDepth() {
         return mDepth;
      }

      protected Box getBox() {
         return mBox;
      }

      protected int getChildCount() {
         return mChildAtoms.size();
      }
   }

   public class AtomViewHolder extends ViewHolder implements OnClickListener, OnCheckedChangeListener {

      protected AtomAdapter mAdapter;

      protected View mView;

      protected Atom mAtom;

      public AtomViewHolder( View view, AtomAdapter adapter ) {
         super( view );

         mView = view;
         mAdapter = adapter;

         ToggleButton expandButton = (ToggleButton) mView.findViewById( R.id.box_expand_button );
         expandButton.setOnCheckedChangeListener( this );

         ImageButton infoButton = (ImageButton) mView.findViewById( R.id.box_info_button );
         infoButton.setOnClickListener( this );
      }

      public void bind( Atom atom ) {
         mAtom = atom;

         TextView typeView = (TextView) mView.findViewById( R.id.box_type );
         typeView.setText( mAtom.getType() );

         RelativeLayout root = (RelativeLayout) mView.findViewById( R.id.root );
         root.setPadding( 10 * mAtom.getDepth(), 0, 0 ,0 );

         TextView descriptionView = (TextView) mView.findViewById( R.id.box_description );
         String name = mAtom.getName();
         descriptionView.setText( name );

         ToggleButton expandButton = (ToggleButton) mView.findViewById( R.id.box_expand_button );
         ImageView boxIcon = (ImageView) mView.findViewById( R.id.box_icon );

         expandButton.setClickable( atom.getChildCount() == 0 ? false : true );
         boxIcon.setVisibility( atom.getChildCount() == 0 ? View.INVISIBLE : View.VISIBLE );
      }

      @Override
      public void onClick( View v ) {
         AtomActivity activity = (AtomActivity) getActivity();
         if ( activity != null ) {
            Intent intent = new Intent( activity, AtomActivity.class );
            intent.putExtra( Extras.EXTRA_BOX_ID, IsoFileCache.Instance().cacheBox( mAtom.getBox() ) );
            startActivity( intent );
         }
      }

      @Override
      public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {
         int from = isChecked ? -90 : 0;
         int to = isChecked ? 0 : -90;

         ImageView boxIcon = (ImageView) mView.findViewById( R.id.box_icon );
         RotateAnimation animation = new RotateAnimation( from, to, boxIcon.getWidth() / 2, boxIcon.getHeight() / 2 );
         animation.setDuration( 600 );
         animation.setFillAfter( true );
         boxIcon.startAnimation( animation );
      }
   }

   public class AtomAdapter extends RecyclerView.Adapter<AtomViewHolder> {

      protected List<Atom> mAtoms = new ArrayList<>();

      public void setAtoms( List<Atom> atoms ) {
         mAtoms = atoms;
         notifyDataSetChanged();
      }

      @Override
      public AtomViewHolder onCreateViewHolder( ViewGroup viewGroup, int position ) {
         LayoutInflater inflater = LayoutInflater.from( viewGroup.getContext() );
         View root = inflater.inflate( R.layout.atom, viewGroup, false );
         return new AtomViewHolder( root, this );
      }

      @Override
      public void onBindViewHolder( AtomViewHolder atomViewHolder, int position ) {
         atomViewHolder.bind( mAtoms.get( position ) );
      }

      @Override
      public int getItemCount() {
         return mAtoms.size();
      }
   }
}
