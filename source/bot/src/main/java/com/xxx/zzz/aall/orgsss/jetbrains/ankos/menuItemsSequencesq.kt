

@file:Suppress("unused")
package com.xxx.zzz.aall.orgsss.jetbrains.ankos

import android.view.Menu
import android.view.MenuItem
import java.util.ConcurrentModificationException
import java.util.NoSuchElementException

@Deprecated(message = "Use the Android KTX version", replaceWith = ReplaceWith("children", "androidx.core.view.children"))
fun Menu.itemsSequence(): Sequence<MenuItem> = MenuItemsSequence(this)

private class MenuItemsSequence(private val menu: Menu) : Sequence<MenuItem> {
    override fun iterator(): Iterator<MenuItem> = MenuItemIterator(menu)

    private class MenuItemIterator(private val menu: Menu) : Iterator<MenuItem> {
        private var index = 0
        private val count = menu.size()

        override fun next(): MenuItem {
            if (!hasNext()) {
                throw NoSuchElementException()
            }

            return menu.getItem(index++)
        }

        override fun hasNext(): Boolean {
            if (count != menu.size()) {
                throw ConcurrentModificationException()
            }

            return index < count
        }
    }
}
