package com.sakhalinec.shoppinglist.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sakhalinec.shoppinglist.R
import com.sakhalinec.shoppinglist.domain.ShopItem
import java.lang.RuntimeException

class ShopItemCardActivity : AppCompatActivity(), ShopItemFragment.OnEditingFinishedListener {

//    private lateinit var shopItemViewModel: ShopItemViewModel
//
//    private lateinit var tilName: TextInputLayout
//    private lateinit var tilCount: TextInputLayout
//    private lateinit var etName: EditText
//    private lateinit var etCount: EditText
//    private lateinit var btnSave: Button

    private var screenMode = MODE_UNKNOWN
    private var shopItemId = ShopItem.UNDEFINED_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_item_card)
        parseIntent()
//        shopItemCardViewModel = ViewModelProvider(this)[ShopItemCardViewModel::class.java]
//        initViews()
//        addTextChangeListeners()
//        launchRightModeActivity()
//        observeViewModel()

        // чтобы при перевороте экрана избежать постоянного создания фрагмента нами и системой,
        // вызов этого метода нужен только при первом создании активити, а дальше фрагмент будет
        // пересоздан системой и нужно проверить savedInstanceState равен null значит activity
        // не пересоздавалась и в этом случае нам нужно создать фрагмент
        if (savedInstanceState == null) {
            launchRightModeFragment()
        }

    }

    override fun onEditingFinished() {
        finish()
    }


//
///* подписываемся на объекты вьюмодели */
//
//    private fun observeViewModel() {
//
///* подписываемся на объект вьюмодели errorInputCount ошибка ввода колличества */
//
//        shopItemCardViewModel.errorInputCount.observe(this) {
//
///* если нужно показать ошибку то, будет показана строка из строковых ресурсов
//            если нет то будет null и ничего не произойдет */
//
//            val message = if (it == true) {
//                getString(R.string.error_input_count)
//            } else {
//                null
//            }
//            tilCount.error = message
//        }
//
///* подписываемся на объект вьюмодели errorInputName ошибка ввода имени */
//
//        shopItemCardViewModel.errorInputName.observe(this) {
//            val message = if (it == true) {
//                getString(R.string.error_input_name)
//            } else {
//                null
//            }
//            tilName.error = message
//        }
//
//
///* если работа с экраном завершена подписываемся на объект вьюмодели
//        shouldCloseScreen и вызываем метод finish */
//
//        shopItemCardViewModel.shouldCloseScreen.observe(this) {
//            finish()
//        }
//    }

    /* устанавливаем правильный режим отображения во фрагменте */
    private fun launchRightModeFragment() {
        /* настройка запуска экрана */
        val fragment = when (screenMode) {
                /* запуск экрана в режиме редактирования экземпляра фрагмента */
                MODE_EDIT -> ShopItemFragment.newInstanceEditItem(shopItemId)
                /* запуск экрана в режиме добавления экземпляра фрагмента */
                MODE_ADD -> ShopItemFragment.newInstanceAddItem()
                else -> throw RuntimeException("Unknown screen mode $screenMode")
            }
        // supportFragmentManager используется для работы с фрагментами, он вставляет полученный
        // фрагмент в контейнер при использовании транзакций и вызова метода commit
        supportFragmentManager.beginTransaction()
            .replace(R.id.shop_item_container, fragment)
            .commit()
    }

//    /* устанавливаем правильный режим отображения */
//    private fun launchRightModeActivity() {
//
//        /* настройка запуска экрана */
//        when (screenMode) {
//
//            /* запуск экрана в режиме редактирования элемента */
//            MODE_EDIT -> launchEditMode()
//
//            /* запуск экрана в режиме добавления элемента */
//            MODE_ADD -> launchAddMode()
//        }
//    }

//    /* устанавливаем слушатели текста */
//    private fun addTextChangeListeners() {
//
//        /* скрытие ошибки при вводе текста */
//        etName.addTextChangedListener(object : TextWatcher {
//
//            /* до того как текст изменен */
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            /* в момент изменения текста, у вьюмодели вызываем метод который скрывает ошибку */
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                shopItemCardViewModel.resetErrorInputName()
//            }
//
//            /* после изменения текста */
//            override fun afterTextChanged(s: Editable?) {
//            }
//        })
//
//        /* скрытие ошибки при вводе текста */
//        etCount.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            /* в момент изменения текста, у вьюмодели вызываем метод который скрывает ошибку */
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                shopItemCardViewModel.resetErrorInputCount()
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//            }
//        })
//    }


