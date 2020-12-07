package com.view.image.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.view.image.activity.GalleryActivity
import com.view.image.adapter.HomeDataShowAdapter
import com.view.image.databinding.FragmentHomeDataShowBinding
import com.view.image.model.DATA_STATUS_NETWORK_ERROR
import com.view.image.model.HomeData
import com.view.image.model.HomeDataViewModel
import com.view.image.model.HomeRuleViewModel

class HomeDataShowFragment : Fragment() {
    lateinit var fragmentBinding: FragmentHomeDataShowBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        fragmentBinding = FragmentHomeDataShowBinding.inflate(layoutInflater)
        return fragmentBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val homeDataShowAdapter = HomeDataShowAdapter()
//        val layoutManager = GridLayoutManager(requireContext(), 2)
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        fragmentBinding.recyclerView.apply {
            adapter = homeDataShowAdapter
            this.layoutManager = layoutManager
        }

        val homeDataViewModel =
            ViewModelProvider(activity ?: this).get(HomeDataViewModel::class.java)
        val ruleViewModel = ViewModelProvider(activity ?: this).get(HomeRuleViewModel::class.java)

        homeDataShowAdapter.setOnClickListener(object : HomeDataShowAdapter.ClickListener {
            override fun setOnClickListener(view: View, data: HomeData) {
                ruleViewModel.ruleLive.value?.let {
                    GalleryActivity.actionStart(requireContext(), data, it)
                }
            }

        })
        if (homeDataViewModel.photoListLive.value.isNullOrEmpty()) {
            homeDataShowAdapter.submitList(homeDataViewModel.photoListLive.value)
        }
        //观察数据变化
        homeDataViewModel.photoListLive.observe(this.viewLifecycleOwner, {
            homeDataShowAdapter.submitList(it)
            if (homeDataViewModel.isRefresh || homeDataViewModel.pageNum.value == 1)
                fragmentBinding.recyclerView.scrollToPosition(0)
            fragmentBinding.gallerySwipe.isRefreshing = false
        }
        )

        // 观察底部状态
        homeDataViewModel.dataStatusLive.observe(this.viewLifecycleOwner, {
            homeDataShowAdapter.footerViewStatus = it
            // 用于网络错误
            homeDataShowAdapter.notifyItemChanged(homeDataShowAdapter.itemCount - 1)
            if (it == DATA_STATUS_NETWORK_ERROR)
                fragmentBinding.gallerySwipe.isRefreshing = false
        })

        // 第一次加载数据
        homeDataViewModel.dataUrl.observe(this.viewLifecycleOwner, {
            homeDataViewModel.clearImageUrl()
            homeDataViewModel.isRefresh = true
            homeDataViewModel.setPageNum(1)
            homeDataViewModel.getHomeDataList()
        })

        homeDataViewModel.curReqUrl.observe(viewLifecycleOwner, {
            homeDataShowAdapter.setReferer(it)
        })


        homeDataViewModel.pageNum.observe(this.viewLifecycleOwner, {
            homeDataViewModel.getHomeDataList()
        })

        // 下拉刷新
        fragmentBinding.gallerySwipe.setOnRefreshListener {
            homeDataViewModel.isRefresh = true
            homeDataViewModel.getHomeDataList()
        }

        // 底部刷新
        fragmentBinding.recyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy < 0) return
                val intArray = IntArray(2)
                layoutManager.findLastVisibleItemPositions(intArray)
                if (intArray[0] == homeDataShowAdapter.itemCount - 1) {
                    val pageNum = homeDataViewModel.pageNum.value!! + 1
                    if (!homeDataViewModel.isBeGetVale) {
                        homeDataViewModel.isRefresh = false
                        homeDataViewModel.setPageNum(pageNum)
                    }
                }
            }
        })
    }

}