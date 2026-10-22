package com.dreamstream.app

import android.app.Activity
import android.os.Bundle
import android.content.Context
import android.graphics.Color
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*

class MainActivity : Activity() {
 private val prefs by lazy { getSharedPreferences("dreamstream", Context.MODE_PRIVATE) }
 private val bg=Color.rgb(3,7,13); private val panel=Color.rgb(10,20,34); private val cyan=Color.rgb(0,221,245); private val muted=Color.rgb(139,167,194)
 private lateinit var root: LinearLayout
 private var server=""; private var user=""; private var pass=""; private var auto=true
 private val channels=listOf("BBC One","BBC Two","ITV1","Channel 4","Sky Sports News","TNT Sports 1","BT Sport","ESPN")
 override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); window.statusBarColor=bg; window.navigationBarColor=bg; server=prefs.getString("server","http://vpn-esxtrapro.info:80")?:""; user=prefs.getString("username","")?:""; auto=prefs.getBoolean("auto",true); login() }
 private fun page(title:String, back:Boolean=true): LinearLayout { root=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(dp(20),dp(20),dp(20),dp(20)); setBackgroundColor(bg) }; val sc=ScrollView(this); sc.addView(root); setContentView(sc); if(back) button("‹   $title",false){ home() } else text(title,24,Color.WHITE,true); return root }
 private fun text(s:String,size:Int,color:Int=Color.WHITE,bold:Boolean=false): TextView=TextView(this).apply { text=s; textSize=size.toFloat(); setTextColor(color); if(bold)setTypeface(null,1); setPadding(0,dp(6),0,dp(6)) }
 private fun button(s:String,primary:Boolean=true,action:()->Unit) { val b=Button(this).apply { text=s; setTextColor(if(primary) bg else Color.WHITE); textSize=14f; setBackgroundTintList(android.content.res.ColorStateList.valueOf(if(primary) cyan else panel)); setOnClickListener{action()} }; root.addView(b,lp(10)) }
 private fun field(hint:String,value:String,type:Int=InputType.TYPE_CLASS_TEXT): EditText=EditText(this).apply { this.hint=hint; setText(value); inputType=type; isSingleLine = true; textSize=15f; setTextColor(Color.WHITE); setHintTextColor(muted); setPadding(dp(12),dp(8),dp(12),dp(8)); background=android.graphics.drawable.GradientDrawable().apply { setColor(panel); cornerRadius=dp(10).toFloat(); setStroke(dp(1),Color.rgb(36,67,94)) } }
 private fun lp(top:Int=0)=LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT).apply{topMargin=dp(top)}
 private fun login(){ page("",false); text("▶",48,cyan,true).gravity=Gravity.CENTER; text("DreamStream",28,Color.WHITE,true).gravity=Gravity.CENTER; text("U L T R A   •   YOUR WORLD. STREAMED.",11,cyan).gravity=Gravity.CENTER; root.addView(Space(this),lp(24)); text("Sign in to your media provider",20,Color.WHITE,true)
 val s=field("Puto (Server Address)",server,InputType.TYPE_TEXT_VARIATION_URI); val u=field("Username",user); val p=field("Password",pass,InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD); root.addView(s,lp(10));root.addView(u,lp(10));root.addView(p,lp(10)); val cb=CheckBox(this).apply{text="Remember server & username";setTextColor(muted);isChecked=auto;buttonTintList=android.content.res.ColorStateList.valueOf(cyan)};root.addView(cb,lp(8)); button("CONTINUE"){ server=s.text.toString().trim();user=u.text.toString().trim();pass=p.text.toString();auto=cb.isChecked;prefs.edit().putString("server",server).putString("username",user).putBoolean("auto",auto).apply(); Toast.makeText(this,"Demo mode: provider authentication is not connected",Toast.LENGTH_LONG).show();home() }; root.addView(text("This prototype does not load channels from a provider yet.",12,muted),lp(10)) }
 private fun home(){ page("DreamStream Ultra",false); text("ULTRA STREAMING",11,cyan,true); root.addView(Space(this),lp(8)); val feature=TextView(this).apply{text="FEATURED\nYour entertainment hub\nConnect an authorized playlist to begin";textSize=18f;setTextColor(Color.WHITE);setPadding(dp(16),dp(20),dp(16),dp(20));background=android.graphics.drawable.GradientDrawable().apply{setColor(panel);cornerRadius=dp(14).toFloat()}};root.addView(feature,lp(6)); listOf("Live TV","Movies","Series","Favourites","Settings").forEach { item->button("›   $item",false){ when(item){"Live TV"->live();"Movies"->library("Movies");"Series"->library("Series");"Favourites"->library("Favourites");else->settings()} } } }
 private fun live(){page("Live TV");channels.forEach { ch->button("▶    $ch     ♡",false){ player(ch) } };root.addView(text("Sample channel names only — no stream URLs are bundled.",12,muted),lp(8))}
 private fun library(name:String){page(name);val rows=when(name){"Movies"->listOf("Featured","Action","Adventure","Comedy","Drama");"Series"->listOf("Continue Watching","Popular Series","Recently Added","Documentary");else->listOf("No favourites saved in this prototype")};rows.forEach{button("▣   $it",false){player(it)}};root.addView(text("Real catalogue data, posters and search need a provider/API connection.",12,muted),lp(8))}
 private fun player(name:String){page(name);val box=TextView(this).apply{text="▶\n\nPLAYER PREVIEW\n\nNo stream URL loaded";gravity=Gravity.CENTER;textSize=18f;setTextColor(cyan);background=android.graphics.drawable.GradientDrawable().apply{setColor(Color.BLACK);cornerRadius=dp(14).toFloat()};setPadding(0,dp(70),0,dp(70))};root.addView(box,lp(16));root.addView(text("Media3 playback dependencies can be wired to a valid authorized stream in the next development step.",12,muted),lp(8))}
 private fun settings(){page("Settings");val cb=CheckBox(this).apply{text="Remember server & username";setTextColor(Color.WHITE);isChecked=auto;buttonTintList=android.content.res.ColorStateList.valueOf(cyan);setOnCheckedChangeListener{_,v->auto=v;prefs.edit().putBoolean("auto",v).apply()}};root.addView(cb,lp(8));button("Change server (PIN protected)",false){pin()};root.addView(text("Version 0.2.0 • UI prototype",12,muted),lp(14))}
 private fun pin(){page("Admin PIN");val p=field("Enter PIN", "",InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD);root.addView(p,lp(12));button("UNLOCK SERVER SETTINGS"){if(p.text.toString()=="007")login() else Toast.makeText(this,"Incorrect PIN",Toast.LENGTH_SHORT).show()};root.addView(text("Demo PIN 007 is a basic UI gate, not strong security.",12,muted),lp(8))}
 private fun dp(v:Int)=(v*resources.displayMetrics.density).toInt()
}
