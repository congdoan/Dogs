package com.cdoan.dogs.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.cdoan.dogs.R
import com.cdoan.dogs.viewmodel.ListViewModel
import kotlinx.android.synthetic.main.fragment_list.*


class ListFragment : Fragment() {

    private lateinit var listViewModel: ListViewModel
    private val dogListAdapter = DogListAdapter(listOf())


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout.setOnRefreshListener {
            listViewModel.refreshBypassCache {
                swipeRefreshLayout.isRefreshing = false
            }
        }

        listViewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)
        listViewModel.refresh()

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dogListAdapter
        }

        observeListViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionSettings -> view?.let {
                Navigation.findNavController(it)
                    .navigate(ListFragmentDirections.actionSettings())
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun observeListViewModel() {
        listViewModel.data.observe(this) { state ->
            if (state == null) return@observe
            when (state) {
                is ListViewModel.LoadStateSuccess -> {
                    dogListAdapter.updateDogList(state.dogs)

                    loadingView.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    errorView.visibility = View.GONE
                }
                is ListViewModel.LoadStateFailure -> {
                    loadingView.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    errorView.visibility = View.VISIBLE
                }
                is ListViewModel.LoadStateLoading -> {
                    loadingView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    errorView.visibility = View.GONE
                }
            }
        }
    }

}