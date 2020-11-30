package com.example.bookit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BookPathViewModel extends ViewModel {
    private final MutableLiveData<String> selectedItem = new MutableLiveData<>();
    public void selectItem(String item) {
        selectedItem.setValue(item);
    }
    public String getSelectedItem() {
        return selectedItem.getValue();
    }
}