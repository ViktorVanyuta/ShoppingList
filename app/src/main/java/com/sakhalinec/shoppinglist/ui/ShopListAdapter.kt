package com.sakhalinec.shoppinglist.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sakhalinec.shoppinglist.R
import com.sakhalinec.shoppinglist.VIEW_TYPE_DISABLED
import com.sakhalinec.shoppinglist.VIEW_TYPE_ENABLED
import com.sakhalinec.shoppinglist.domain.ShopItem
import java.lang.RuntimeException

class ShopListAdapter: RecyclerView.Adapter<ShopItemViewHolder>() {

    var shopList = listOf<ShopItem>()
        set(value) { // переопределяю сеттер
            /* когда устанавливаем новое значение в shopList, создаем объект DiffCallback
               это мой класс в который передаем старый лист и новый лист, а так же он
               реализует методы для сравнения объектов. На этом этапе никаких вычислений нет.*/
            val callback = ShopListDiffCallback(shopList, value)
            /* все вычисления проводит метод calculateDiff из класса DiffUtil в метод передаем
               наш callback и он производит все расчеты, полностью сравнивает списки и их элементы
               и сохраняет все в объект diffResult
               Главный минус метода calculateDiff в том что он работает в главном потоке! */
            val diffResult = DiffUtil.calculateDiff(callback)
            /* после всех вычислений объект diffResult сообщает адаптеру какие методы следует вызвать
               например: при удалении notifyItemRemoved(int) или при изменении notifyItemChanged(int)
               если вставили notifyItemInserted(int)*/
            diffResult.dispatchUpdatesTo(this)
            /* после того как все сделано устанавливаем новое значение value нашей переменной shopList*/
            field = value
        }

    /* делаю переменую null для того чтобы использовать ее когда будет необходимость,
       а если она не нужна то так и останется нулабельной*/
    // var onShopItemLongClickListener: OnShopItemLongClickListener? = null

    /* слушатель клика с долгим нажатием.
       новый, модный, молодежный котлиновский, по сути аналог строчки выше!!!*/
    var onShopItemLongClickListener: ((ShopItem) -> Unit?)? = null

    /* слушатель клика с обычным нажатием. */
    var onShopItemClickListener: ((ShopItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {

        /* определяем по типу вью какую кард вью использовать */
        val layout = when (viewType) {
            VIEW_TYPE_ENABLED -> R.layout.item_shop_enabled
            VIEW_TYPE_DISABLED -> R.layout.item_shop_disabled
            else -> throw RuntimeException("Unknown view type: $viewType")
        }

        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ShopItemViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ShopItemViewHolder, position: Int) {
        val shopItem = shopList[position]
        viewHolder.tvName.text = shopItem.name
        viewHolder.tvCount.text = shopItem.count.toString()
        viewHolder.view.setOnLongClickListener{
              /* вызываю метод с кликом по вью */
            // onShopItemLongClickListener?.onShopItemLongClick(shopItem)
            onShopItemLongClickListener?.invoke(shopItem)
            true
        }
        viewHolder.view.setOnClickListener{
            onShopItemClickListener?.invoke(shopItem)
        }

    }

    override fun getItemCount(): Int {
        return shopList.size
    }

    /* отвечает за получение объекта по его позиции а так же, за определение типа view,
    для разных элементов используются разные макеты */
    override fun getItemViewType(position: Int): Int {

        val item = shopList[position]
        return if (item.enabled) {
            VIEW_TYPE_ENABLED
        } else {
            VIEW_TYPE_DISABLED
        }

    }


    // долгий клик по вью, этот интерфейс нужен для варианта по старинке!!!
    /*interface OnShopItemLongClickListener {
        fun onShopItemLongClick(shopItem: ShopItem)
    }*/

}