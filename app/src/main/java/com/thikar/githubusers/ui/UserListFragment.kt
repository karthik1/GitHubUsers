package com.thikar.githubusers.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.thikar.githubusers.R
import com.thikar.githubusers.adapter.UserListAdapter
import com.thikar.githubusers.databinding.FragmentListBinding
import com.thikar.githubusers.util.Event
import com.thikar.githubusers.util.Resource
import com.thikar.githubusers.util.onQueryTextSubmit
import com.thikar.githubusers.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserListFragment : Fragment(R.layout.fragment_list) {

    private val viewModel: UserListViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()


    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var listAdapter: UserListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentListBinding.bind(view)

        listAdapter = UserListAdapter(
            onItemClick = {
                sharedViewModel.selectedName(it.name)
                findNavController().navigate(UserListFragmentDirections.actionUserListFragmentToFollowersListFragment())
            }
        )

        binding.apply {
            buttonRetry.setOnClickListener {
                viewModel.currentQueryFlow.value?.let {
                    viewModel.setTrigger()
                }
            }

            recyclerView.apply {
                adapter = listAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.results.collect {
                    val result = it ?: return@collect

                    when (result) {
                        is Resource.Loading -> {
                            progressbar.isVisible = true
                            textViewInstructions.isVisible = false
                        }

                        is Resource.Success -> {
                            val list = result.data
                            progressbar.visibility = View.GONE

                            if (list!!.isNotEmpty()) {
                                listAdapter.submitList(list)
                                recyclerView.visibility = View.VISIBLE
                                textViewInstructions.visibility = View.GONE
                                textViewError.visibility = View.GONE
                                buttonRetry.visibility = View.GONE
                            }else{
                                textViewInstructions.isVisible = true
                                textViewInstructions.text = resources.getString(R.string.no_results_found)
                            }
                        }
                        is Resource.Error -> {
                            recyclerView.isVisible = false
                            progressbar.isVisible = false
                            textViewError.isVisible = true
                            buttonRetry.isVisible = true
                        }
                    }
                }
            }


            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.events.collect { event ->
                    when (event) {
                        is Event.ShowErrorMessage -> {
                            showSnackbar(getString(R.string.could_not_refresh,
                                    event.error.localizedMessage ?: getString(R.string.unknown_error_occurred)
                                )
                            )
                        }
                    }
                }
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.onQueryTextSubmit { query ->
            viewModel.onSearchQuerySubmit(query)
            viewModel.setTrigger()
            searchView.clearFocus()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter = null
        _binding = null
    }
}