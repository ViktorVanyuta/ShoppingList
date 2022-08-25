package com.sakhalinec.shoppinglist.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sakhalinec.shoppinglist.MAX_POOL_SIZE
import com.sakhalinec.shoppinglist.R
import com.sakhalinec.shoppinglist.VIEW_TYPE_DISABLED
import com.sakhalinec.shoppinglist.VIEW_TYPE_ENABLED


class MainActivity : AppCompatActivity(), ShopItemFragment.OnEditingFinishedListener {

    private lateinit var viewModel: MainViewModel

    /* использование адаптера со стандартным вызовом notifyItemChanged()
    private lateinit var shopListAdapter: ShopListAdapter
    */
    private lateinit var shopItemDiffListAdapter: ShopItemDiffListAdapter
    // null потому что в зависимости от ориентации экрана будет зависить создавать view или нет
    private var shopItemContainer: FragmentContainerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* элемент с этим id есть только в макете с альбомной ориентацией, а в другом макете нет
        * если метод findViewById по какому то id элемент не был найдет то, метод вернет null
        * если в shopItemContainer ничего не лежит его значение null то, значит мы
        * находимся в книжной ориентации! а если не равно null значит в альбомной ориентации. */
        shopItemContainer = findViewById(R.id.shop_item_container)
        setupRecyclerView()

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        // подписываемся на изменения в лайвдате
        viewModel.shopList.observe(this) {
            shopItemDiffListAdapter.submitList(it)
        }

        val btnAddItem = findViewById<FloatingActionButton>(R.id.btn_add_shop_item)
        btnAddItem.setOnClickListener {
            if (isOnePaneMode()) {
                val intent = ShopItemCardActivity.newIntentAddItem(this)
                startActivity(intent)
            } else {
                launchFragment(ShopItemFragment.newInstanceAddItem())
            }
        }
    }

    // переопределение метода интерфейса
    override fun onEditingFinished() {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
        supportFragmentManager.popBackStack()
    }

    // метод для проверки в каком режиме находимся, если получили null значит портретка
    private fun isOnePaneMode(): Boolean {
        return shopItemContainer == null
    }

    // метод для запуска фрагмента
    private fun launchFragment(fragment: Fragment) {
        // метод popBackStack удалит из стека один фрагмент если он там был, а если его не было то,
        // этот метод ничего не будет делать! перед добавлением нового фрагмента старый будет удален.
        supportFragmentManager.popBackStack()
        supportFragmentManager.beginTransaction()
            // метод add() добавляет новый фрагмент и ничего не делает со старым фрагментом если
            // он был, при каждом перевороте экрана добавляется новый фрагмент по верх предыдущего
            //.add(R.id.shop_item_container, fragment)
            .replace(R.id.shop_item_container, fragment) // replace заменит старый фрагмент на новый!
            .addToBackStack(null)
            .commit()
    }

    private fun setupRecyclerView() {
        val rvShopList = findViewById<RecyclerView>(R.id.rv_shop_list)
        with(rvShopList) {
            shopItemDiffListAdapter = ShopItemDiffListAdapter()
            adapter = shopItemDiffListAdapter
            // устанавливаю размер пула для каждого вью холдера в списке recyclerView,
            // это необходимо делать только когда список большой и
            // он возможно будет использоваться на слабых устройствах
            recycledViewPool.setMaxRecycledViews(VIEW_TYPE_ENABLED, MAX_POOL_SIZE)
            recycledViewPool.setMaxRecycledViews(VIEW_TYPE_DISABLED, MAX_POOL_SIZE)
        }

        setupLongClickListener()
        setupClickListener()
        setupSwipeListener(rvShopList)

    }

    // меняю состояние вью по долгому клику, по старинке!!!
    /*shopListAdapter.onShopItemLongClickListener =
        object : ShopListAdapter.OnShopItemLongClickListener {
            override fun onShopItemLongClick(shopItem: ShopItem) {
                viewModel.changeEnableState(shopItem)
            }
        }*/

    // по долгому клику меняется состояние вью.
    // новый, модный, молодежный способ! аналог коду выше!!!
    private fun setupLongClickListener() {
        shopItemDiffListAdapter.onShopItemLongClickListener = {
            viewModel.changeEnableState(it)
        }
    }

    // обычный клик по вью для редактирования или просмотра
    private fun setupClickListener() {
        shopItemDiffListAdapter.onShopItemClickListener = {
            if (isOnePaneMode()) {
                val intent = ShopItemCardActivity.newIntentEditItem(this, shopItemId = it.id)
                startActivity(intent)
            } else {
                launchFragment(ShopItemFragment.newInstanceEditItem(it.id))
            }
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
                /* currentList служит для получения текущего списка и получение
                 элемента коллекции для удаления по его позиции,
                 позицию можно получить из вью холдера */
                val item = shopItemDiffListAdapter.currentList[viewHolder.adapterPosition]
                viewModel.deleteShopItem(item)
            }
        }

        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rvShopList)
    }


}