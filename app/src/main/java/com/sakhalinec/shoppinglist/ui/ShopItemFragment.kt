package com.sakhalinec.shoppinglist.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import com.sakhalinec.shoppinglist.R
import com.sakhalinec.shoppinglist.domain.ShopItem

class ShopItemFragment
/* (
Передача параметров в констуктор фрагмента плохая затея!!!
Например, если хотим показать фрагмент то, создается его экземпляр и в этот момент можно передать
в конструктор любые параметры, но если перевернуть экран, а фрагмент находился на этом экране то,
система захочет его пересоздать и при пересоздании будет вызван пустой конструктор фрагмента,
а его нету так как мы передали какие то параметры. Поэтому приложение будет падать,
так как не сможет найти подходящий конструктор! То есть пустой!!!
Передавать параметры в конструктор фрагмента нужно через arguments типа Bundle.
Bundle это коллекция, а конкретно Map<key, value>

 private val screenMode: String = MODE_UNKNOWN,
 private val shopItemId: Int = ShopItem.UNDEFINED_ID
) */
    : Fragment() {

    private lateinit var shopItemViewModel: ShopItemViewModel
    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private lateinit var tilName: TextInputLayout
    private lateinit var tilCount: TextInputLayout
    private lateinit var etName: EditText
    private lateinit var etCount: EditText
    private lateinit var btnSave: Button

    private var screenMode = MODE_UNKNOWN
    private var shopItemId = ShopItem.UNDEFINED_ID

    // фрагмент прикрепляется к активити которая передается в контексте, то-есть контекс
    // это та самая активити к которой нужно прикрепить фрагмент
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // если активити реализует интерфейс то приваиваем контекст в интерфейсную переменную
        if (context is OnEditingFinishedListener) {
            onEditingFinishedListener = context
        } else {
            // бросит исключение если активити не реализует интерфейс
            throw RuntimeException("Activity must implement OnEditingFinishedListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // проверка переданных параметров во фрагмент при его создании
        parseParams()
    }

    /* этот метод служит для создания из макета вью R.layout.fragment_shop_item */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shop_item, container, false)
    }

    /* этот метод вызывается только тогда когда вью уже точно создана из макета
     и начиная с этого метода можно работать с вью без ограничений */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shopItemViewModel = ViewModelProvider(this)[ShopItemViewModel::class.java]
        initViews(view)
        addTextChangeListeners()
        launchRightMode()
        observeViewModel()
    }


/* подписываемся на объекты вьюмодели */
    private fun observeViewModel() {

/* подписываемся на объект вьюмодели errorInputCount ошибка ввода колличества */
    /* в качестве параметра в метод observe нужно передать
    viewLifecycleOwner(это объект у которого есть жизненый цикл) использует
    не жизненый цикл фрагмента, а жизненый цикл созданой view в методе onViewCreated из макета
    переданного в методе onCreateView и когда view умрет то мы отпишемся от лайвдаты */
        shopItemViewModel.errorInputCount.observe(viewLifecycleOwner) {

/* если нужно показать ошибку то, будет показана строка из строковых ресурсов
            если нет то будет null и ничего не произойдет */
            val message = if (it == true) {
                getString(R.string.error_input_count)
            } else {
                null
            }
            tilCount.error = message
        }


/* подписываемся на объект вьюмодели errorInputName ошибка ввода имени */
        shopItemViewModel.errorInputName.observe(viewLifecycleOwner) {
            val message = if (it == true) {
                getString(R.string.error_input_name)
            } else {
                null
            }
            tilName.error = message
        }


/* если работа с экраном завершена подписываемся на объект вьюмодели
        shouldCloseScreen и вызываем метод finish */
        shopItemViewModel.shouldCloseScreen.observe(viewLifecycleOwner) {
            /* чтобы получить ссылку на activity к которой прикреплен фрагмент, можно вызвать метод
             getActivity( activity? в kotlin ) или requireActivity их разница в ->
            * activity? - возвращает нулабельный объект, поэтому нужно делать проверку на null
            * requireActivity - возвращает не нулабельный объект, нужно быть осторожнее при вызове
             этого метода так как можно получить null */
            // activity?.onBackPressed()

            onEditingFinishedListener.onEditingFinished()

            /* ВО ФРАГМЕНТЕ МНОГО ПОХОЖИХ МЕТОДОВ!!! */
            /* вернет нулабельный объект */
            // context?.applicationContext
            /* вернет не нулабельный объект */
            // requireContext().applicationContext

            /* вернет нулабельную объект */
            // view?.id
            /* вернет не нулабельную объект */
            // requireView().id

        }
    }


