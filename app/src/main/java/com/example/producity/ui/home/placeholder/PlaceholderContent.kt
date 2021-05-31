package com.example.producity.ui.home.placeholder

import java.util.*

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object PlaceholderContent {

    /**
     * An array of sample (placeholder) items.
     */
    val ITEMS: MutableList<PlaceholderItem> = arrayListOf(
        PlaceholderItem("Title", "My First Activity", ""),
        PlaceholderItem("Start Time", "My First Activity", ""),
        PlaceholderItem("End Time", "My First Activity", ""),
        PlaceholderItem("Description", "My First Activity", "")
    )

    /**
     * A map of sample (placeholder) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, PlaceholderItem> = HashMap()

    private val COUNT = 4

    init {
        // Add some sample items.
        /*
        for (i in 5..COUNT) {
            addItem(createPlaceholderItem(i))
        }

         */
    }

    private fun addItem(item: PlaceholderItem) {
        ITEMS.add(item)
        ITEM_MAP.put(item.id, item)
    }

    private fun createPlaceholderItem(position: Int): PlaceholderItem {
        return PlaceholderItem(position.toString(), "Item " + position, makeDetails(position))
    }

    private fun makeDetails(position: Int): String {
        val builder = StringBuilder()
        builder.append("Details about Item: ").append(position)
        for (i in 0..position - 1) {
            builder.append("\nMore details information here.")
        }
        return builder.toString()
    }

    /**
     * A placeholder item representing a piece of content.
     */
    data class PlaceholderItem(val id: String, var content: String?, var details: String) {
        override fun toString(): String = content.toString()
    }
}