package com.sakhalinec.shoppinglist.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.sakhalinec.shoppinglist.MAX_POOL_SIZE
import com.sakhalinec.shoppinglist.R
import com.sakhalinec.shoppinglist.VIEW_TYPE_DISABLED
import com.sakhalinec.shoppinglist.VIEW_TYPE_ENABLED

class ExampleMainActivityItemDiffCallback: AppCompatActivity() {

    private  lateinit var viewModel: MainViewModel
    private lateinit var shopItemDiffListAdapter: ShopItemDiffListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecyclerView()

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        // подписываемся на изменения в лайвдате
        viewModel.shopList.observe(this){
            /* когда вызывается метод submitList внутри адаптера запускается новый поток и
            в нем проводятся все вычисления, и только после всех вычислений список обновляется */
            shopItemDiffListAdapter.submitList(it)
        }

    }

    private fun setupRecyclerView() {
        val rvShopList = findViewById<RecyclerView>(R.id.rv_shop_list)
        with(rvShopList) {
            shopItemDiffListAdapter = ShopItemDiffListAdapter()
            adapter = shopItemDiffListAdapter
            // устанавливаю размер пула для каждого вью холдера в списке recyclerView,
            // это необходимо делать только когда список большой и
            // он возможно будет использоваться на слабых устройствах
            recycledViewPool.setMaxRecycledViews(
                VIEW_TYPE_ENABLED,
                MAX_POOL_SIZE
            )
            recycledViewPool.setMaxRecycledViews(
                VIEW_TYPE_DISABLED,
                MAX_POOL_SIZE
            )
        }

        setupLongClickListener()
        setupClickListener()
        setupSwipeListener(rvShopList)

    }

    private fun setupLongClickListener() {
        shopItemDiffListAdapter.onShopItemLongClickListener = {
            viewModel.changeEnableState(it)
        }
    }

    private fun setupClickListener() {
        shopItemDiffListAdapter.onShopItemClickListener = {
            Log.d("MainActivity", it.toString())
        }
    }

    private fun setupSwipeListener(rvShopList: RecyclerView) {
        // удаление вью свайпом лево, право
        // 0 это значение отвечающее за перемещение элемента, при 0 перемещение отсутствует!
        val callback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // возвращаю false так как этот метод не нужен.
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // получение элемента коллекции для удаления по его позиции,
                // позицию можно получить из вью холдера
                val item = shopItemDiffListAdapter.currentList[viewHolder.adapterPosition]
                viewModel.deleteShopItem(item)
            }
        }

        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rvShopList)
    }
}