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
import com.alsadimoh.graduationproject.chatUtils.models.UserModelForRegister
import com.alsadimoh.graduationproject.databinding.FragmentAllAvailableDoctorsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AllAvailableDoctorsFragment : Fragment(R.layout.fragment_all_available_doctors) {

    lateinit var binding: FragmentAllAvailableDoctorsBinding
    private lateinit var db: FirebaseFirestore
    lateinit var data: MutableList<UserModelForRegister>
    lateinit var adapter: ChatUsersAdapter
    private lateinit var mAuth: FirebaseAuth

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAllAvailableDoctorsBinding.inflate(inflater, container, false)

        CommonConstants.showProgressDialog(requireActivity())

        db = Firebase.firestore
        mAuth = FirebaseAuth.getInstance()

        data = mutableListOf()
        adapter = ChatUsersAdapter(requireActivity(), data)
        binding.rvAllAvailableDoctors.adapter = adapter
        binding.rvAllAvailableDoctors.layoutManager = LinearLayoutManager(requireActivity())

        if (adapter.data.isNotEmpty()){
            binding.layoutEmpty.visibility = View.GONE
        }else{
           binding.layoutEmpty.visibility = View.VISIBLE
        }

        adapter.onClickChatItem = { model ->
            val action =
                AllAvailableDoctorsFragmentDirections.actionAllAvailableDoctorsFragmentToChatFragment(
                    model.username!!,
                    model.userId!!
                )
            findNavController().navigate(action)
        }

        db.collection("registeredUsers").get().addOnSuccessListener { querySnapshot ->
            for (doc in querySnapshot) {

                val userId = doc.getString("userId")!!
                val userType = doc.getString("userType")!!
                if (mAuth.currentUser?.uid != userId && userType == "doctor") {
                    data.add(
                        UserModelForRegister(
                            doc.id,
                            userId,
                            doc.getString("username")!!,
                            doc.getString("email")!!,
                            doc.getString("image")!!,
                            doc.getString("lastMessageBody")!!,
                            null /*doc.getString("lastMessageTime")!!*/,
                            doc.getString("roomId"),
                            userType
                        )
                    )
                }
            }
            adapter.notifyDataSetChanged()
            if (adapter.data.isNotEmpty()){
                binding.layoutEmpty.visibility= View.GONE
            }else{
                binding.layoutEmpty.visibility= View.VISIBLE
            }
            CommonConstants.hideProgressDialog()
        }.addOnFailureListener {
            CommonConstants.showCustomToast(requireActivity(),  "حدث خطأ ما!","error")
        }

        return binding.root
    }
}