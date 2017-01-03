package com.example.enclaveit.app_meida.ui.activities;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.enclaveit.app_meida.R;
import com.example.enclaveit.app_meida.lib.ConfiguraAlert;
import com.example.enclaveit.app_meida.models.bean.Song;
import com.example.enclaveit.app_meida.ui.adapters.AdapterSong;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.List;

public class HomeAcitivity extends AppCompatActivity{

    private Toolbar toolbar;

    private Context context = this;

    private ListView listSong;
    private List<Song> arrSong;
    private AdapterSong adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_activity);
        this.setOnItemClickListenerListView();
    }
    private void setOnItemClickListenerListView(){
        // Declare Widget Android
        initComponents();
        // Get data song
        arrSong = this.getDataSongFromMemoryDevice();
        adapter = new AdapterSong(this,arrSong);
        listSong.setAdapter(adapter);
        // Definition event and send data into screen acitivty
        listSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Song song = (Song) adapterView.getItemAtPosition(position);
                /**
                 * @author: Lorence
                 * set data to send from this activity to that activity
                 */
                Bundle bundle = new Bundle();
                bundle.putString("id",String.valueOf(song.getId()));
                bundle.putString("title",String.valueOf(song.getTitle()));
                bundle.putString("album",String.valueOf(song.getTitle()));
                bundle.putString("artist",String.valueOf(song.getArtist()));
                bundle.putString("duration",String.valueOf(song.getDuration()));
                bundle.putString("path",String.valueOf(song.getPath()));
                bundle.putString("album_id",String.valueOf(song.getAlbum_id()));
                /**
                 * @author Lorence
                 * call startActivity to send intent
                 */
                Intent intent = new Intent(HomeAcitivity.this,PlayActivity.class);
                intent.putExtra("SONG",bundle);
                startActivity(intent);
            }
        });
    }

    private List<Song> getDataSongFromMemoryDevice(){
        arrSong = new ArrayList<Song>();
        Uri mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[] { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID};
        Cursor mediaCursor = this.getContentResolver().query(mediaContentUri, projection, null, null, null);
        int index = 0;
        while (index < mediaCursor.getCount()) {
            mediaCursor.moveToPosition(index);
            String id = mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String title = mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String album = mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            String artist = mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            long duration = mediaCursor.getLong(mediaCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            long album_id = mediaCursor.getLong(mediaCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            Bitmap bitmap = getAlbumart(album_id);
            String path = mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            arrSong.add(new Song(id,title,album,artist, (int) duration,String.valueOf(bitmap),path,album_id));
            index++;
        }
        return arrSong;
    }

    public Bitmap getAlbumart(Long album_id)
    {
        Bitmap bm = null;
        try
        {
            final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
            ParcelFileDescriptor pfd = this.getContentResolver().openFileDescriptor(uri, "r");
            if (pfd != null)
            {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (Exception e) {
        }
        return bm;
    }

    /**
     * Toolbar Android
     */
    private void initComponents() {
        /**
         * Declare Toolbar Android
         */
        toolbar = (Toolbar)this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.toolbar_ic_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ConfiguraAlert.onCreateDialog((Activity)context,"How to way Manage fragment layout by screen size","fsdf",context).show();
            }
        });

        /**
         * Declare ListView
         */
        listSong = (ListView)this.findViewById(R.id.listSong);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar,menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(HomeAcitivity.this, "Permission denied on this device!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
