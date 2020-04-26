/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.persistence.ui;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.persistence.R;
import com.example.android.persistence.databinding.ListFragmentBinding;
import com.example.android.persistence.db.entity.ProductEntity;
import com.example.android.persistence.viewmodel.ProductListViewModel;

import java.util.List;

public class ProductListFragment extends Fragment {

    public static final String TAG = "ProductListFragment";

    private ProductAdapter mProductAdapter;
    private LinearLayoutManager mManager;
    private ListFragmentBinding mBinding;

    private FrameLayout mRootSticky;
    private LinearLayout mContainerSticky;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.list_fragment, container, false);

        mRootSticky = new FrameLayout(getActivity());
        mContainerSticky = new LinearLayout(getActivity());
        mContainerSticky.setOrientation(LinearLayout.VERTICAL);
        mContainerSticky.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mRootSticky.addView(mContainerSticky);

        StickyHeaderView stickyHeaderView = new StickyHeaderView(getActivity());
        stickyHeaderView.setTextTitle("스티키1");
        StickyHeaderView stickyHeaderView2 = new StickyHeaderView(getActivity());
        stickyHeaderView2.setTextTitle("스티키2");
        StickyHeaderView stickyHeaderView3 = new StickyHeaderView(getActivity());
        stickyHeaderView3.setTextTitle("스티키3");
        mContainerSticky.addView(stickyHeaderView);
        mContainerSticky.addView(stickyHeaderView2);
        mContainerSticky.addView(stickyHeaderView3);

        ((ViewGroup)mBinding.getRoot()).addView(mRootSticky);


        mProductAdapter = new ProductAdapter(mProductClickCallback);
        mManager = new LinearLayoutManager(getActivity());
        mBinding.productsList.setLayoutManager(mManager);
        mBinding.productsList.setAdapter(mProductAdapter);
        mBinding.productsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                Log.d("kth","FirstVisible : " + mManager.findFirstVisibleItemPosition());
                Log.d("kth","FirstCompleteVisible : " + mManager.findFirstCompletelyVisibleItemPosition());

                View childView = recyclerView.getChildAt(mManager.findFirstCompletelyVisibleItemPosition());
                if(childView != null) {
                    Log.d("kth", "childView Top : " + childView.getTop());
                    // 2번째 뷰라면, 첫번째 뷰만큼의 height 빼준다.
                    // 3번째 뷰라면, 2번째 뷰만큼 height를 더해 빼준다.
                    Log.d("kth", "childView Top : " + childView.getTop());
                    int offset = childView.getTop();
                    if (offset >= 0 && offset <= 100) {
                        int topMargin = offset - 100; //마이너스 topMargin 주기
                        //rootview에 setY 설정.
                    }
                }





                super.onScrolled(recyclerView, dx, dy);
            }
        });

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ProductListViewModel viewModel =
                new ViewModelProvider(this).get(ProductListViewModel.class);

        mBinding.productsSearchBtn.setOnClickListener(v -> {
            Editable query = mBinding.productsSearchBox.getText();
            viewModel.setQuery(query);
        });

        subscribeUi(viewModel.getProducts());
    }

    private void subscribeUi(LiveData<List<ProductEntity>> liveData) {
        // Update the list when the data changes
        liveData.observe(getViewLifecycleOwner(), myProducts -> {
            if (myProducts != null) {
                mBinding.setIsLoading(false);
                mProductAdapter.setProductList(myProducts);
            } else {
                mBinding.setIsLoading(true);
            }
            // espresso does not know how to wait for data binding's loop so we execute changes
            // sync.
            mBinding.executePendingBindings();
        });
    }

    @Override
    public void onDestroyView() {
        mBinding = null;
        mProductAdapter = null;
        super.onDestroyView();
    }

    private final ProductClickCallback mProductClickCallback = product -> {
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            ((MainActivity) requireActivity()).show(product);
        }
    };
}
