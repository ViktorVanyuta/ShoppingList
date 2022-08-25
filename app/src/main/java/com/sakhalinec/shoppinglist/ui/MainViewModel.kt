package com.sakhalinec.shoppinglist.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sakhalinec.shoppinglist.data.ShopListRepositoryImpl
import com.sakhalinec.shoppinglist.data.ShopListRepositoryImpl.getShopList
import com.sakhalinec.shoppinglist.domain.DeleteShopItemUseCase
import com.sakhalinec.shoppinglist.domain.EditShopItemUseCase
import com.sakhalinec.shoppinglist.domain.GetShopListUseCase
import com.sakhalinec.shoppinglist.domain.ShopItem

class MainViewModel: ViewModel() {

    private val repository = ShopListRepositoryImpl

    private val getShopListUseCase = GetShopListUseCase(repository)
    private val deleteShopItemUseCase = DeleteShopItemUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)

    val shopList = getShopListUseCase.getShopList()


    fun deleteShopItem(shopItem: ShopItem) {
        deleteShopItemUseCase.deleteShopItem(shopItem)
    }

    fun changeEnableState(shopItem: ShopItem) {
        // создается полная копия ShopItem
        // но состояние будет противоположным к первоначальному обьекту
        val newItem = shopItem.copy(enabled = !shopItem.enabled)
        editShopItemUseCase.editShopItem(newItem)
    }

}