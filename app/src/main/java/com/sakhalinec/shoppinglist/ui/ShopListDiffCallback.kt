package com.sakhalinec.shoppinglist.ui

import androidx.recyclerview.widget.DiffUtil
import com.sakhalinec.shoppinglist.domain.ShopItem

class ShopListDiffCallback(
    private val oldList: List<ShopItem>,
    private val newList: List<ShopItem>
): DiffUtil.Callback() {

    // вернет размер старого списка
    override fun getOldListSize(): Int {
        return oldList.size
    }

    // вернет размер нового списка
    override fun getNewListSize(): Int {
        return newList.size
    }

    // сравнивает объекты по id, чтобы адаптер понял работает он с одним и тем же объектом или разными
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        // вернет true если id одинаковые и false если id разные
        return oldItem.id == newItem.id
    }

    // сравнивает поля объектов, чтобы узнать надо ли перерисовывать конкретный элемент
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        /* так как у data class метод equals переопределен то,
         тут будет происходить сравнение по всем полям класса в первичном конструкторе.
         если изменений в полях нет вернется true и ничего перисовывать не нужно,
         а если изменения в полях есть вернется false и элемент будет перерисован*/
        return oldItem == newItem
    }

    /* зачем вообще нужны 2 сравнения, это нужно если при первом сравнении именно объектов их
     id одинаковый то, один объект и его нужно проверить вторым сравнением по полям объекта,
     чтобы выяснить надо его перерисовывать или нет.
     Если при первом сравнении id равны, значит это один объект и второе сравнение не нужно.*/
}