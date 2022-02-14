package com.example.phonebook

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phonebook.databinding.FragmentContactListBinding
import com.example.phonebook.models.ContactListAdapter
import com.example.phonebook.models.SwipeToRemoveGesture
import com.example.phonebook.viewmodels.ContactViewModel
import com.google.android.material.snackbar.Snackbar
import java.util.*

class ContactCardList : Fragment() {
    private val contactViewModel: ContactViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentContactListBinding>(inflater,
            R.layout.fragment_contact_list,
            container,
            false)
        // Creates an instance of ContactViewModel adapted for navGraph
        //val contactViewModel: ContactViewModel by navGraphViewModels(R.id.main_nested_graph)
        val adapter = ContactListAdapter(requireContext())

        // When an item in the contact list is clicked we get its index and notify the ViewModel
        adapter.onItemClick = { _, viewHolder ->
            contactViewModel.setContactInfo(viewHolder.absoluteAdapterPosition)
            findNavController().navigate(R.id.action_contactCardList_to_contactInfo)
        }

        val swipeGesture = object : SwipeToRemoveGesture() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                super.onSwiped(viewHolder, direction)
                val tmpContact = adapter.getItem(viewHolder.absoluteAdapterPosition)
                adapter.deleteItem(viewHolder.absoluteAdapterPosition)
                Snackbar.make(binding.contactCardList, "Contact deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") { _ ->
                        adapter.addItem(viewHolder.absoluteAdapterPosition + 1, tmpContact)
                    }
                    .show()
            }
        }
        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(binding.contactCardList)

        // Layout manager and adapter are bound to RecyclerView
        binding.contactCardList.layoutManager = LinearLayoutManager(context)
        binding.contactCardList.adapter = adapter

        // Navigates to AddContact fragment when button is pressed
        binding.addContactButton.setOnClickListener{ view: View ->
            view.findNavController().navigate(R.id.action_contactCardList_to_addContact)
        }

        contactViewModel.contactListModel.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            // Displays msg on screen if cardList is empty
            binding.emptyListTv.visibility = if (it.size == 0) View.VISIBLE else View.GONE
            adapter.updateList(it)

        })

        return binding.root
    }
}