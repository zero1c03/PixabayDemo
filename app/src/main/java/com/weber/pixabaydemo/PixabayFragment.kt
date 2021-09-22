package com.weber.pixabaydemo

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.weber.pixabaydemo.adapter.PixabayAdapter
import com.weber.pixabaydemo.databinding.FragmentPixabayBinding
import com.weber.pixabaydemo.utils.Utils
import com.weber.pixabaydemo.viewmodels.PixabayViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PixabayFragment : BaseFragment() {

    private lateinit var binding: FragmentPixabayBinding
    private lateinit var pixabayAdapter: PixabayAdapter
    private lateinit var layoutManager: GridLayoutManager
    private val pixabayViewModel: PixabayViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPixabayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            pixabayAdapter = PixabayAdapter(requireContext(), pixabayViewModel)
            pixabayAdapter.addLoadStateListener {
                if (it.append == LoadState.NotLoading(false)) {
                    mainViewModel.mLoading.postValue(View.GONE)
                    binding.tvNoResult.visibility =
                        if (pixabayAdapter.itemCount == 0) View.VISIBLE else View.GONE
                } else {
                    mainViewModel.mLoading.postValue(View.VISIBLE)
                }
            }
            binding.rvPhotos.adapter = pixabayAdapter
            layoutManager = GridLayoutManager(requireContext(), 1)
            binding.rvPhotos.layoutManager = layoutManager
            binding.rvPhotos.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    Utils.hideSoftKeyboard(requireContext(), binding.tvSearch)
                }
                false
            }
            binding.srPhoto.setOnRefreshListener {
                pixabayAdapter.refresh()
            }
            pixabayViewModel.layoutType.postValue(PixabayViewModel.Companion.LayoutType.LIST.ordinal)
            pixabayViewModel.queryString.observe(viewLifecycleOwner, {
                this.launch { getPhotos() }
            })
            binding.tvSearch.imeOptions = EditorInfo.IME_ACTION_SEARCH
            binding.tvSearch.setOnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    binding.btSearch.performClick()
                }
                false
            }
            binding.tvSearch.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    binding.btSearch.performClick()
                }
                false
            }

            val tvSearchAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                pixabayViewModel.autoCompleteList
            )
            binding.tvSearch.setOnClickListener {
                binding.tvSearch.showDropDown()
            }

            binding.tvSearch.setAdapter(tvSearchAdapter)
            binding.btSearch.setOnClickListener {
                if (!binding.tvSearch.text.toString().isNullOrEmpty()) {
                    binding.tvSearch.error = null
                    Utils.hideSoftKeyboard(requireContext(), binding.tvSearch)
                    tvSearchAdapter.add(binding.tvSearch.text.toString())
                    pixabayViewModel.queryString.postValue(binding.tvSearch.text.toString())
                } else {
                    binding.tvSearch.error = getString(R.string.search_empty)
                }
            }
            getPhotos()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_pixabay, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.swap_view) {
            switchLayoutManager()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun switchLayoutManager() {
        if (pixabayViewModel.layoutType.value ==
            PixabayViewModel.Companion.LayoutType.LIST.ordinal
        ) {
            pixabayViewModel.layoutType.postValue(PixabayViewModel.Companion.LayoutType.GRID.ordinal)
            layoutManager.spanCount = 4
        } else {
            pixabayViewModel.layoutType.postValue(PixabayViewModel.Companion.LayoutType.LIST.ordinal)
            layoutManager.spanCount = 1
        }
        pixabayAdapter.notifyItemChanged(0, pixabayAdapter.itemCount)
    }

    private fun refreshLayoutManager(type: Int) {
        if (type == PixabayViewModel.Companion.LayoutType.LIST.ordinal) {
            pixabayViewModel.layoutType.postValue(PixabayViewModel.Companion.LayoutType.LIST.ordinal)
            layoutManager.spanCount = 1
        } else {
            pixabayViewModel.layoutType.postValue(PixabayViewModel.Companion.LayoutType.GRID.ordinal)
            layoutManager.spanCount = 4
        }
        pixabayAdapter.notifyItemChanged(0, pixabayAdapter.itemCount)
    }


    private suspend fun getPhotos() {
        mainViewModel.mLoading.postValue(View.VISIBLE)
        coroutineScope {
            launch {
                pixabayViewModel.getPhotos().collect {
                    refreshLayoutManager(pixabayViewModel.getFireBaseLayoutType())
                    mainViewModel.mLoading.postValue(View.GONE)
                    pixabayAdapter.submitData(it)
                    binding.srPhoto.isRefreshing = false
                    binding.tvSearch.error = null
                }
            }
        }
    }

}

