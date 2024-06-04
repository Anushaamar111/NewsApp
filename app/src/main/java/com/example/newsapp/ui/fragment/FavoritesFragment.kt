package com.example.newsapp.ui.fragment


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.adapters.AdapterClass
import com.example.newsapp.databinding.FragmentFavoritesBinding
import com.example.newsapp.ui.MainActivity

import com.example.newsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar


class FavoritesFragment : Fragment(R.layout.fragment_favorites) {
    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapterClass: AdapterClass
    lateinit var binding: FragmentFavoritesBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavoritesBinding.bind(view)

        newsViewModel = (activity as MainActivity).newsViewModel
        setUpFavoritesRecycler()

        newsAdapterClass.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }

            findNavController().navigate(R.id.action_favouritesFragment_to_articleFragment)

        }
        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapterClass.differ.currentList[position]
                newsViewModel.deleteNews(article)
                Snackbar.make(view, "Article Removed from Favorites", Snackbar.LENGTH_SHORT).apply {
                    setAction("Undo"){
                        newsViewModel.addAllToFavorites(article)
                    }
                    show()
                }
            }
    }
        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(binding.recyclerFavourites)
        }
        newsViewModel.getFavoriteNews().observe(viewLifecycleOwner, Observer { article->
            newsAdapterClass.differ.submitList(article)
        })
}
    private fun setUpFavoritesRecycler(){
        newsAdapterClass = AdapterClass()
        binding.recyclerFavourites.apply {
            adapter = newsAdapterClass
            layoutManager = LinearLayoutManager(activity)

        }
    }

}