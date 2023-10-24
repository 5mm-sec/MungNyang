package com.example.mungnyang.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mungnyang.Pet.AddPetActivity
import com.example.mungnyang.R
import com.example.mungnyang.databinding.FragmentHomeBinding

class homeFragment : Fragment() {
    //바인등을 사용하기위해 선언
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Enable options menu
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu, menu) // Inflate your menu layout (e.g., menu_main.xml)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item1 -> {
                // Handle menu item 1 click
                // Add your code here
                return true
            }
            R.id.menu_item2 -> {
                // Handle menu item 2 click
                // Add your code here
                return true
            }
            // Add more menu item cases if needed
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val intent = Intent(getActivity(), AddPetActivity::class.java) //프래그먼트에서 인텐트
        //바인딩을 사용해서 버튼 이벤트 처리
        binding.addbtn2.setOnClickListener{
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
    }
}