/* устанавливаем правильный режим отображения */
    private fun launchRightMode() {

/* настройка запуска экрана */
        when (screenMode) {

/* запуск экрана в режиме редактирования элемента */
            MODE_EDIT -> launchEditMode()

/* запуск экрана в режиме добавления элемента */
            MODE_ADD -> launchAddMode()
        }
    }


/* устанавливаем слушатели текста */
    private fun addTextChangeListeners() {

/* скрытие ошибки при вводе текста */
        etName.addTextChangedListener(object : TextWatcher {

/* до того как текст изменен */
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }


/* в момент изменения текста, у вьюмодели вызываем метод который скрывает ошибку */
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                shopItemViewModel.resetErrorInputName()
            }


/* после изменения текста */
            override fun afterTextChanged(s: Editable?) {
            }
        })


/* скрытие ошибки при вводе текста */
        etCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }


/* в момент изменения текста, у вьюмодели вызываем метод который скрывает ошибку */
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                shopItemViewModel.resetErrorInputCount()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }


/* метод запуска режима редактирования элемента */
    private fun launchEditMode() {

/* получаем элемент по его id */
        shopItemViewModel.getShopItem(shopItemId)

/* подписываемся на данный элемент */
        shopItemViewModel.shopItem.observe(viewLifecycleOwner) {

/* после загрузки объекта, устанавливаются значения из загруженного итема в поля ввода */
            etName.setText(it.name)

/* count это число, поэтому его нужно привести к нужному типу String */
            etCount.setText(it.count.toString())
        }

/* при клике на кнопку сохранить, вызывается из вьюмодели метод editShopItem и в него
         передаются значения из полей ввода */
        btnSave.setOnClickListener {
            shopItemViewModel.editShopItem(etName.text?.toString(), etCount.text?.toString())
        }
    }


/* метод запуска режима добавления элемента */
    private fun launchAddMode() {

/* при клике на кнопку из вьюмодели вызывается метод addShopItem и
        в него устанавливаются значения из полей ввода */
        btnSave.setOnClickListener {
            shopItemViewModel.addShopItem(etName.text?.toString(), etCount.text?.toString())
        }
    }


    /* в методе получаем переданные аргументы через requireArguments, если параметры не были переданы
     то приложение сразу упадет, так как будет получен null в переданных параметрах */
    private fun parseParams() {
        val args = requireArguments()
        //
        if (!args.containsKey(SCREEN_MODE)) {
            throw RuntimeException("Param screen mode is absent")
        }

        val mode = args.getString(SCREEN_MODE)
        if (mode != MODE_EDIT && mode != MODE_ADD) {
            throw RuntimeException("Unknown screen mode $mode")
        }

        screenMode = mode
        if (screenMode == MODE_EDIT) {
            if (!args.containsKey(SHOP_ITEM_ID)) {
                throw RuntimeException("Param shop item id is absent")
            }
            shopItemId = args.getInt(SHOP_ITEM_ID, ShopItem.UNDEFINED_ID)
        }

    }


    /* параметр view получаем из onViewCreated так как там вью уже создана то можно вызвать
     метод findViewById */
    private fun initViews(view: View) {
        tilName = view.findViewById(R.id.text_input_layout_name)
        tilCount = view.findViewById(R.id.text_input_layout_count)
        etName = view.findViewById(R.id.edit_text_name)
        etCount = view.findViewById(R.id.edit_text_count)
        btnSave = view.findViewById(R.id.save_btn)
    }

    // для реализации общения фрагмента с активити
    interface OnEditingFinishedListener {
        fun onEditingFinished()
    }

    companion object {

        private const val SCREEN_MODE = "extra_mode"
        private const val SHOP_ITEM_ID = "extra_shop_item_id"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_ADD = "mode_add"
        private const val MODE_UNKNOWN = ""

        /* статический фабричный метод для режима создания нового инстанса фрагмента */
        fun newInstanceAddItem(): ShopItemFragment {
//            val args = Bundle()
//            args.putString(SCREEN_MODE, MODE_ADD)
//            val fragment = ShopItemFragment()
//            fragment.arguments = args
//            return fragment

//            val args = Bundle().apply {
//                putString(SCREEN_MODE, MODE_ADD)
//            }
//            val fragment = ShopItemFragment().apply {
//                arguments = args
//            }
//            return fragment

            // стильно модно молодежно :)
            /* Будет создан экземпляр класса фрагмента у которого будут вызваны методы arguments
             которые будут переданы в Bundle и применятся к вновь созданному фрагменту */
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_ADD)
                }
            }
        }

        /* статический фабричный метод для режима редактирования инстанса фрагмента */
        fun newInstanceEditItem(shopItemId: Int): ShopItemFragment {
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_EDIT)
                    putInt(SHOP_ITEM_ID, shopItemId)
                }
            }
        }

    }

}