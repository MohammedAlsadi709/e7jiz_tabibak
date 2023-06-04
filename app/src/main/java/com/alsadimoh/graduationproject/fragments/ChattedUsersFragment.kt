package com.alsadimoh.graduationproject.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.chatUtils.adapter.ChatUsersAdapter
import com.alsadimoh.graduationproject.chatUtils.models.ChattedUsers
import com.alsadimoh.graduationproject.chatUtils.models.UserModelForRegister
import com.alsadimoh.graduationproject.databinding.FragmentChattedUsersBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChattedUsersFragment : Fragment(R.layout.fragment_chatted_users) {

    lateinit var binding: FragmentChattedUsersBinding
    private lateinit var data: MutableList<UserModelForRegister>
    private lateinit var chattedUsers: MutableList<ChattedUsers>
    private lateinit var adapter: ChatUsersAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChattedUsersBinding.inflate(inflater, container, false)

        CommonConstants.showProgressDialog(requireActivity())

        val userType  = CommonConstants.myShared.getString(CommonConstants.userType,"user")
        if (userType == "doctor"){
            binding.fab.visibility = View.GONE
        }else{
            binding.fab.visibility = View.VISIBLE
        }

        mAuth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        data = mutableListOf()
        if (data.isNotEmpty()) {
            binding.imgEmptyItems.visibility = View.GONE
        } else {
            binding.imgEmptyItems.visibility = View.VISIBLE
        }
        chattedUsers = mutableListOf()
        adapter = ChatUsersAdapter(requireActivity(), data)
        binding.rvChattedUsers.adapter = adapter
        binding.rvChattedUsers.layoutManager = LinearLayoutManager(requireActivity())

        adapter.onClickChatItem = { model ->
            val action = ChattedUsersFragmentDirections.actionChattedUsersFragmentToChatFragment(
                model.username!!,
                model.userId!!
            )
            findNavController().navigate(action)
        }

        binding.fab.setOnClickListener {
            val action =
                ChattedUsersFragmentDirections.actionChattedUsersFragmentToAllAvailableDoctorsFragment()
            findNavController().navigate(action)
        }

        val userId = mAuth.currentUser?.uid
        db.collection("chattedUsers").whereEqualTo("userId", userId).get()
            .addOnSuccessListener { querySnapshot ->
                data.clear()
                val receiversIds = mutableListOf<String>()
                for (doc in querySnapshot) {
                    val roomId = doc.getString("roomId")
                    val receiverId = doc.getString("receiverId")!!
                    if (roomId!!.endsWith(userId!!)) {
                        chattedUsers.add(
                            ChattedUsers(
                                doc.id,
                                doc.getString("userId")!!,
                                receiverId,
                                doc.getString("lastMessageBody")!!,
                                doc.getString("lastMessageTime")!!,
                                roomId
                            )
                        )
                        receiversIds.add(receiverId)
                    }
                }

                if (receiversIds.isNotEmpty()) {

                    db.collection("registeredUsers").whereIn("userId", receiversIds)
                        .get().addOnSuccessListener { querySnapshot ->
                            for (doc in querySnapshot) {
                                val userId2 = doc.getString("userId")!!
                                for (i in chattedUsers) {
                                    if (i.receiverId == userId2) {
                                        data.add(
                                            UserModelForRegister(
                                                doc.id,
                                                userId2,
                                                doc.getString("username")!!,
                                                doc.getString("email")!!,
                                                doc.getString("image")!!,
                                                i.lastMessageBody,
                                                i.lastMessageTime,
                                                i.roomId,
                                                doc.getString("userType")!!
                                            )
                                        )


                                    }
                                }
                            }
                            adapter.notifyDataSetChanged()
                            CommonConstants.hideProgressDialog()
                            if (data.isNotEmpty()) {
                                binding.imgEmptyItems.visibility = View.GONE
                            } else {
                                binding.imgEmptyItems.visibility = View.VISIBLE
                            }
                        }
                }

            }.addOnFailureListener {
                CommonConstants.hideProgressDialog()
            }.addOnCompleteListener {
                CommonConstants.hideProgressDialog()
            }

        return binding.root
    }
}