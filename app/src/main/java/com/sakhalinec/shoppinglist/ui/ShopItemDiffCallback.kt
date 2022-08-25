package com.sakhalinec.shoppinglist.ui

import androidx.recyclerview.widget.DiffUtil
import com.sakhalinec.shoppinglist.domain.ShopItem

class ShopItemDiffCallback: DiffUtil.ItemCallback<ShopItem>() {

    // сравнивает объекты по id, чтобы адаптер понял работает он с одним и тем же объектом или разными
    override fun areItemsTheSame(oldItem: ShopItem, newItem: ShopItem): Boolean {
        // вернет true если id одинаковые и false если id разные
        return oldItem.id == newItem.id
    }

    // сравнивает поля объектов, чтобы узнать надо ли перерисовывать конкретный элемент
    override fun areContentsTheSame(oldItem: ShopItem, newItem: ShopItem): Boolean {
        /* так как у data class метод equals переопределен то,
         тут будет происходить сравнение по всем полям класса в первичном конструкторе.
         если изменений в полях нет вернется true и ничего перисовывать не нужно,
         а если изменения в полях есть вернется false и элемент будет перерисован*/
        return oldItem == newItem
    }

}
