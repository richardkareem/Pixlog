@file:Suppress("DEPRECATION")

package com.richard.pixlog.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.richard.pixlog.R
import com.richard.pixlog.ui.screen.detailStory.DetailStoryActivity

class StoryWidget : AppWidgetProvider() {
    
    companion object {
        const val EXTRA_ITEM_ID = "extra_item_id"
        const val EXTRA_ITEM_TITLE = "extra_item_title"
    }
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        if (intent.hasExtra(EXTRA_ITEM_ID)) {
            val itemId = intent.getStringExtra(EXTRA_ITEM_ID)
            val itemTitle = intent.getStringExtra(EXTRA_ITEM_TITLE)
            
            val detailIntent = Intent(context, DetailStoryActivity::class.java).apply {
                putExtra(EXTRA_ITEM_ID, itemId)
                putExtra(EXTRA_ITEM_TITLE, itemTitle)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(detailIntent)
        }
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = RemoteViews(context.packageName, R.layout.story_widget)
    
    val intent = Intent(context, StoryWidgetService::class.java)
    views.setRemoteAdapter(R.id.rvStory, intent)
    
    val clickIntent = Intent(context, StoryWidget::class.java).apply {
        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    }
    
    val clickPendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        clickIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    
    views.setPendingIntentTemplate(R.id.rvStory, clickPendingIntent)
    
    appWidgetManager.updateAppWidget(appWidgetId, views)
    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.rvStory)
}