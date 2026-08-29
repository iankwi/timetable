package com.zhengwenjuan.timetable;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import org.json.JSONArray;
import org.json.JSONObject;

public class TodayWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context ctx, AppWidgetManager mgr, int[] ids) {
        for (int id : ids) mgr.updateAppWidget(id, build(ctx));
    }
    @Override
    public void onEnabled(Context ctx) { }

    static RemoteViews build(Context ctx) {
        RemoteViews v = new RemoteViews(ctx.getPackageName(), R.layout.widget_today);
        SharedPreferences sp = ctx.getSharedPreferences("CapacitorStorage", Context.MODE_PRIVATE);
        String raw = sp.getString("widget_week", null);
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.CHINA)
                .format(new java.util.Date());
        StringBuilder sb = new StringBuilder();
        String sub = "";
        try {
            JSONObject o = new JSONObject(raw);
            JSONArray days = o.getJSONArray("days");
            sub = o.optString("teacher", "");
            boolean found = false;
            for (int i = 0; i < days.length(); i++) {
                JSONObject d = days.getJSONObject(i);
                if (!d.getString("date").equals(today)) continue;
                found = true;
                JSONArray items = d.getJSONArray("items");
                if (items.length() == 0) sb.append("今天没有课");
                for (int j = 0; j < items.length(); j++) {
                    JSONObject it = items.getJSONObject(j);
                    sb.append(it.getString("time")).append("  ").append(it.getString("cls")).append("\n");
                }
                break;
            }
            if (!found) sb.append("今天没有课");
        } catch (Exception e) {
            sb.append("打开App导入课表");
        }
        v.setTextViewText(R.id.w_title, "今日课程");
        v.setTextViewText(R.id.w_sub, sub);
        v.setTextViewText(R.id.w_list, sb.toString().trim());
        return v;
    }
}
