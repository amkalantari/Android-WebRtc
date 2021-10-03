package com.axon.webrtc.util.bindingAdapter

import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.core.databinding.LayoutRecyclerLoadingBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


fun <T : Any, VM : RecyclerView.ViewHolder> PagingDataAdapter<T, VM>.handleLoadingPagerAdapter(
    lifecycleScope: LifecycleCoroutineScope,
    layout: LayoutRecyclerLoadingBinding
): PagingDataAdapter<T, VM> {
    lifecycleScope.launch {
        this@handleLoadingPagerAdapter.addLoadStateListener { loadState ->
            launch(Dispatchers.Main) {
                if (loadState.refresh is LoadState.Loading) {
                    layout.progressBar.visibility = View.GONE
                    if (this@handleLoadingPagerAdapter.itemCount == 0) {
                        layout.emptyLayout.visibility = View.VISIBLE
                    }else{
                        layout.emptyLayout.visibility = View.GONE
                    }
                } else {
                    if (loadState.append is LoadState.Loading) {
                        layout.progressBar.visibility = View.VISIBLE
                    }
                }
                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error

                errorState?.let {
                    layout.progressBar.visibility = View.GONE
                    layout.emptyLayout.visibility = View.VISIBLE
                }
            }
        }
    }
    return this
}
