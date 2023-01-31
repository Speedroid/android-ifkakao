package com.example.ifkakao.presentation.session.select

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ifkakao.*
import com.example.ifkakao.databinding.FragmentSessionSelectBinding
import com.example.ifkakao.presentation.MainActivity
import com.example.ifkakao.presentation.session.SessionSelectViewModel
import com.example.ifkakao.presentation.session.detail.DetailFragment
import com.example.ifkakao.presentation.session.select.adapter.FilterListAdapter
import com.example.ifkakao.presentation.session.select.adapter.SessionSelectListAdapter
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SessionSelectFragment : Fragment() {
    private var _binding: FragmentSessionSelectBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SessionSelectViewModel by viewModels()

    private var sessionListAdapter = SessionSelectListAdapter(::onSessionItemClick)
    private var typeFilterListAdapter = FilterListAdapter(FILTER_CODE_TYPE, ::onTypeFilterItemClick)
    private var trackFilterListAdapter = FilterListAdapter(FILTER_CODE_TRACK, ::onTrackFilterItemClick)
    private var companyFilterListAdapter = FilterListAdapter(FILTER_CODE_COMPANY, ::onCompanyFilterItemClick)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initialize filter from arguments
        arguments?.getString(ARG_KEY_TYPE)?.let {
            viewModel.filterInfoListByType(it)
        }
        arguments?.getString(ARG_KEY_TRACK)?.let {
            viewModel.filterInfoListByTrack(it)
        }

        // collect state
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    val isTypeFiltered = state.typeSet.isNotEmpty()
                    val isTrackFiltered = state.trackSet.isNotEmpty()
                    val isCompanyFiltered = state.companySet.isNotEmpty()
                    val isFiltered = isTypeFiltered || isTrackFiltered || isCompanyFiltered

                    // set list size text
                    binding.sessionSizeText.text = state.filteredInfoList.size.toString()

                    // set no session text visibility
                    binding.noSessionText.isVisible = state.filteredInfoList.isEmpty()

                    // set filter button tint
                    binding.filterButton.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            if (isFiltered) R.color.blue_primary
                            else R.color.white_title
                        )
                    )

                    // set type filter size text
                    binding.typeFilterSelectedSizeText.text = state.typeSet.size.toString()
                    binding.typeFilterSelectedSizeText.isVisible = isTypeFiltered
                    binding.typeFilterSizeText.text =
                        if (isTypeFiltered) "/" + typeFilterListAdapter.currentList.size.toString()
                        else typeFilterListAdapter.currentList.size.toString()

                    // set track filter size text
                    binding.trackFilterSelectedSizeText.text = state.trackSet.size.toString()
                    binding.trackFilterSelectedSizeText.isVisible = isTrackFiltered
                    binding.trackFilterSizeText.text =
                        if (isTrackFiltered) "/" + trackFilterListAdapter.currentList.size.toString()
                        else trackFilterListAdapter.currentList.size.toString()

                    // set company filter size text
                    binding.companyFilterSelectedSizeText.text = state.companySet.size.toString()
                    binding.companyFilterSelectedSizeText.isVisible = isCompanyFiltered
                    binding.companyFilterSizeText.text =
                        if (isCompanyFiltered) "/" + companyFilterListAdapter.currentList.size.toString()
                        else companyFilterListAdapter.currentList.size.toString()

                    // refresh filter list
                    typeFilterListAdapter.refresh(state.typeSet)
                    trackFilterListAdapter.refresh(state.trackSet)
                    companyFilterListAdapter.refresh(state.companySet)

                    // set reset button tint
                    binding.filterResetImage.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            if (isFiltered) R.color.blue_primary
                            else R.color.gray_content
                        )
                    )
                    binding.filterResetText.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            if (isFiltered) R.color.blue_primary
                            else R.color.gray_content
                        )
                    )

                    // submit filtered info list
                    sessionListAdapter.submitList(state.filteredInfoList)
                }
            }
        }

        // load info list
        viewModel.loadInfoList()

        // set status bar color
        requireActivity().window.statusBarColor =
            requireContext().getColor(R.color.gray_transparent)

        // set action bar color
        (requireActivity() as AppCompatActivity).supportActionBar?.setBackgroundDrawable(
            ColorDrawable(ContextCompat.getColor(requireContext(), R.color.gray_transparent))
        )

        // initialize navigation drawer
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val drawerToggle = object : ActionBarDrawerToggle(
            requireActivity(),
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                (requireActivity() as MainActivity).showToolbar()
            }
        }
        drawerLayout.addDrawerListener(drawerToggle)

        // initialize filter recycler view
        val typeFilterRecyclerView = binding.typeFilterList
        typeFilterRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        typeFilterRecyclerView.adapter = typeFilterListAdapter
        typeFilterRecyclerView.setHasFixedSize(true)
        typeFilterRecyclerView.itemAnimator = null
        typeFilterListAdapter.refresh(viewModel.state.value.typeSet)

        val trackFilterRecyclerView = binding.trackFilterList
        trackFilterRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        trackFilterRecyclerView.adapter = trackFilterListAdapter
        trackFilterRecyclerView.setHasFixedSize(true)
        trackFilterRecyclerView.itemAnimator = null
        trackFilterListAdapter.refresh(viewModel.state.value.trackSet)

        val companyFilterRecyclerView = binding.companyFilterList
        companyFilterRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        companyFilterRecyclerView.adapter = companyFilterListAdapter
        companyFilterRecyclerView.setHasFixedSize(true)
        companyFilterRecyclerView.itemAnimator = null
        companyFilterListAdapter.refresh(viewModel.state.value.companySet)

        // set scroll change listener
        binding.nestedScroll.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            // set up button visibility
            binding.upButton.isVisible = scrollY > binding.nestedScroll.getChildAt(0).height / 5
        }

        // initialize session recycler view
        val sessionRecyclerView = binding.sessionList
        sessionRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2)  // TODO change span dynamic
        sessionRecyclerView.adapter = sessionListAdapter

        // initialize tab layout
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val day = tab?.position ?: 0
                sessionListAdapter.changeDay(day)
                viewModel.filterInfoListByDay(day)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // set click listener
        binding.includeNavHeader.headerTitle.setOnClickListener {
            (requireActivity() as MainActivity).navigateToHome()
            (requireActivity() as MainActivity).showToolbar()
        }
        binding.includeNavHeader.closeButton.setOnClickListener {
            binding.drawerLayout.close()
        }
        binding.scheduleButton.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(URL_SCHEDULE))
            startActivity(browserIntent)
        }
        binding.filterButton.setOnClickListener {
            (requireActivity() as MainActivity).hideToolbar()
            binding.drawerLayout.open()
        }
        binding.filterResetLayout.setOnClickListener {
            viewModel.resetFilter()
        }
        binding.upButton.setOnClickListener {
            binding.nestedScroll.smoothScrollTo(0, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onTypeFilterItemClick(position: Int) {
        val value = when (position) {
            TYPE_POSITION_KEYNOTE -> TYPE_VALUE_KEYNOTE
            TYPE_POSITION_PREVIEW -> TYPE_VALUE_PREVIEW
            TYPE_POSITION_TECH -> TYPE_VALUE_TECH
            else -> ""
        }
        viewModel.filterInfoListByType(value)
    }

    private fun onTrackFilterItemClick(position: Int) {
        val value = when (position) {
            TRACK_POSITION_1015 -> TRACK_VALUE_1015
            TRACK_POSITION_AI -> TRACK_VALUE_AI
            TRACK_POSITION_BACKEND -> TRACK_VALUE_BACKEND
            TRACK_POSITION_FRONTEND -> TRACK_VALUE_FRONTEND
            TRACK_POSITION_MOBILE -> TRACK_VALUE_MOBILE
            TRACK_POSITION_CLOUD -> TRACK_VALUE_CLOUD
            TRACK_POSITION_DATA -> TRACK_VALUE_DATA
            TRACK_POSITION_BLOCK_CHAIN -> TRACK_VALUE_BLOCK_CHAIN
            TRACK_POSITION_DEV_OPS -> TRACK_VALUE_DEV_OPS
            TRACK_POSITION_ESG -> TRACK_VALUE_ESG
            TRACK_POSITION_GENERAL -> TRACK_VALUE_GENERAL
            TRACK_POSITION_CULTURE -> TRACK_VALUE_CULTURE
            else -> ""
        }
        viewModel.filterInfoListByTrack(value)
    }

    private fun onCompanyFilterItemClick(position: Int) {
        val value = when (position) {
            COMPANY_POSITION_KAKAO -> COMPANY_VALUE_KAKAO
            COMPANY_POSITION_KAKAO_PA -> COMPANY_VALUE_KAKAO_PA
            COMPANY_POSITION_KAKAO_EP -> COMPANY_VALUE_KAKAO_EP
            COMPANY_POSITION_KAKAO_M -> COMPANY_VALUE_KAKAO_M
            COMPANY_POSITION_KAKAO_B -> COMPANY_VALUE_KAKAO_B
            COMPANY_POSITION_KAKAO_R -> COMPANY_VALUE_KAKAO_R
            COMPANY_POSITION_KAKAO_G -> COMPANY_VALUE_KAKAO_G
            COMPANY_POSITION_KAKAO_ET -> COMPANY_VALUE_KAKAO_ET
            COMPANY_POSITION_KU -> COMPANY_VALUE_KU
            COMPANY_POSITION_KAKAO_PI -> COMPANY_VALUE_KAKAO_PI
            else -> ""
        }
        viewModel.filterInfoListByCompany(value)
    }

    private fun onSessionItemClick(position: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.session_fragment_container, DetailFragment())
            .addToBackStack(null)
            .commit()
    }
}