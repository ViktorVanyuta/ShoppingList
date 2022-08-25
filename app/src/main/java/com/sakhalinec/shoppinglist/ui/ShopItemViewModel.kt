package com.sakhalinec.shoppinglist.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sakhalinec.shoppinglist.data.ShopListRepositoryImpl
import com.sakhalinec.shoppinglist.domain.AddShopItemUseCase
import com.sakhalinec.shoppinglist.domain.EditShopItemUseCase
import com.sakhalinec.shoppinglist.domain.GetShopItemUseCase
import com.sakhalinec.shoppinglist.domain.ShopItem
import java.lang.Exception

class ShopItemViewModel: ViewModel() {

    private val repository = ShopListRepositoryImpl

    private val getShopItemUseCase = GetShopItemUseCase(repository)
    private val addShopItemUseCase = AddShopItemUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)

    /* лайвдата для работы с ошибкой, подписываемся из Activity на errorInputName
    родительский класс LiveData для работы из ViewModel используется MutableLiveData */
    private val _errorInputName = MutableLiveData<Boolean>()
    val errorInputName: LiveData<Boolean>
        get() = _errorInputName

    /* аналогичная запись верхнему обьявлению лайвдаты и геттера, запись выше рекомендована гуглом
    private val errorInputName = MutableLiveData<Boolean>()
    fun getErrorInputNameLD(): LiveData<Boolean> {
        return errorInputName
    } */

    /* лайвдата для работы с ошибкой, подписываемся из Activity на errorInputName
     родительский класс LiveData для работы из ViewModel используется MutableLiveData */
    private val _errorInputCount = MutableLiveData<Boolean>()
    val errorInputCount: LiveData<Boolean>
        get() = _errorInputCount

    /* для получения элемента в методе getShopItem и установкой элемента в лайвдату */
    private val _shopItem = MutableLiveData<ShopItem>()
    val shopItem: LiveData<ShopItem>
        get() = _shopItem

    /* лайвдата для сообщения Activity когда можно закрывать экран тип Unit потому что
    * в этой ситуации Activity не будет смотреть на тип лайвдаты который прилетит в observer */
    private val _shouldCloseScreen = MutableLiveData<Unit>()
    val shouldCloseScreen: LiveData<Unit>
        get() = _shouldCloseScreen

    /* получаем объект */
    fun getShopItem(shopItemId: Int) {
        val item = getShopItemUseCase.getShopItem(shopItemId)
        _shopItem.value = item
    }

    /* добавляю объект */
    fun addShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldsValid = validateInput(name, count)
        if (fieldsValid) {
            val shopItem = ShopItem(name, count, true)
            addShopItemUseCase.addShopItem(shopItem)
            finishWork()
        }
    }

    /* редактирую объект */
    fun editShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldsValid = validateInput(name, count)
        if (fieldsValid) {
            /* берется существующий объект и делается его копия
            только в том случае если объект не равен null */
            _shopItem.value?.let {
                val item = it.copy(name = name, count = count)
                editShopItemUseCase.editShopItem(item)
                finishWork()
            }

        }
    }

    /* парсим вводимое имя в поле name и приводим его к нужному виду, trim() обрезает лишние пробелы */
    private fun parseName(inputName: String?): String {
        return inputName?.trim() ?: ""
    }

    /* парсим вводимое колличество в поле count и приводим его к нужному виду,
    в случае ошибки просто вернем 0 */
    private fun parseCount(inputCount: String?): Int {
        return try {
            inputCount?.trim()?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }

    /* проверка полей на их корректность, если данные введены не корректно то,
     получим false и получим ошибку */
    private fun validateInput(name: String, count: Int): Boolean {
        var result = true
        if (name.isBlank()) {
            _errorInputName.value = true
            result = false
        }
        if (count <= 0) {
            _errorInputCount.value = true
            result = false
        }
        return result
    }

    /* ошибка ввода имени в поле name */
    fun resetErrorInputName() {
        _errorInputName.value = false
    }

    /* ошибка ввода имени в поле count */
    fun resetErrorInputCount() {
        _errorInputCount.value = false
    }

    /* сообщает в observer о том что вся работа проведена и Activity можно закрывать экран */
    private fun finishWork() {
        _shouldCloseScreen.value = Unit
    }

}