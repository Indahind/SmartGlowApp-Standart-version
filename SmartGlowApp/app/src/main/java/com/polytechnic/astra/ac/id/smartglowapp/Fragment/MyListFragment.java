//package com.polytechnic.astra.ac.id.smartglowapp.Fragment;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.Observer;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.polytechnic.astra.ac.id.smartglowapp.Model.MyModel;
//import com.polytechnic.astra.ac.id.smartglowapp.R;
//import com.polytechnic.astra.ac.id.smartglowapp.ViewModel.MyListViewModel;
//
//import java.util.Collections;
//import java.util.List;
//
//public class MyListFragment extends Fragment {
//
//    private MyListViewModel mMyModelListViewModel;
//    private RecyclerView mMyModelRecyclerView;
//    private MyModelAdapter mMyModelAdapter;
//
//    public static MyListFragment newInstance(){
//        return new MyListFragment();
//    }
//
//    private class MyModelHolder extends RecyclerView.ViewHolder {
//
//        private TextView mIdTextView;
//        private TextView mUsernameTextView;
//        private MyModel mMyModel;
//
//        public MyModelHolder(LayoutInflater inflater, ViewGroup parent){
//            super(inflater.inflate(R.layout.recylerview_mymodellist, parent, false));
//            mIdTextView = (TextView) itemView.findViewById(R.id.txv_id);
//            mUsernameTextView = (TextView) itemView.findViewById(R.id.txv_username);
//        }
//
//        public void bind(MyModel data){
//            mMyModel = data;
//            mIdTextView.setText(mMyModel.getId());
//            mUsernameTextView.setText(mMyModel.getName());
//        }
//    }
//
//    private class MyModelAdapter extends RecyclerView.Adapter<MyModelHolder>{
//        private List<MyModel> mMyModelList;
//        public MyModelAdapter(List<MyModel> datas){
//            mMyModelList = datas;
//        }
//
//        @Override
//        public MyModelHolder onCreateViewHolder(ViewGroup parent, int viewType){
//            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
//            return new MyModelHolder(layoutInflater, parent);
//        }
//
//        @Override
//        public void onBindViewHolder(MyModelHolder holder, int position){
//            MyModel data = mMyModelList.get(position);
//            holder.bind(data);
//        }
//
//        @Override
//        public int getItemCount(){
//            return mMyModelList.size();
//        }
//    }
//
//    private void updateUI(List<MyModel> datas){
//        mMyModelAdapter = new MyModelAdapter(datas);
//        mMyModelRecyclerView.setAdapter(mMyModelAdapter);
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mMyModelListViewModel = new ViewModelProvider(this)
//                .get(MyListViewModel.class);
//        mMyModelAdapter = new MyModelAdapter(Collections.<MyModel>emptyList());
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_mymodelist,
//                container, false);
//        mMyModelRecyclerView = (RecyclerView) view.findViewById(R.id.rvw_mymodel);
//        mMyModelRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mMyModelRecyclerView.setAdapter(mMyModelAdapter);
//        return view;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        mMyModelListViewModel.getListModel().observe(
//                getViewLifecycleOwner(),
//                new Observer<List<MyModel>>() {
//                    @Override
//                    public void onChanged(List<MyModel> myModelList) {
//                        updateUI(myModelList);
//                        Log.d("TEST","Called" + myModelList.size());
//                    }
//                }
//        );
//    }
//
//
//}
