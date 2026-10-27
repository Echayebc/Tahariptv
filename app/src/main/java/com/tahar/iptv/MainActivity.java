package com.tahar.iptv;
import android.os.Bundle; import android.widget.*; import androidx.appcompat.app.AppCompatActivity;
import com.google.android.exoplayer2.*; import com.google.android.exoplayer2.ui.PlayerView;
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
            // حط قائمة القنوات تاعك هنا - هذا التطبيق ما يتصلش بـ GitHub نهائيا
            // تقدر تبدلها برابط M3U مباشر من سيرفرك
            InputStream is = getAssets().open("channels.m3u");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line, name=""; while((line=reader.readLine())!=null){
                if(line.startsWith("#EXTINF")){ int idx=line.lastIndexOf(','); if(idx!=-1) name=line.substring(idx+1); }
                else if(line.startsWith("http")){ names.add(name); urls.add(line.trim()); name=""; }
            }
        }catch(Exception e){
            // قنوات افتراضية اذا ما كاينش ملف
            names.add("Tahar TV Demo"); urls.add("https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8");
        }
        ArrayAdapter<String> ad = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        listView.setAdapter(ad); if(!urls.isEmpty()) play(urls.get(0));
    }
    void play(String url){ player.setMediaItem(MediaItem.fromUri(url)); player.prepare(); player.setPlayWhenReady(true); }
    @Override protected void onDestroy(){ super.onDestroy(); player.release(); }
}
