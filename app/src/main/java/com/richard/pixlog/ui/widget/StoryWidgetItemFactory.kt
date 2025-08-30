package com.richard.pixlog.ui.widget

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.richard.pixlog.R

class StoryWidgetItemFactory(
    private val context: Context,
) : RemoteViewsService.RemoteViewsFactory {
    
    private var storyItems: List<StoryWidgetItem> = emptyList()
    
    override fun onCreate() {
        storyItems = getDummyData()
    }

    private fun getDummyData(): List<StoryWidgetItem> {
        return listOf(
            StoryWidgetItem("1", "Cerita Pertama", "Ini adalah deskripsi untuk cerita pertama yang menarik.", null),
            StoryWidgetItem("2", "Petualangan di Hutan", "Sebuah kisah tentang petualangan seru di tengah hutan belantara.", null),
            StoryWidgetItem("3", "Misteri Kota Tua", "Mengungkap misteri yang tersembunyi di balik bangunan tua.", null),
            StoryWidgetItem("4", "Resep Nenek", "Resep rahasia turun-temurun dari nenek.", null),
            StoryWidgetItem("5", "Cerita 5", "Resep rahasia turun-temurun dari nenek.", null),
            StoryWidgetItem("6", "Cerita 6", "Resep rahasia turun-temurun dari nenek.", null),
            StoryWidgetItem("7", "Cerita 7", "Resep rahasia turun-temurun dari nenek.", null),
            StoryWidgetItem("8", "Cerita 8", "Resep rahasia turun-temurun dari nenek.", null),
            StoryWidgetItem("9", "Cerita 9", "Resep rahasia turun-temurun dari nenek.", null),
            StoryWidgetItem("10", "Cerita 10", "Resep rahasia turun-temurun dari nenek.", null),

        )
    }

    override fun onDataSetChanged() {
        storyItems = getDummyData()
    }

    override fun onDestroy() {
        storyItems = emptyList()
    }

    override fun getCount(): Int {
        return storyItems.size
    }

    override fun getViewAt(position: Int): RemoteViews? {
        if (position == AdapterView.INVALID_POSITION || position >= storyItems.size) {
            return null
        }

        val item = storyItems[position]
        val views = RemoteViews(context.packageName, R.layout.widget_story_item)

        views.setTextViewText(R.id.widget_item_title, item.title)
        views.setTextViewText(R.id.widget_item_description, item.description)
        views.setImageViewResource(R.id.widget_item_image, R.mipmap.ic_launcher)

        val extras = Bundle()
        extras.putString(StoryWidget.EXTRA_ITEM_ID, item.id)
        extras.putString(StoryWidget.EXTRA_ITEM_TITLE, item.title)

        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        views.setOnClickFillInIntent(R.id.widget_item_image, fillInIntent)

        return views
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return storyItems[position].id.hashCode().toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }
}