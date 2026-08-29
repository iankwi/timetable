package com.zhengwenjuan.timetable;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import org.json.JSONArray;
import org.json.JSONObject;

/** 方案C：左侧大号日期 + 星期，右侧课程列表（最多3条） */
public class DateWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context ctx, AppWidgetManager mgr, int[] ids) {
        for (int id : ids) mgr.updateAppWidget(id, build(ctx));
    }

    static RemoteViews build(Context ctx) {
        RemoteViews v = new RemoteViews(ctx.getPackageName(), R.layout.widget_date);
        SharedPreferences sp = ctx.getSharedPreferences("CapacitorStorage", Context.MODE_PRIVATE);
        String raw = sp.getString("widget_week", null);
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.CHINA)
                .format(new java.util.Date());
        Intent intent = new Intent(ctx, MainActivity.class);
        v.setOnClickPendingIntent(R.id.date_widget_root,
            PendingIntent.getActivity(ctx, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
        try {
            JSONObject o = new JSONObject(raw);
            JSONObject d = o.getJSONObject("today").getString("date").equals(today)
                ? o.getJSONObject("today") : o.getJSONObject("tomorrow");
            if (!d.getString("date").equals(today)) {
                v.setTextViewText(R.id.d_list, "打开App刷新");
                return v;
            }
            java.text.SimpleDateFormat wf = new java.text.SimpleDateFormat("E", java.util.Locale.CHINA);
            java.util.Calendar cal = java.util.Calendar.getInstance();
            v.setTextViewText(R.id.d_num, String.valueOf(cal.get(java.util.Calendar.DAY_OF_MONTH)));
            v.setTextViewText(R.id.d_wk, wf.format(cal.getTime()));
            JSONArray lessons = d.getJSONArray("lessons");
            StringBuilder sb = new StringBuilder();
            int n = Math.min(lessons.length(), 3);
            for (int i = 0; i < n; i++) {
                JSONObject it = lessons.getJSONObject(i);
                sb.append(it.getString("time")).append(" ").append(it.getString("cls"));
                if (it.optBoolean("now")) sb.append(" ←");
                if (i < n - 1) sb.append("\n");
            }
            if (lessons.length() == 0) sb.append("没有课");
            v.setTextViewText(R.id.d_list, sb.toString());
        } catch (Exception e) {
            v.setTextViewText(R.id.d_num, "–");
            v.setTextViewText(R.id.d_list, "打开App\n导入课表");
        }
        return v;
    }
}
