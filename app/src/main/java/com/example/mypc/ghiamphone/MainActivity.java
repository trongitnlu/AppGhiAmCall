package com.example.mypc.ghiamphone;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mypc.ghiamphone.adapter.ListMusicAdapter;
import com.example.mypc.ghiamphone.model.Music;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    List<Music> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    public void init(){
        listView = (ListView) findViewById(R.id.lisView);
        list = getListMusic();
        ListMusicAdapter listMusicAdapter = new ListMusicAdapter(this, R.layout.row_item, list);
        listView.setAdapter(listMusicAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                intent.putExtra("PATH", list.get(position).getPath());
                startActivity(intent);
            }
        });
    }
    public List<Music> getListMusic() {
        List<Music> list = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().toString() + "/" + PhoneStatReceiver.FORDER;
        File directory = new File(path);
        if(!directory.exists()) directory.mkdir();
        File[] files = directory.listFiles();
        Toast.makeText(this, ""+files.length, Toast.LENGTH_SHORT).show();
        for (int i = 0; i < files.length; i++)
        {
            StringTokenizer tokenizer = new StringTokenizer(files[i].getName(), "-");

            Music music = new Music();
            String status = tokenizer.nextToken();
            String phoneNumber = tokenizer.nextToken().trim();
            String name = getContactName(this, phoneNumber);
            Date date = new Date(files[i].lastModified());
            music.setName(name);
            music.setPhoneNumber(phoneNumber);
            music.setStatus(status);
            music.setPath(files[i].getPath());
            music.setDate(date);
            list.add(music);
        }
        return list;
    }
    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return "Không tên";
        }
        String contactName = "NO NAME";
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }
}
