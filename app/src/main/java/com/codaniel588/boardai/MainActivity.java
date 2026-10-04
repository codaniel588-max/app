package com.codaniel588.boardai;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.*;
import android.text.InputType;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout chat, filters;
    EditText input;
    String filter = "הכול";
    final int PAD = 16;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.parseColor("#F5F7FB"));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        build();
    }

    void build() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#F5F7FB"));
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(20), dp(18), dp(20), dp(12));
        header.setBackground(round("#FFFFFF",22));
        TextView title = text("BoardAI", 30, true, "#162033");
        TextView sub = text("בינה מקומית למשחקי קופסה", 14, false, "#68758A");
        header.addView(title);
        header.addView(sub, lp(-1,-2,0,3,0,0));
        TextView offline = text("●  עובד בלי אינטרנט", 12, true, "#3929C7");
        header.addView(offline, lp(-1,-2,0,10,0,0));
        root.addView(header, lp(-1,-2,0,0,0,8));

        filters = new LinearLayout(this);
        filters.setPadding(dp(14),0,dp(14),dp(8));
        filters.setGravity(Gravity.CENTER_VERTICAL);
        addFilter("הכול"); addFilter("קטאן"); addFilter("מונופול"); addFilter("פוקר");
        root.addView(filters, lp(-1,-2));

        ScrollView sc = new ScrollView(this);
        chat = new LinearLayout(this);
        chat.setOrientation(LinearLayout.VERTICAL);
        chat.setPadding(dp(16), dp(8), dp(16), dp(18));
        chat.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        sc.addView(chat, lp(-1,-1));
        root.addView(sc, new LinearLayout.LayoutParams(-1,0,1));

        LinearLayout composer = new LinearLayout(this);
        composer.setPadding(dp(12),dp(10),dp(12),dp(12));
        composer.setGravity(Gravity.CENTER_VERTICAL);
        composer.setBackground(round("#FFFFFF",22));

        input = new EditText(this);
        input.setSingleLine(false);
        input.setMinLines(1);
        input.setMaxLines(4);
        input.setHint("שאל שאלה על חוק או מצב במשחק…");
        input.setTextSize(15);
        input.setTextColor(Color.parseColor("#162033"));
        input.setHintTextColor(Color.parseColor("#8A94A6"));
        input.setPadding(dp(14),dp(9),dp(14),dp(9));
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
        input.setBackground(round("#F5F7FB",18));
        composer.addView(input, new LinearLayout.LayoutParams(0,-2,1));

        TextView send = text("שלח", 15, true, "#FFFFFF");
        send.setGravity(Gravity.CENTER);
        send.setBackground(round("#5B4BFF",18));
        send.setPadding(dp(18),dp(12),dp(18),dp(12));
        composer.addView(send, lp(-2,-2,10,0,0,0));
        send.setOnClickListener(v -> ask());
        input.setOnEditorActionListener((v, actionId, event) -> { ask(); return true; });

        root.addView(composer, lp(-1,-2));
        setContentView(root);
        showBot("שלום! אני BoardAI 👋\nאני עובד בלי אינטרנט ומכיר חוקים ומצבי משחק בקטאן, מונופול ופוקר.\n\nלדוגמה: "יצא 7 בקטאן — מה עושים?" או "במונופול לא קנו את הנכס — מה קורה?"");
    }

    void ask() {
        String q=input.getText().toString().trim();
        if(q.isEmpty()) return;
        showUser(q);
        String a=KnowledgeBase.answer(q,filter);
        showBot(a);
        input.setText("");
        input.clearFocus();
        ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(input.getWindowToken(),0);
    }

    void addFilter(String label) {
        TextView b=text(label,13,true, label.equals(filter)?"#FFFFFF":"#162033");
        b.setGravity(Gravity.CENTER);
        b.setPadding(dp(16),dp(9),dp(16),dp(9));
        b.setBackground(round(label.equals(filter)?"#5B4BFF":"#FFFFFF",50));
        b.setOnClickListener(v -> {
            filter=label;
            for(int i=0;i<filters.getChildCount();i++){
                TextView x=(TextView)filters.getChildAt(i);
                boolean on=x.getText().toString().equals(filter);
                x.setTextColor(Color.parseColor(on?"#FFFFFF":"#162033"));
                x.setBackground(round(on?"#5B4BFF":"#FFFFFF",50));
            }
        });
        filters.addView(b, lp(-2,-2,6,0,0,0));
    }

    void showUser(String s){ bubble("אתה",s,true); }
    void showBot(String s){ bubble("BoardAI",s,false); }

    void bubble(String who,String s,boolean user){
        LinearLayout box=new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(16),dp(12),dp(16),dp(13));
        box.setBackground(round(user?"#EDEBFF":"#FFFFFF",20));
        TextView w=text(who,12,true,user?"#5B4BFF":"#68758A");
        TextView m=text(s,15,false,"#162033");
        m.setGravity(Gravity.RIGHT);
        m.setLineSpacing(0,1.08f);
        box.addView(w);
        box.addView(m,lp(-1,-2,0,5,0,0));
        LinearLayout.LayoutParams p=lp(-1,-2,0,0,0,10);
        chat.addView(box,p);
        chat.post(() -> ((ScrollView)chat.getParent()).fullScroll(View.FOCUS_DOWN));
    }

    TextView text(String s,int size,boolean bold,String color){
        TextView t=new TextView(this);
        t.setText(s); t.setTextSize(size); t.setTextColor(Color.parseColor(color));
        if(bold)t.setTypeface(Typeface.create("sans",Typeface.BOLD));
        t.setTextDirection(View.TEXT_DIRECTION_ANY_RTL);
        return t;
    }
    GradientDrawable round(String color,int r){
        GradientDrawable g=new GradientDrawable();
        g.setColor(Color.parseColor(color)); g.setCornerRadius(dp(r)); return g;
    }
    LinearLayout.LayoutParams lp(int w,int h){return new LinearLayout.LayoutParams(w,h);}
    LinearLayout.LayoutParams lp(int w,int h,int l,int t,int r,int b){
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(w,h);
        p.setMargins(dp(l),dp(t),dp(r),dp(b)); return p;
    }
    int dp(int n){return Math.round(n*getResources().getDisplayMetrics().density);}
}
