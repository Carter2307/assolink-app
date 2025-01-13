package com.assolink.views.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.assolink.R
import com.assolink.viewmodel.MainViewModel

class MapFragment : Fragment() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.associations.observe(viewLifecycleOwner) { associations ->
            view.findViewById<TextView>(R.id.associationCount).text =
                "Nombre d'associations : ${associations.size}"
        }

        viewModel.loadAssociations()
    }
}