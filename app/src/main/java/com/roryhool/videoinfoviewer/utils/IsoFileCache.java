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
