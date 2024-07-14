package com.polytechnic.astra.ac.id.smartglowapp.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.polytechnic.astra.ac.id.smartglowapp.API.Repository.MyRepository;
import com.polytechnic.astra.ac.id.smartglowapp.Model.MyModel;

import java.util.List;

public class MyListViewModel extends ViewModel {

    private MutableLiveData<List<MyModel>> mMyModelListMutableLiveData;
    private MyRepository mMyRepository;

    public MyListViewModel(){
        mMyRepository = MyRepository.get();
        mMyModelListMutableLiveData = new MutableLiveData<>();
        // Initialize data retrieval from repository
        fetchMyModelList();
    }

    public MutableLiveData<List<MyModel>> getListModel(){
        return mMyModelListMutableLiveData;
    }

    private void fetchMyModelList() {
        // Use repository to fetch data asynchronously
        //mMyModelListMutableLiveData = mMyRepository.getMyListModel();
    }
}
