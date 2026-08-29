package com.zhengwenjuan.timetable;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "WidgetSync")
public class WidgetSyncPlugin extends Plugin {
    @PluginMethod
    public void refresh(PluginCall call) {
        Context ctx = getContext();
        AppWidgetManager mgr = AppWidgetManager.getInstance(ctx);
        for (int id : mgr.getAppWidgetIds(new ComponentName(ctx, TodayWidgetProvider.class)))
            mgr.updateAppWidget(id, TodayWidgetProvider.build(ctx));
        for (int id : mgr.getAppWidgetIds(new ComponentName(ctx, DateWidgetProvider.class)))
            mgr.updateAppWidget(id, DateWidgetProvider.build(ctx));
        call.resolve();
    }
}
