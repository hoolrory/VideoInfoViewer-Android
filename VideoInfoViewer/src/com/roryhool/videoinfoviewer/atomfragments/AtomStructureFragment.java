package com.roryhool.videoinfoviewer.atomfragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.FileTypeBox;
import com.coremedia.iso.boxes.FreeBox;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.UserDataBox;
import com.coremedia.iso.boxes.mdat.MediaDataBox;
import com.roryhool.videoinfoviewer.R;
import com.roryhool.videoinfoviewer.data.Video;
import com.roryhool.videoinfoviewer.views.BoxView;

public class AtomStructureFragment extends Fragment {

   @Override
   public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {

      return inflater.inflate( R.layout.fragment_atom_structure, container, false );
   }

   public void LoadVideo( Video video ) {
      new RetrieveAtomStructureTask().execute( video );
   }

   public class RetrieveAtomStructureTask extends AsyncTask<Video, Void, List<BoxView>> {

      @Override
      protected void onPreExecute() {

      }

      @Override
      protected List<BoxView> doInBackground( Video... videos ) {
         Video video = videos[0];

         return LoadBoxStructure( video );
      }

      @Override
      protected void onPostExecute( List<BoxView> boxViews ) {

         for ( BoxView boxView : boxViews ) {

            LinearLayout layout = (LinearLayout) getActivity().findViewById( R.id.atom_layout );
            layout.addView( boxView );
         }
      }
   }

   private List<BoxView> LoadBoxStructure( Video video ) {
      
      List<BoxView> views = new ArrayList<BoxView>();

      IsoFile isoFile = null;
      try {
         isoFile = new IsoFile( video.FilePath );
      } catch ( IOException e ) {
         e.printStackTrace();
      }

      if ( isoFile == null ) {
         return views;
      }

      Log.d( "This", "Logging boxes" );
      for ( Box box : isoFile.getBoxes() ) {

         BoxView view = BoxView.CreateBoxViewAndChildren( getActivity(), box );

         views.add( view );

         if ( box instanceof FileTypeBox ) {
            FileTypeBox b = (FileTypeBox) box;
            List<String> brands = b.getCompatibleBrands();

            for ( String brand : brands ) {
               Log.d( "this", "has brand " + brand );
            }
            String majorBrand = b.getMajorBrand();
            long minorVersion = b.getMinorVersion();
            String type1 = b.getType();
            byte[] userType = b.getUserType();
            // String userTypeString = Hex.encodeHex( userType );

            Log.d( "This", String.format( "brand %s, minor version %d", majorBrand, minorVersion ) );
         } else if ( box instanceof FreeBox ) {
            FreeBox freeBox = (FreeBox) box;
            // freeBox.getBoxes();
         } else if ( box instanceof MediaDataBox ) {
            MediaDataBox mediaDataBox = (MediaDataBox) box;
            // mediaDataBox.getBoxes();

         } else if ( box instanceof MovieBox ) {
            MovieBox movieBox = (MovieBox) box;
            movieBox.getBoxes();
         } else if ( box instanceof MovieHeaderBox ) {

            MovieHeaderBox movieHeaderBox = (MovieHeaderBox) box;
            double rate = movieHeaderBox.getRate();
            String type = movieHeaderBox.getType();
            int version = movieHeaderBox.getVersion();
            long timeScale = movieHeaderBox.getTimescale();

            int flags = movieHeaderBox.getFlags();
            float volume = movieHeaderBox.getVolume();

            Log.d( "this", String.format( "Rate = %f, type = %s, version = %d, timeScale = %d, flags = %d, volume = %f", rate, type, version, timeScale, flags, volume ) );

         } else if ( box instanceof TrackBox ) {
            TrackBox trackBox = (TrackBox) box;
            trackBox.getBoxes();
         } else if ( box instanceof UserDataBox ) {
            UserDataBox userDataBox = (UserDataBox) box;
            userDataBox.getBoxes();
         }

         Log.d( "This", String.format( "Box - %s with class %s", box.getType(), box.getClass().toString() ) );

      }
      for ( Box box : isoFile.getMovieBox().getBoxes() ) {
         Log.d( "This", String.format( "Movie Box - %s with class %s", box.getType(), box.getClass().toString() ) );

      }

      try {
         isoFile.close();
      } catch ( IOException e ) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      return views;
   }
}