//    /* метод запуска режима редактирования элемента */
//    private fun launchEditMode() {
//
//        /* получаем элемент по его id */
//        shopItemCardViewModel.getShopItem(shopItemId)
//
//        /* подписываемся на данный элемент */
//        shopItemCardViewModel.shopItem.observe(this) {
//
//        /* после загрузки объекта, устанавливаются значения из загруженного итема в поля ввода */
//            etName.setText(it.name)
//
//        /* count это число, поэтому его нужно привести к нужному типу String */
//            etCount.setText(it.count.toString())
//        }
//
//        /* при клике на кнопку сохранить, вызывается из вьюмодели метод editShopItem и в него
//         передаются значения из полей ввода */
//        btnSave.setOnClickListener {
//            shopItemCardViewModel.editShopItem(etName.text?.toString(), etCount.text?.toString())
//        }
//    }
//
//
//    /* метод запуска режима добавления элемента */
//    private fun launchAddMode() {
//
//        /* при клике на кнопку из вьюмодели вызывается метод addShopItem и
//        в него устанавливаются значения из полей ввода */
//        btnSave.setOnClickListener {
//            shopItemCardViewModel.addShopItem(etName.text?.toString(), etCount.text?.toString())
//        }
//    }
////
//
    /* проверка параметров в интентов переданы успешно */
    private fun parseIntent() {

        /* если интент не содержит параметр extra_screen_mode то бросаем исключение */
        if (!intent.hasExtra(EXTRA_SCREEN_MODE)) {
            throw RuntimeException("Param screen mode is absent")
        }

        /* переданный параметр пишем в переменную mode */
        val mode = intent.getStringExtra(EXTRA_SCREEN_MODE)

        /* проверяем переменную mode на наличие параметра изменения и добавления,
        если mode не равен одному из параметров то, бросаем исключение */
        if (mode != MODE_EDIT && mode != MODE_ADD) {
            throw RuntimeException("Unknown screen mode $mode")
        }

        /* в screenMode передаю mode и если этот мод на редактирование то, нужно проверить id */
        screenMode = mode
        if (screenMode == MODE_EDIT) {

            /* если интент не содержит параметр id то, бросаю исключение */
            if (!intent.hasExtra(EXTRA_SHOP_ITEM_ID)) {
                throw RuntimeException("Param shop item id is absent")
            }

            /* записываю из intenta в shopItemId параметр id элемента */
            shopItemId = intent.getIntExtra(EXTRA_SHOP_ITEM_ID, ShopItem.UNDEFINED_ID)
        }
    }

//    private fun initViews() {
//        tilName = findViewById(R.id.text_input_layout_name)
//        tilCount = findViewById(R.id.text_input_layout_count)
//        etName = findViewById(R.id.edit_text_name)
//        etCount = findViewById(R.id.edit_text_count)
//        btnSave = findViewById(R.id.save_btn)
//    }

    companion object {

        private const val EXTRA_SCREEN_MODE = "extra_mode"
        private const val EXTRA_SHOP_ITEM_ID = "extra_shop_item_id"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_ADD = "mode_add"
        private const val MODE_UNKNOWN = ""


        /* статический фабричный метод вызова интента для запуска активити на добавления элемента списка */
        fun newIntentAddItem(context: Context): Intent {
            val intent = Intent(context, ShopItemCardActivity::class.java)
            intent.putExtra(EXTRA_SCREEN_MODE, MODE_ADD)
            return intent
        }


        /* статический фабричный метод вызова интента для запуска активити на редактирование елемента списка */
        fun newIntentEditItem(context: Context, shopItemId: Int): Intent {
            val intent = Intent(context, ShopItemCardActivity::class.java)
            intent.putExtra(EXTRA_SCREEN_MODE, MODE_EDIT)
            intent.putExtra(EXTRA_SHOP_ITEM_ID, shopItemId)
            return intent
        }

    }



}