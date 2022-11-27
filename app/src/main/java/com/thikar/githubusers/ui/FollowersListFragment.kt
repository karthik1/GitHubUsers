package com.thikar.githubusers.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.thikar.githubusers.R
import com.thikar.githubusers.adapter.UserListAdapter
import com.thikar.githubusers.databinding.FragmentListBinding
import com.thikar.githubusers.util.Event
import com.thikar.githubusers.util.Resource
import com.thikar.githubusers.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FollowersListFragment : Fragment(R.layout.fragment_list) {

    private val viewModel: FollowersListViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var listAdapter: UserListAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentListBinding.bind(view)
        listAdapter = UserListAdapter { }

        sharedViewModel.selectedName.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.setSelectedUserName(it)
                viewModel.setTrigger()
            }
        }

        binding.apply {

            buttonRetry.setOnClickListener {
                sharedViewModel.selectedName.value?.let {
                    viewModel.setTrigger()
                }
            }

            recyclerView.apply {
                adapter = listAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.followersList.collect {
                    val result = it ?: return@collect

                    if (result is Resource.Loading) {
                        progressbar.isVisible = true
                        textViewInstructions.isVisible = false
                    } else if (result is Resource.Success){
                        val list = result.data
                        listAdapter.submitList(list)
                        recyclerView.visibility = View.VISIBLE
                        textViewInstructions.visibility = View.GONE
                        progressbar.visibility = View.GONE
                        textViewError.visibility = View.GONE
                        buttonRetry.visibility = View.GONE
                    } else{
                        recyclerView.isVisible = false
                        progressbar.isVisible = false
                        textViewError.isVisible = true
                        buttonRetry.isVisible = true
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.events.collect { event ->
                    when (event) {
                        is Event.ShowErrorMessage -> {
                            textViewError.visibility = View.VISIBLE
                            buttonRetry.visibility = View.VISIBLE

                            showSnackbar(getString(R.string.could_not_refresh,
                                event.error.localizedMessage ?: getString(R.string.unknown_error_occurred)
                            )
                            )
                        }
                    }
                }
            }
        }
    }
}