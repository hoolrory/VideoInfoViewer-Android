package com.roryhool.videoinfoviewer.utils;

import android.util.SparseArray;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;

public class IsoFileCache {

   private static IsoFileCache sInstance = null;

   public static IsoFileCache Instance() {
      if ( sInstance == null ) {
         sInstance = new IsoFileCache();
      }

      return sInstance;
   }

   private SparseArray<IsoFile> mIsoFiles = new SparseArray<IsoFile>();

   private SparseArray<Box> mBoxes = new SparseArray<Box>();

   public int cacheIsoFile( IsoFile isoFile ) {
      int index = mIsoFiles.size();
      mIsoFiles.put( index, isoFile );
      return index;
   }

   public int cacheBox( Box box ) {
      int index = mBoxes.size();
      mBoxes.put( index, box );
      return index;
   }

   public IsoFile getIsoFile( int isoFileId ) {
      return mIsoFiles.get( isoFileId );
   }

   public Box getBox( int boxId ) {
      return mBoxes.get( boxId );
   }
}
