package com.tahar.iptv;
import android.os.Bundle; import android.widget.*; import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem; import androidx.media3.exoplayer.ExoPlayer; import androidx.media3.ui.PlayerView;
import java.io.*; import java.util.*;
public class MainActivity extends AppCompatActivity {
    PlayerView playerView; ExoPlayer player; ListView listView;
    List<String> names = new ArrayList<>(); List<String> urls = new ArrayList<>();
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_main);
        playerView = findViewById(R.id.player_view); listView = findViewById(R.id.channel_list);
        player = new ExoPlayer.Builder(this).build(); playerView.setPlayer(player);
        loadChannels();
        listView.setOnItemClickListener((a,b,pos,c)-> play(urls.get(pos)));
    }
    void loadChannels(){
        try{
            InputStream is = getAssets().open("channels.m3u");
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String line, name=""; while((line=r.readLine())!=null){
                if(line.startsWith("#EXTINF")){ int idx=line.lastIndexOf(','); if(idx!=-1) name=line.substring(idx+1); }
                else if(line.startsWith("http")){ names.add(name.isEmpty()?"Channel":name); urls.add(line.trim()); name=""; }
            }
        }catch(Exception e){ names.add("Test Channel"); urls.add("https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"); }
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names));
        if(!urls.isEmpty()) play(urls.get(0));
    }
    void play(String url){ player.setMediaItem(MediaItem.fromUri(url)); player.prepare(); player.setPlayWhenReady(true); }
    @Override protected void onDestroy(){ super.onDestroy(); player.release(); }
}
