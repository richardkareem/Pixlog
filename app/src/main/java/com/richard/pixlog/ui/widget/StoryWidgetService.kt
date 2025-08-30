package com.richard.pixlog.ui.widget

import android.content.Intent
import android.widget.RemoteViewsService

class StoryWidgetService : RemoteViewsService() {
    
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        // Log.d("StoryWidgetService", "onGetViewFactory called")
        // return try {
        //     // Get repository from dependency injection
        //     val repository = Injection.provideRepository(this)
        //     Log.d("StoryWidgetService", "Repository created successfully")
        //     StoryWidgetItemFactory(this.applicationContext, intent, repository)
        // } catch (e: Exception) {
        //     Log.e("StoryWidgetService", "Error creating factory or getting repository", e)
        //     // Return a factory with dummy data if there's any error
        //     StoryWidgetItemFactory(this.applicationContext, intent, null)
        // }
        return StoryWidgetItemFactory(this.applicationContext)
    }
}